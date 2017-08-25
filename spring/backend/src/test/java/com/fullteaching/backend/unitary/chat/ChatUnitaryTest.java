package com.fullteaching.backend.unitary.chat;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import com.fullteaching.backend.chat.Chat;
import com.fullteaching.backend.chat.ChatManager;
import com.fullteaching.backend.chat.WebSocketChatUser;
import com.fullteaching.backend.unitary.AbstractUnitTest;

public class ChatUnitaryTest extends AbstractUnitTest {

	private static String chat_name="chat Name";
	private String teacher_name="teacher Name";
	private String user_name="user Name";

	private static String color = "color";
	
	@Mock
	WebSocketSession session;
	
	@Mock
	ExecutorService executor;
	
	@Mock 
	ChatManager chatManager; 
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testChat() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		
		Assert.notNull(ch);
	}

	@Test
	public void testAddUser() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		
		Assert.notNull(ch);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		WebSocketChatUser wscht = new WebSocketChatUser(session, teacher_name, "teacherColor");
		ch.addUser(wschu);
		ch.addUser(wscht);
		Assert.isTrue(ch.getUser(user_name).equals(wschu));
		Assert.isTrue(ch.getUser(teacher_name).equals(wscht));
		Assert.isTrue(ch.getUsers().size()==2);
	}

	@Test
	public void testRemoveUser() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		
		Assert.notNull(ch);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		ch.addUser(wschu);
		Assert.isTrue(ch.getUser(user_name).equals(wschu));
		
		ch.removeUser(wschu);
		Assert.isTrue(ch.getUser(user_name)==null);
		
	}

	@Ignore //unitary test on chatManager.closeChat
	@Test
	public void testClose() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		
		Assert.notNull(ch);
		ch.close();
	}

	@Test 
	public void testSendMessage() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		ch.addUser(wschu);
		Assert.notNull(ch);
		ch.sendMessage(wschu, "Este mensaje");
	}

	@Test
	public void testRequestIntervention() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		WebSocketChatUser wscht = new WebSocketChatUser(session, teacher_name, "teacherColor");
		ch.addUser(wschu);
		ch.addUser(wscht);
		Assert.notNull(ch);
		ch.requestIntervention(wschu, true);
	}

	@Test
	public void testGrantIntervention() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		ch.addUser(wschu);
		Assert.notNull(ch);
		ch.grantIntervention(wschu.getName(), true);
	}

	@Test
	public void testGetUsers() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		ch.addUser(wschu);
		Assert.notNull(ch);
		Assert.isTrue(ch.getUsers().contains(wschu));
	}

	@Test
	public void testGetName() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		
		Assert.notNull(ch);
		Assert.isTrue(chat_name.equals(ch.getName()));
	}

	@Test
	public void testGetUser() {
		
		Chat ch = new Chat (chatManager, chat_name, executor, teacher_name);
		WebSocketChatUser wschu = new WebSocketChatUser(session, user_name, color);
		ch.addUser(wschu);
		Assert.notNull(ch);
		Assert.isTrue(ch.getUser(wschu.getName()).equals(wschu));
	}

}
