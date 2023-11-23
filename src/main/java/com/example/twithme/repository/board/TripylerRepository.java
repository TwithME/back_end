package com.example.twithme.repository.board;

import com.example.twithme.model.entity.board.Tripyler;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripylerRepository extends JpaRepository<Tripyler, Long> {

    List<Tripyler> findAll();

    List<Tripyler> findTripylersById(Long tripylerId);

    Tripyler findTripylerById(Long tripylerId);

    List<Tripyler> findByContinentIdAndNationIdAndRegionId(Long continentId, Long nationId, Long regionId);

    List<Tripyler> findByContinentIdAndNationId(Long continentId, Long nationId);

    List<Tripyler> findByContinentId(Long continentId);

    List<Tripyler> findByWriterId(Long userId);

    List<Tripyler> findByWriter(User writer);

    //최신순
    List<Tripyler> findAllByIsRecruitingOrderByRegDateTimeDesc(int isRecruiting);

    //Tripyler findByTripylerId(Long tripylerId);



    //댓글순

    //조회수순 -> hit 내림차순
    List<Tripyler> findAllByIsRecruitingOrderByHitsDesc(int isRecruiting);

    @Modifying
    @Query(value = "update tripyler set hits = hits + 1 where id = :id",
            nativeQuery = true)
    void incrementHits(Long id);
    Tripyler findByWriterAndId(User user, Long tripylerId);

    @Query(value = "select * from tripyler where writer_id = :userId and YEAR(start_dt) = :year",
            nativeQuery = true)
    List<Tripyler> findByYearAndUserId(int year, Long userId);
}
