package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Review;
import com.example.twithme.model.entity.board.ReviewLike;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    ReviewLike findByReviewAndUser(Review review, User user);

    List<ReviewLike> findByUser(User user);

    List<ReviewLike> findByReview(Review review);

    int countByReview(Review review);
}
