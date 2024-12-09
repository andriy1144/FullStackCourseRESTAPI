package org.studyeasy.SpringRestDemo.Security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.springframework.stereotype.Component;

/*
 * Генератор RSA ключів для авторизації
 */
@Component
final class KeyGenerator {
    
    private KeyGenerator(){}

    static KeyPair generateRsaKey(){
        KeyPair keyPair;
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();            
        }catch(Exception ex){
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
