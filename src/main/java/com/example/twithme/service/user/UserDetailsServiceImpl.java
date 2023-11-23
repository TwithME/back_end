package com.example.twithme.service.user;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.model.dto.user.UserRes;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String id) {
        UserRes.RoleDto roleDto = getUserRoleById(Long.valueOf(id));
        return new UserDetailsImpl(roleDto);
    }

    public UserRes.RoleDto getUserRoleById(Long id) {
        User user;
        try {
            user = userRepository.findById(id).get();
        }
        catch(NoSuchElementException e) {
            throw new BadRequestException("존재하지 않는 유저입니다.");
        }
        List<String> userRole = new ArrayList<>();
        userRole.add(user.getUserRole());
        return UserRes.RoleDto.builder()
                .id(id)
                //.password(user.getPassword())
                .roles(userRole)
                .build();
    }

}