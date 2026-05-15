package com.rehancode.ems.Config.DetailsService;

import com.rehancode.ems.Exception.UserNotExists;
import com.rehancode.ems.Model.UsersModel;
import com.rehancode.ems.Repository.UserRepository;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepo;
    public CustomUserDetailsService(UserRepository userRepo){
        this.userRepo=userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersModel user=userRepo.findByUsername(username);
        if(user==null){
            throw new UserNotExists("No user exists");
        }
        return new UserPrinicple(user);
    }
}
