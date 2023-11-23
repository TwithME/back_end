package com.example.twithme.model.entity.board;

import com.example.twithme.common.model.BaseTimeEntity;
import com.example.twithme.model.entity.destination.Continent;
import com.example.twithme.model.entity.destination.Nation;
import com.example.twithme.model.entity.destination.Region;
import com.example.twithme.model.entity.user.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tripyler")
@Where(clause = "delete_yn = 0")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "continent_id")
    private Continent continent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(name = "start_dt")
    private LocalDate startDate;

    @Column(name = "end_dt")
    private LocalDate endDate;

    @Column(name = "recruit_people_num")
    private int recruitPeopleNum;

    @Column(name = "total_people_num")
    private int totalPeopleNum;

    private String title;

    private String content;

    private int hits;

    @Column(name = "is_recruiting")
    private int isRecruiting;

    @Column(name = "estimated_price")
    private int estimatedPrice;

    private String image;

    @Column(name = "delete_yn")
    private boolean deleteYn;
}