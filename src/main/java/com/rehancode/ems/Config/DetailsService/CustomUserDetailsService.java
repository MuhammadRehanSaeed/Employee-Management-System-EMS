package com.rehancode.ems.Config.DetailsService;

import com.rehancode.ems.Model.UsersModel;
import com.rehancode.ems.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepo;
    public CustomUserDetailsService(UserRepository userRepo){
        this.userRepo=userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username='{}'", username);
        UsersModel user = userRepo.findByUsername(username);

        if (user == null) {
            log.warn("User not found in DB username='{}'", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        log.debug("User loaded successfully username='{}' role='{}' active='{}'",
                username, user.getRole(), user.isActive());
        return new UserPrinicple(user);
    }
}
