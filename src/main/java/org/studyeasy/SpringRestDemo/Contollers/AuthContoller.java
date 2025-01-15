package org.studyeasy.SpringRestDemo.Contollers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestDemo.Entities.Account;
import org.studyeasy.SpringRestDemo.Sevice.AccountService;
import org.studyeasy.SpringRestDemo.Sevice.TokenService;
import org.studyeasy.SpringRestDemo.Util.Constants.AccountError;
import org.studyeasy.SpringRestDemo.Util.Constants.AccountSuccess;
import org.studyeasy.SpringRestDemo.payload.auth.AccountDTO;
import org.studyeasy.SpringRestDemo.payload.auth.AccountViewDTO;
import org.studyeasy.SpringRestDemo.payload.auth.AuthoritiesDTO;
import org.studyeasy.SpringRestDemo.payload.auth.PasswordChangeDTO;
import org.studyeasy.SpringRestDemo.payload.auth.ProfileDTO;
import org.studyeasy.SpringRestDemo.payload.auth.TokenDTO;
import org.studyeasy.SpringRestDemo.payload.auth.UserLoginDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Controller for user management!")
@Slf4j
@CrossOrigin(originPatterns = "http://localhost:3000",maxAge = 3600) //Allows to comunicate with backend
public class AuthContoller {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private TokenService tokenService;

    //Mapping to return token
    @PostMapping("/token")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) {
        try {
            log.debug("/auth/token : Username: {}", userLogin.getEmail());
            log.debug("/auth/token : Password: {}", userLogin.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword())
            );
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
        } catch (Exception e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/users/add", consumes = "application/json", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new user")
    @ApiResponse(responseCode = "401", description = "Please enter a valid email and password length between 6 and 20 characters")
    @ApiResponse(responseCode = "200", description = "Account added")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody AccountDTO accountDTO) {
        try{
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());

            accountService.saveUser(account);

            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
        }catch(Exception e){
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @GetMapping(value = "/users", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of users!")
    @ApiResponse(responseCode = "401", description = "Token missing!")
    @ApiResponse(responseCode = "403", description = "Token error!")
    @Operation(summary = "List of users api")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<List<AccountViewDTO>> users() {
        List<AccountViewDTO> accountViews = accountService.findAll()
                                            .stream()
                                            .map((account) -> new AccountViewDTO(account.getId(),account.getEmail(),account.getAuthorities()))
                                            .collect(Collectors.toList());
        
        return ResponseEntity.ok(accountViews);
    }


    @PutMapping(value = "/users/{userId}/update-authorities", consumes = "application/json",produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Authorities updated!")
    @ApiResponse(responseCode = "401", description = "Token missing!")
    @ApiResponse(responseCode = "403", description = "Token error!")
    @Operation(summary = "Update authorities!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<AccountViewDTO> updateAuth(@Valid @RequestBody AuthoritiesDTO authoritiesDTO, @PathVariable(name = "userId") Long userId) {
        Optional<Account> account = accountService.findUserById(userId);
        if(account.isPresent()){
            Account accountToChange = account.get();
            accountToChange.setAuthorities(authoritiesDTO.getAuthorities());
            accountService.saveUser(accountToChange);

            return ResponseEntity.ok(new AccountViewDTO(accountToChange.getId(), accountToChange.getEmail(), accountToChange.getAuthorities()));
        }else{
            return new ResponseEntity<>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "View profile!")
    @ApiResponse(responseCode = "401", description = "Token missing!")
    @ApiResponse(responseCode = "403", description = "Token error!")
    @Operation(summary = "View profile!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<ProfileDTO> profile(Authentication authentication) {
        String email = authentication.getName();

        Optional<Account> account = accountService.findByEmail(email);
        if(account.isPresent()){
            Account accountToView = account.get();
            ProfileDTO profile = new ProfileDTO(accountToView.getId(), accountToView.getEmail(),accountToView.getAuthorities());
            return ResponseEntity.ok(profile);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/profile/update-password", consumes = "application/json",produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Update profile!")
    @ApiResponse(responseCode = "401", description = "Token missing!")
    @ApiResponse(responseCode = "403", description = "Token error!")
    @Operation(summary = "Update profile!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<AccountViewDTO> updatePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO, Authentication authentication) {
        String email = authentication.getName();

        Optional<Account> account = accountService.findByEmail(email);
        if(account.isPresent()){
            Account accountToChange = account.get();
            accountToChange.setPassword(passwordChangeDTO.getPassword());
            accountService.saveUser(accountToChange);

            return ResponseEntity.ok(new AccountViewDTO(accountToChange.getId(), accountToChange.getEmail(), accountToChange.getAuthorities()));
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping(value = "/profile/{userId}/delete")
    @ApiResponse(responseCode = "200", description = "Delete profile!")
    @ApiResponse(responseCode = "401", description = "Token missing!")
    @ApiResponse(responseCode = "403", description = "Token error!")
    @Operation(summary = "Delete profile!")
    @SecurityRequirement(name = "studyeasy-demo-api")
    public ResponseEntity<String> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> account = accountService.findByEmail(email);
        if(account.isPresent()){
            accountService.deleteById(account.get().getId());

            return ResponseEntity.ok("User deleted!");
        }

        return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }
}
