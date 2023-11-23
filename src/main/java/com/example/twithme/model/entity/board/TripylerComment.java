package com.example.twithme.model.entity.board;

import com.example.twithme.common.model.BaseTimeEntity;
import com.example.twithme.model.entity.user.User;
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
@Table(name = "tripyler_comment")
@Where(clause = "delete_yn = 0")
public class TripylerComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tripyler_id")
    private Tripyler tripyler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id")
    private User commenter;

    private String content;

    @Column(name = "delete_yn")
    private boolean deleteYn;
}