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

    @Column(name = "sns_id")
    private String snsId; //ok

    @Column(name = "sns_token")
    private String snsToken; //ok

    @Column(name = "login_type")
    private String loginType; //ok

    @Column(name = "profile_url")
    private String profileUrl; //ok

    private String name; //TODO 닉네임으로 이름 넣기

    @Column(name = "first_login")
    private boolean firstLogin; //ok

    @Column(name = "user_role")
    private String userRole; //ok


    //추가 입력 받을 정보
    private String gender;

    private String phone;

    private String instagram;

    @Column(name = "birth_date")
    private LocalDate birthDate;


    private String username; //필요없음
    private String nickname; //필요없음



    //추후 마이페이지에서 수정할 내용
    @Column(name = "bio_1")
    private String firstBio;

    @Column(name = "bio_2")
    private String secondBio;

    @Column(name = "bio_3")
    private String thirdBio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private Mbti mbti;


    @Column(name = "delete_yn")
    private boolean deleteYn;

}
