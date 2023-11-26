package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Board;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAll();

    Board findBoardById(Long boardId);

    List<Board> findByContinentIdAndNationIdAndRegionId(Long continentId, Long nationId, Long regionId);

    List<Board> findByContinentIdAndNationId(Long continentId, Long nationId);

    List<Board> findByContinentId(Long continentId);

    List<Board> findByWriterId(Long userId);

    List<Board> findByWriter(User writer);

    //최신순
    List<Board> findAllByIsRecruitingOrderByRegDateTimeDesc(int isRecruiting);


    //댓글순

    //조회수순 -> hit 내림차순
    List<Board> findAllByIsRecruitingOrderByHitsDesc(int isRecruiting);

    @Modifying
    @Query(value = "update board set hits = hits + 1 where id = :id",
            nativeQuery = true)
    void incrementHits(Long id);
    Board findByWriterAndId(User user, Long boardId);

    @Query(value = "select * from board where writer_id = :userId and YEAR(start_dt) = :year",
            nativeQuery = true)
    List<Board> findByYearAndUserId(int year, Long userId);
}
