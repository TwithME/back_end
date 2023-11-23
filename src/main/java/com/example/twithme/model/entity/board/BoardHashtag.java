package com.example.twithme.model.entity.board;

import com.example.twithme.common.model.BaseTimeEntity;
import com.example.twithme.model.entity.hashtag.Hashtag;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tripyler_hashtag")
@Where(clause = "delete_yn = 0")
public class BoardHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Column(name = "delete_yn")
    private boolean deleteYn;
}
