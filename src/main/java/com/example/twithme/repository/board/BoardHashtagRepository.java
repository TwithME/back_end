package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Board;
import com.example.twithme.model.entity.board.BoardHashtag;
import com.example.twithme.model.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardHashtagRepository extends JpaRepository<BoardHashtag, Long> {

    List<BoardHashtag> findByHashtag(Hashtag hashtag);

    List<BoardHashtag> findByTripyler(Board board);

    BoardHashtag findByTripylerAndHashtag_Id(Board board, Long hashtagId);

}
