package com.example.twithme.model.entity.user;

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
@Table(name = "user_hashtag")
@Where(clause = "delete_yn = 0")
public class UserHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    // DB Table에는 존재하지 않는 컬럼이지만, JPA에서는 해당 컬럼을 사용하고 싶을 때 적용하는 어노테이션
    @Transient
    private int cnt;

    @Column(name = "delete_yn")
    private boolean deleteYn;
}
