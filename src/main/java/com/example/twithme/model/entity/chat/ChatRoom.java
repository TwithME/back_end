package com.example.twithme.model.entity.chat;

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
@Table(name = "chat_room")
@Where(clause = "delete_yn = 0")
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user0_id")
    private User userZero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private User userOne;

    @Column(name = "delete_yn")
    private boolean deleteYn;
}
