package org.studyeasy.SpringRestDemo.Contollers;

import org.springframework.web.bind.annotation.RestController;
import org.studyeasy.SpringRestDemo.Sevice.TokenService;
import org.studyeasy.SpringRestDemo.payload.auth.Token;
import org.studyeasy.SpringRestDemo.payload.auth.UserLogin;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@RestController
public class AuthContoller {
    
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthContoller(AuthenticationManager authenticationManager, TokenService tokenService){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    //Mapping to return token
    @PostMapping("/token")
    @ResponseBody
    public Token token(@RequestBody UserLogin userLogin) {
        Authentication authentication = 
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));
        
        return new Token(tokenService.generateToken(authentication));
    }

}
