package com.fullteaching.backend.integration.session;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.unitary.AbstractLoggedControllerUnitTest;
import com.fullteaching.backend.unitary.utils.CourseTestUtils;
import com.fullteaching.backend.unitary.utils.SessionTestUtils;

public class VideoSessionControllerUnitaryTest extends AbstractLoggedControllerUnitTest {

	private static String getIdAndToken_uri="/api-video-sessions/get-sessionid-token/";
	//private static String removeUser_uri="/api-video-sessions/remove-user";
	
	@Before
	public void setUp() {
		super.setUp();
	}

	@Ignore
	@Test
	public void testGetSessionIdAndToken() {
		//newSession
		Course c = CourseTestUtils.newCourseWithCd("Course", loggedUser, null, "This is the info", false);
		c = CourseTestUtils.createCourseIfNotExist(mvc, c, httpSession);

		Long date = System.currentTimeMillis();
		Session s = new Session("Mock Session", "this descriptions", date, c);
		
		c = SessionTestUtils.newSession(mvc, s, c, httpSession);
		
		s = (Session)c.getSessions().toArray()[0];
		
		try {
			MvcResult result =  mvc.perform(get(getIdAndToken_uri+s.getId())
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .session((MockHttpSession) httpSession)
					                ).andReturn();
			
			//String content = result.getResponse().getContentAsString();
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.OK.value();
			
			Assert.assertEquals("failure - expected HTTP status "+expected, expected, status);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("EXCEPTION: //test OK");
		}
	}

	@Ignore
	@Test
	public void testRemoveUser() {
		fail("Not yet implemented");
	}

}
