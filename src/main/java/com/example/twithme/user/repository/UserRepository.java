package com.example.twithme.user.repository;

import com.example.twithme.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findUserWithAuthoritiesByKakaoId(Long kakaoId);

    //Optional<User> findUserByKakaoId(Long kakaoId);


    Optional<User> findUserById(Long userId);


    User findByKakaoIdAndSocial(Long kakaoId, String social);

    boolean existsByKakaoIdAndSocial(Long kakaoId, String social);

    boolean existsByKakaoId(Long kakaoId);
}

