package com.example.twithme.model.entity.user;

import com.example.twithme.common.model.BaseTimeEntity;
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
@Table(name = "user")
@Where(clause = "delete_yn = 0")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; //TODO 카카오 아이디

    private String name; //TODO 닉네임으로 이름 넣기

    private String gender;

    private String phone;

    private String nickname;

    private String instagram;

    @Column(name = "bio_1")
    private String firstBio;

    @Column(name = "bio_2")
    private String secondBio;

    @Column(name = "bio_3")
    private String thirdBio;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "login_type")
    private String loginType;

    @Column(name = "profile_url")
    private String profileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private Mbti mbti;

    @Column(name = "first_login")
    private boolean firstLogin;

    @Column(name = "sns_id")
    private String snsId;

    @Column(name = "sns_token")
    private String snsToken;

    @Column(name = "delete_yn")
    private boolean deleteYn;

}
