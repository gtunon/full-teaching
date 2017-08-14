package com.fullteaching.backend.unitary.forum;

import org.junit.Test;
import org.springframework.util.Assert;

import com.fullteaching.backend.forum.Forum;
import com.fullteaching.backend.unitary.AbstractUnitTest;

public class ForumUnitaryTest extends AbstractUnitTest {

	
	@Test
	public void newForumTest() {
		Forum f = new Forum();
		Assert.notNull(f);
		
		Forum f2 = new Forum(true);
		Assert.notNull(f2);
		Assert.isTrue(f2.isActivated());
		
		Forum f3 = new Forum(false);
		Assert.notNull(f3);
		Assert.isTrue(!f3.isActivated());
	}

	
	@Test
	public void activateAndDeactivateTest() {
		Forum f = new Forum();
		f.setActivated(true);
		Assert.isTrue(f.isActivated());
		
		f.setActivated(false);
		Assert.isTrue(!f.isActivated());
		
	}

	@Test
	public void testGetEntries() {
		//TODO
		Assert.isTrue(true);
	}

}
