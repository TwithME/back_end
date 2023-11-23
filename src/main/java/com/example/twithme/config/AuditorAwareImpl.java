package com.example.twithme.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String userName = authentication.getName();
        long userId;
        try {
            userId = Long.parseLong(userName);
        }
        catch(NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(userId);
    }
}
