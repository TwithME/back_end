package com.example.twithme.service.chat;

import com.example.twithme.common.exception.NotFoundException;
import com.example.twithme.common.exception.ServerErrorException;
import com.example.twithme.common.exception.UnauthorizedException;
import com.example.twithme.model.dto.chat.ChatRes;
import com.example.twithme.model.entity.chat.Chat;
import com.example.twithme.model.entity.chat.ChatRoom;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.repository.chat.ChatRepository;
import com.example.twithme.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoom getChatRoomId(User sender, User recipient) {
        List<ChatRoom> chatRoomListZero = chatRoomRepository.findByUserZero(sender);
        for(ChatRoom chatRoom : chatRoomListZero) {
            if(chatRoom.getUserOne().equals(recipient)) {
                return chatRoom;
            }
        }

        List<ChatRoom> chatRoomListOne = chatRoomRepository.findByUserZero(recipient);
        for(ChatRoom chatRoom : chatRoomListOne) {
            if(chatRoom.getUserOne().equals(sender)) {
                return chatRoom;
            }
        }

        return null;
    }

    public List<ChatRes.ChatRoomDto> getChatRoomList(User user) {
        List<ChatRes.ChatRoomDto> chatRoomDtoList = new ArrayList<>();
        List<ChatRoom> chatRoomListZero = chatRoomRepository.findByUserZero(user);
        for(ChatRoom chatRoom : chatRoomListZero) {
            User otherUser = chatRoom.getUserOne();
            chatRoomDtoList.add(ChatRes.ChatRoomDto.builder()
                            .chatRoomId(chatRoom.getId())
                            .recipientId(otherUser.getId())
                            .name(otherUser.getNickname())
                            .profileUrl(otherUser.getProfileUrl())
                            .build());
        }

        List<ChatRoom> chatRoomListOne = chatRoomRepository.findByUserOne(user);
        for(ChatRoom chatRoom : chatRoomListOne) {
            User otherUser = chatRoom.getUserZero();
            chatRoomDtoList.add(ChatRes.ChatRoomDto.builder()
                            .chatRoomId(chatRoom.getId())
                            .recipientId(otherUser.getId())
                            .name(otherUser.getNickname())
                            .profileUrl(otherUser.getProfileUrl())
                            .build());
        }
        return chatRoomDtoList;
    }

    @Transactional
    public ChatRoom createChatRoom(User sender, User recipient) {
        ChatRoom chatRoom = ChatRoom.builder()
                .userZero(sender)
                .userOne(recipient)
                .build();
        chatRoomRepository.save(chatRoom);
        Optional<ChatRoom> optionalChatRoom = Optional.ofNullable(chatRoomRepository.findByUserZeroAndUserOne(sender, recipient));
        if(optionalChatRoom.isEmpty()) {
            throw new ServerErrorException();
        }

        return optionalChatRoom.get();
    }

    @Transactional
    public void writeChat(ChatRoom chatRoom, User sender, User recipient, String content){
        boolean senderDetermineCode = chatRoom.getUserOne().equals(sender);
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .content(content)
                .senderDetermineCode(senderDetermineCode)
                .build();

        chatRepository.save(chat);
    }

    @Transactional
    public boolean userCheck(Long chatRoomId, User user) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if(optionalChatRoom.isEmpty()) {
            throw new NotFoundException("존재하지 않는 채팅방입니다.");
        }
        ChatRoom chatRoom = optionalChatRoom.get();
        if(chatRoom.getUserZero().equals(user)) {
            return true;
        }
        else if(chatRoom.getUserOne().equals(user)) {
            return false;
        }
        else {
            throw new UnauthorizedException();
        }
    }

    @Transactional
    public List<ChatRes.ChatDto> getChatList(Long chatRoomId, boolean senderDetermineCode) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId);
        if(optionalChatRoom.isEmpty()) {
            throw new NotFoundException("존재하지 않는 채팅방입니다.");
        }
        ChatRoom chatRoom = optionalChatRoom.get();
        List<Chat> chatList = chatRepository.findByChatRoom(chatRoom);
        List<ChatRes.ChatDto> chatDtoList = new ArrayList<>();
        for(Chat chat : chatList) {
            if(senderDetermineCode) {
                chatDtoList.add(ChatRes.ChatDto.builder()
                        .isSender(!chat.isSenderDetermineCode())
                        .content(chat.getContent())
                        .sendTime(chat.getRegDateTime())
                        .build());
            }
            else {
                chatDtoList.add(ChatRes.ChatDto.builder()
                        .isSender(chat.isSenderDetermineCode())
                        .content(chat.getContent())
                        .sendTime(chat.getRegDateTime())
                        .build());
            }
        }
        return chatDtoList;
    }
}
