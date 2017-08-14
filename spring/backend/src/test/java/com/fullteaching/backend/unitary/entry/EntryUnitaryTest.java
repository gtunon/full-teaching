package com.fullteaching.backend.unitary.entry;


import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.unitary.AbstractUnitTest;
import com.fullteaching.backend.user.User;

public class EntryUnitaryTest extends AbstractUnitTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEmptyEntry() {
		Entry e = new Entry();
		Assert.notNull(e);
	}

	@Test
	public void testEntry() {
		String[] roles = {"TEACHER"};
		User u =  new User("mock", "Pass1234", "mock", null, roles);
		long date = System.currentTimeMillis();
		Entry e = new Entry("Test Entry",date,u);
		Assert.notNull(e);
		Assert.isTrue("Test Entry".equals(e.getTitle()));
		Assert.isTrue(date==e.getDate());
		Assert.isTrue(u.equals(e.getUser()));
	}

	@Test
	public void testGetTitle() {
		Entry e = new Entry();
		e.setTitle("This title");
		Assert.notNull(e);
		Assert.isTrue("This title".equals(e.getTitle()));
	}

	@Test
	public void testGetDate() {
		Entry e = new Entry();
		long date = System.currentTimeMillis();
		e.setDate(date);
		Assert.notNull(e);
		Assert.isTrue(date==e.getDate());

	}

	@Test
	public void testGetComments() {
		Entry e = new Entry();
		Assert.notNull(e);
		
	}

	@Test
	public void testGetUser() {
		String[] roles = {"TEACHER"};

		User u =  new User("mock", "Pass1234", "mock", null, roles);

		Entry e = new Entry();
		e.setUser(u);
		Assert.notNull(e);
		Assert.isTrue(u.equals(e.getUser()));

	}

}
