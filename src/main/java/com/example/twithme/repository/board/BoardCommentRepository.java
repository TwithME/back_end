package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Board;
import com.example.twithme.model.entity.board.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    int countByBoard(Board board);


    List<BoardComment> findAllByBoardOrderByRegDateTimeDesc(Board board);
}
