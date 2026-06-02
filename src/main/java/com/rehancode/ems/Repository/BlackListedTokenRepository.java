package com.rehancode.ems.Repository;

import com.rehancode.ems.Model.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, Long> {
    boolean existsByJti(String jti);

    void deleteByExpiryBefore(LocalDateTime expiry);


}
