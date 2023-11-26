package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Board;
import com.example.twithme.model.entity.board.BoardLike;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    BoardLike findByBoardAndUser(Board board, User user);
    int countByBoard(Board board);
    List<BoardLike> findByUser(User user);

    List<BoardLike> findByBoard(Board board);

    //게시물 필터링
    @Query(value = "SELECT board_id as boardId, COUNT(board_id) as cnt FROM board_like GROUP BY board_id ORDER BY cnt DESC",
            nativeQuery = true)
    List<BoardLikeCount> countBoardId();

    interface BoardLikeCount {
        Long getBoardId();
        int getCnt();
    }


    @Query(value = "SELECT board_id as boardId, COUNT(board_id) as cnt FROM board_like\n" +
            "                                                            join board b on b.id = board_like.board_id\n" +
            "                                                            where b.is_recruiting = :isRecruiting\n" +
            "                                                            GROUP BY board_id ORDER BY cnt DESC",
            nativeQuery = true)
    List<BoardLikeCountWhereIsRecruiting> countBoardIdWhereIsRecruiting(int isRecruiting);

    interface BoardLikeCountWhereIsRecruiting {
        Long getBoardId();
        int getCnt();
    }


}
