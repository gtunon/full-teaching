package com.fullteaching.backend.unitary.chat;

import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import com.fullteaching.backend.chat.Chat;
import com.fullteaching.backend.chat.WebSocketChatUser;
import com.fullteaching.backend.unitary.AbstractUnitTest;

public class WebSocketChatUserUnitaryTest extends AbstractUnitTest {

	private static String name = "Nombre";
	private static String color = "color";
	private static String chat_name ="chat name";
	private static String teacher_name = "teacher_name";
	
	@Mock
	WebSocketSession session;
	
	@Mock
	ExecutorService executor;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testWebSocketChatUser() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Assert.notNull(wschu);
		Assert.isTrue(name.equals(wschu.getName()));
		Assert.isTrue(color.equals(wschu.getColor()));
	}

	@Test
	public void testNewChat() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		wschu.newChat(ch);
		
		Assert.notNull(wschu);

	}

	@Test
	public void testChatClosed() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Assert.notNull(wschu);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		wschu.newChat(ch);
		wschu.chatClosed(ch);
	}

	@Test
	public void testNewUserInChat() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		WebSocketChatUser wschu2 = new WebSocketChatUser(session, "Second User", "Grey");

		Assert.notNull(wschu);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		
		ch.addUser(wschu); 
		
		wschu.newChat(ch);
		
		wschu.newUserInChat(ch, wschu2);
		
	}

	@Test
	public void testUserExitedFromChat() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		WebSocketChatUser wschu2 = new WebSocketChatUser(session, "Second User", "Grey");

		Assert.notNull(wschu);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		wschu.newChat(ch);
		
		wschu.userExitedFromChat(ch, wschu2);
	}

	@Test
	public void testNewMessage() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Assert.notNull(wschu);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		wschu.newChat(ch);
		wschu.newMessage(ch, wschu, "Nuevo mensaje");
	}

	@Ignore //already tested with newUserInChat
	@Test
	public void testSendConnectedUsers() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Assert.notNull(wschu);
	}

	@Test
	public void testSendInterventionPetition() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Assert.notNull(wschu);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		wschu.newChat(ch);
		wschu.sendInterventionPetition(ch, wschu, true);
	}

	@Test
	public void testGrantIntervention() {
		WebSocketChatUser wschu = new WebSocketChatUser(session, name, color);
		Assert.notNull(wschu);
		Chat ch = new Chat (null, chat_name, executor, teacher_name);
		wschu.newChat(ch);
		wschu.grantIntervention(ch, wschu, true);
	}

}
