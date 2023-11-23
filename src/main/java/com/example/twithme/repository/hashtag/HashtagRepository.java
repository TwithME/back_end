package com.example.twithme.repository.hashtag;

import com.example.twithme.model.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    boolean existsByName(String name);
    List<Hashtag> findByNameContains(String name);
    Hashtag findByName(String name);
}
