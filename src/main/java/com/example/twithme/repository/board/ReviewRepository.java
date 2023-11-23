package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Review;
import com.example.twithme.model.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBoard(Board board);
    @Modifying
    @Query(value = "update review set hits = hits + 1 where id = :id",
            nativeQuery = true)
    void incrementHits(Long id);


    List<Review> findAll();

    @Query(value = "select review.id as id, tripyler_id, review.writer_id as writer_id, review.title as title, review.hits as hits, review.content as content, " +
            "review.one_line as one_line, review.mod_dt as mod_dt, review.mod_user_id as mod_user_id, review.reg_user_id as reg_user_id, review.reg_dt as reg_dt, review.delete_yn as delete_yn " +
            "from review join tripyler on review.tripyler_id = tripyler.id " +
            "where review.writer_id = :userId and YEAR(tripyler.start_dt) = :year",
            nativeQuery = true)
    List<Review> findByYearAndUserId(int year, Long userId);

}
