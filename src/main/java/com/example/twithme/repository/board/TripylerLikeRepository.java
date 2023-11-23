package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Tripyler;
import com.example.twithme.model.entity.board.TripylerLike;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripylerLikeRepository extends JpaRepository<TripylerLike, Long> {

    TripylerLike findByTripylerAndUser(Tripyler tripyler, User user);
    int countByTripyler(Tripyler tripyler);
    List<TripylerLike> findByUser(User user);

    List<TripylerLike> findByTripyler(Tripyler tripyler);

    //게시물 필터링
    @Query(value = "SELECT tripyler_id as tripylerId, COUNT(tripyler_id) as cnt FROM tripyler_like GROUP BY tripyler_id ORDER BY cnt DESC",
            nativeQuery = true)
    List<TripylerLikeCount> countTripylerId();

    interface TripylerLikeCount{
        Long getTripylerId();
        int getCnt();
    }


    @Query(value = "SELECT tripyler_id as tripylerId, COUNT(tripyler_id) as cnt FROM tripyler_like\n" +
            "                                                            join tripyler t on t.id = tripyler_like.tripyler_id\n" +
            "                                                            where t.is_recruiting = :isRecruiting\n" +
            "                                                            GROUP BY tripyler_id ORDER BY cnt DESC",
            nativeQuery = true)
    List<TripylerLikeCountWhereIsRecruiting> countTripylerIdWhereIsRecruiting(int isRecruiting);

    interface TripylerLikeCountWhereIsRecruiting{
        Long getTripylerId();
        int getCnt();
    }


}
