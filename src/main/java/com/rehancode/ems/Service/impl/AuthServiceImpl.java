package com.rehancode.ems.Service.impl;

import com.rehancode.ems.Config.DetailsService.UserPrinicple;
import com.rehancode.ems.Config.Jwt.JwtService;
import com.rehancode.ems.Dto.LoginRequestDTO;
import com.rehancode.ems.Dto.LoginResponseDTO;
import com.rehancode.ems.Dto.MapStruct.LoginUserMapper;
import com.rehancode.ems.Exception.ApiResponse;
import com.rehancode.ems.Exception.BadCredentials;
import com.rehancode.ems.Model.BlackListedToken;
import com.rehancode.ems.Model.UsersModel;
import com.rehancode.ems.Repository.BlackListedTokenRepository;
import com.rehancode.ems.Service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private BlackListedTokenRepository blackListedTokenRepository;
    private LoginUserMapper mapper;
    private JwtService jwtService;
    public AuthServiceImpl(BlackListedTokenRepository blackListedTokenRepository,LoginUserMapper mapper,JwtService jwtService,AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
        this.blackListedTokenRepository=blackListedTokenRepository;
        this.mapper=mapper;
    }
    @Override
    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        log.info("Login attempt for username='{}'", loginRequestDTO.getUsername());
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),loginRequestDTO.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed for username='{}' – bad credentials", loginRequestDTO.getUsername());
            throw new BadCredentials("Invalid Credentials");
        }
        UserPrinicple userPrinicple= (UserPrinicple) authentication.getPrincipal();
        UsersModel usersModel=userPrinicple.getUser();
        String token=jwtService.generateToken(usersModel);
        LoginResponseDTO response=mapper.mapToDto(usersModel);
        response.setToken(token);
        log.info("Login successful for username='{}' role='{}'", usersModel.getUsername(), usersModel.getRole());

        return ApiResponse.<LoginResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Login Success")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<String> logout(String token) {
        log.info("Logout attempt for username='{}'", token);
      String jti=jwtService.extractAllClaims(token).get("jti").toString();
      Date exp=jwtService.extractAllClaims(token).getExpiration();
      blackListenToken(jti,exp);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Logout Success")
                .data("Success")
                .build();

    }

    private void blackListenToken(String jti, Date exp) {
        BlackListedToken entity=new BlackListedToken();
        entity.setJti(jti);
        entity.setExpiry(exp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        blackListedTokenRepository.save(entity);
    }

    @Scheduled(cron = "0 */5 * * * ?")
    private void deleteBlackListedToken() {
        blackListedTokenRepository.deleteByExpiryBefore(LocalDateTime.now());

    }
}
