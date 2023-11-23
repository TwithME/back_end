package com.example.twithme.repository.user;

import com.example.twithme.model.entity.hashtag.Hashtag;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.model.entity.user.UserHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHashtagRepository extends JpaRepository<UserHashtag, Long> {

    int countByHashtag(Hashtag hashtag);

    @Query(value = "SELECT *, COUNT(*) as cnt FROM user_hashtag GROUP BY hashtag_id ORDER BY cnt DESC, hashtag_id ASC",
            nativeQuery = true)
    List<UserHashtag> findHashtagIdByCount();

    List<UserHashtag> findByUser(User user);

    UserHashtag findByUserAndHashtag_Id(User user, Long hashtagId);
}
