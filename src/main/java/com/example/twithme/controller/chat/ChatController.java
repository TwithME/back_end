package com.example.twithme.controller.chat;

import com.example.twithme.common.exception.BadRequestException;
import com.example.twithme.common.exception.dto.HttpRes;
import com.example.twithme.model.dto.chat.ChatReq;
import com.example.twithme.model.dto.chat.ChatRes;
import com.example.twithme.model.entity.chat.ChatRoom;
import com.example.twithme.model.entity.user.User;
import com.example.twithme.service.chat.ChatService;
import com.example.twithme.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags={"05.Chat"})
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @ApiOperation(value = "쪽지 보내기", notes = "쪽지를 보낼 수 있습니다. ")
    @PostMapping("/send")
    public HttpRes<String> sendMessage(@RequestBody ChatReq.SendChatDto sendChatDto,
                                       HttpServletRequest httpServletRequest) {
        Long senderId = userService.getUserId(httpServletRequest);
        Long recipientId = sendChatDto.getRecipientId();
        if(senderId.equals(recipientId)) {
            throw new BadRequestException("내게 쓰기는 지원하지 않는 기능입니다.");
        }
        User sender = userService.getUserByUserId(senderId);
        User recipient = userService.getUserByUserId(recipientId);
        String content = sendChatDto.getContent();

        ChatRoom chatRoom = chatService.getChatRoomId(sender, recipient);
        if (chatRoom == null) {
            chatRoom = chatService.createChatRoom(sender, recipient);
        }
        chatService.writeChat(chatRoom, sender, recipient, content);

        return new HttpRes<>("메시지 전송이 완료되었습니다.");
    }

    @ApiOperation(value = "채팅방 목록 조회", notes = "채팅방의 목록을 조회합니다.")
    @GetMapping("/chatroom-list")
    public HttpRes<List<ChatRes.ChatRoomDto>> getChatRoomList(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        User user = userService.getUserByUserId(userId);
        return new HttpRes<>(chatService.getChatRoomList(user));
    }

    @ApiOperation(value = "채팅방 채팅 읽기", notes = "해당되는 채팅방의 채팅들을 읽습니다.")
    @GetMapping("/{chatRoomId}")
    public HttpRes<List<ChatRes.ChatDto>> getChatList(@PathVariable("chatRoomId") Long chatRoomId,
                                             HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        User user = userService.getUserByUserId(userId);
        boolean senderDetermineCode = chatService.userCheck(chatRoomId, user);
        return new HttpRes<>(chatService.getChatList(chatRoomId, senderDetermineCode));
    }
}