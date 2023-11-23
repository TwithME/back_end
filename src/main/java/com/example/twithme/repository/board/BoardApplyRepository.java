package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Board;
import com.example.twithme.model.entity.board.BoardApply;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardApplyRepository extends JpaRepository<BoardApply, Long> {

    List<BoardApply> findByBoardId(Long boardId); // 트리플러 아이디로 신청된 내역들 불러오기


    BoardApply findTripylerApplyById(Long id);
    List<BoardApply> findByBoard(Board board);

    List<BoardApply> findByBoardAndAccepted(Board board, int accepted);


    List<BoardApply> findByApplicant(User applicant);

    List<BoardApply> findByApplicantAndAcceptedEquals(User applicant, int accepted);

}
