package com.fullteaching.backend.unitary.session;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.forum.Forum;
import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.unitary.AbstractUnitTest;
import com.fullteaching.backend.user.User;

public class SessionUnitaryTest extends AbstractUnitTest {

	private static String title = "Session Title";
	private static String description = "Session Description";
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEmptySession() {
		Session session = new Session();
		Assert.notNull(session);
	}

	@Test
	public void testSession() {
		Long date = System.currentTimeMillis();
		Session session = new Session(title, description, date);
		Assert.notNull(session);
		Assert.isTrue(date == session.getDate());
		Assert.isTrue(title.equals(session.getTitle()));
		Assert.isTrue(description.equals(session.getDescription()));
	}

	@Test
	public void testSessionWithCourse() {
		CourseDetails cd = new CourseDetails();
		cd.setInfo("This is the info");
		Forum f = new Forum(false);
		cd.setForum(f);
		String[] roles = {"STUDENT"};
		User u = new User("mock_teacher","mock2222","t_mocky", null,roles);
		//prepare test
		Course c = new Course("to modify", "/../assets/images/default_session_image.png", 
				u,cd);

		Long date = System.currentTimeMillis();
		Session session = new Session(title, description, date, c);
		Assert.notNull(session);
		Assert.isTrue(date == session.getDate());
		Assert.isTrue(title.equals(session.getTitle()));
		Assert.isTrue(description.equals(session.getDescription()));
		Assert.isTrue(c.equals(session.getCourse()));
	}

	@Test
	public void testGetTitle() {
		Session session = new Session();
		session.setTitle(title);
		Assert.notNull(session);
		Assert.isTrue(title.equals(session.getTitle()));
	}

	@Test
	public void testGetDescription() {
		Session session = new Session();
		session.setDescription(description);
		Assert.notNull(session);
		Assert.isTrue(description.equals(session.getDescription()));
	}

	@Test
	public void testGetDate() {
		Session session = new Session();
		Long date = System.currentTimeMillis();
		session.setDate(date);
		Assert.notNull(session);
		Assert.isTrue(date == session.getDate());	
	}

	@Test
	public void testGetCourse() {
		CourseDetails cd = new CourseDetails();
		cd.setInfo("This is the info");
		Forum f = new Forum(false);
		cd.setForum(f);
		String[] roles = {"STUDENT"};
		User u = new User("mock_teacher","mock2222","t_mocky", null,roles);
		//prepare test
		Course c = new Course("to modify", "/../assets/images/default_session_image.png", 
				u,cd);
		Session session = new Session();
		session.setCourse(c);
		Assert.notNull(session);
		Assert.isTrue(c.equals(session.getCourse()));		
	}

	@Test
	public void testEqualsObject() {
		Session session1 = new Session();
		session1.setId(1);
		Session session2 = new Session();
		session2.setId(1);
		Session session3 = new Session();
		session3.setId(2);
		Assert.notNull(session1);
		Assert.notNull(session2);
		Assert.notNull(session3);
		Assert.isTrue(session1.equals(session2));	
		Assert.isTrue(session1.equals(session1));
		Assert.isTrue(!session1.equals(null));
		Assert.isTrue(!session1.equals("not_a_session"));
		Assert.isTrue(!session1.equals(session3));
		
	}

}
