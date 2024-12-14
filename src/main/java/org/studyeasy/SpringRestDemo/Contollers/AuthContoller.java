package org.studyeasy.SpringRestDemo.Contollers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import org.studyeasy.SpringRestDemo.payload.auth.TokenDTO;
import org.studyeasy.SpringRestDemo.payload.auth.UserLoginDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Controller for user management!")
@Slf4j
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
    @Operation(summary = "List of users api")
    public ResponseEntity<List<AccountViewDTO>> users() {
        List<AccountViewDTO> accountViews = accountService.findAll()
                                            .stream()
                                            .map((account) -> new AccountViewDTO(account.getId(),account.getEmail(),account.getRole()))
                                            .collect(Collectors.toList());
        
        return ResponseEntity.ok(accountViews);
    }

}
