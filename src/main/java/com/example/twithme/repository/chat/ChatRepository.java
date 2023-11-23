package com.example.twithme.repository.chat;

import com.example.twithme.model.entity.chat.Chat;
import com.example.twithme.model.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatRoom(ChatRoom chatRoom);
}
