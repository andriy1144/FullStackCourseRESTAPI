package org.studyeasy.SpringRestDemo.Sevice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringRestDemo.Entities.Account;
import org.studyeasy.SpringRestDemo.Repositories.AccountRepo;
import org.studyeasy.SpringRestDemo.Util.Constants.Authority;

@Service
public class AccountService implements UserDetailsService{
    @Autowired
    private AccountRepo accountRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account saveUser(Account account){
        account.setPassword( passwordEncoder.encode(account.getPassword()) );
        if(account.getAuthorities() == null) account.setAuthorities(Authority.USER.toString());

        return accountRepo.save(account);
    }

    public List<Account> findAll(){
        return accountRepo.findAll();
    }

    public Optional<Account> findByEmail(String email){
        return accountRepo.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> user = accountRepo.findByEmail(username);

        if(user.isPresent()){
            Account foundUser = user.get();
            return new org.springframework.security.core.userdetails.User(
                foundUser.getEmail(),
                foundUser.getPassword(),
                List.of(new SimpleGrantedAuthority(foundUser.getAuthorities())));
        }else{
            throw new UsernameNotFoundException("User with username " + username + " wasn't found");
        }
    }
    
}
