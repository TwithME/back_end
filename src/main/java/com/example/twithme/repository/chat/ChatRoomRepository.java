package com.example.twithme.repository.chat;

import com.example.twithme.model.entity.chat.ChatRoom;
import com.example.twithme.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUserZero(User user);
    List<ChatRoom> findByUserOne(User user);
    ChatRoom findByUserZeroAndUserOne(User userZero, User userOne);
}
