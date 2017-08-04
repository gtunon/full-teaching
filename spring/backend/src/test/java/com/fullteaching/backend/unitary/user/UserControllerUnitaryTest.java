/**
 * 
 */
package com.fullteaching.backend.unitary.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mockingDetails;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.unitary.AbstractControllerUnitTest;
import com.fullteaching.backend.unitary.utils.LoginUtils;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import com.fullteaching.backend.user.UserRepository;

/**
 * @author gtunon
 *
 */
/*@Transactional After each test the BBDD is rolled back*/
@Transactional
public class UserControllerUnitaryTest extends AbstractControllerUnitTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Autowired
	private UserComponent user;
	
	@Mock
	private AuthorizationService authorizationService;
	
	//urls
	String new_user_uri = "/api-users/new";
	String change_password_uri = "/api-users/changePassword";
	String login_uri = "/api-logIn";
	/*@BeforeClass
	public static void bbddSetUp() throws Exception {
		(new DatabaseInitializer()).run();
	}*/
	
	@Before
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void mockTests() {
		String[] roles = {"STUDENT"};

		assertThat(mockingDetails(userRepository).isMock(), is(true));
		Mockito.when(userRepository.findByName("fakeemail@gmail.com")).thenReturn(null);
	    Mockito.when(userRepository.findByName("repeated@gmail.com")).thenReturn(new User("fake", "Mock6666", "fakeUser", null, roles));
	    Mockito.when(userRepository.findByName("nonvalidMAIL")).thenReturn(null);
	    Assert.assertNull(userRepository.findByName("fakeemail@gmail.com"));
	    Assert.assertNull(userRepository.findByName("nonvalidMAIL"));
	    Assert.assertNotNull(userRepository.findByName("repeated@gmail.com"));
	    
	    /*TO_DO: For some reason I this pass but when executed inside a controlle the mock is not working...*/
	}
	
	/**
	 * Test method for {@link com.fullteaching.backend.user.UserController#newUser(java.lang.String[])}.
	 */
	@Test
	@Transactional
	@Rollback
	public void testNewUser() {
		String[] roles = {"STUDENT"};

		/*Parameters of the new user*/
		String ok_parameters = "[\"fakeemail@gmail.com\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";
		String ko_parameters1 = "[\"fakeemail@gmail.com\", \"Mock66666\", \"repeatedUser\", \"IGNORE\"]";
		String ko_parameters2 = "[\"fakeemail2@gmail.com\", \"Mock\", \"fakeUser\", \"IGNORE\"]";
		String ko_parameters3 = "[\"nonvalidMAIL\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";

	
		/*mock captcha*/
		//it seems that the dev key does nothing so automaticly ignored?
		//maybe validateGoogleCaptcha should be in a service?
		
		/*mock user repository to accept email and to reject it (another case) 
        Mockito.when(userRepository.findByName("fakeemail@gmail.com")).thenReturn(null);
        Mockito.when(userRepository.findByName("repeated@gmail.com")).thenReturn(new User("fake", "Mock6666", "fakeUser", null, roles));
        Mockito.when(userRepository.findByName("nonvalidMAIL")).thenReturn(null);
        Is not working... why?
        */

		/*Test OK*/
		try {
			MvcResult result =  mvc.perform(post(new_user_uri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(ok_parameters)
					                ).andReturn();
		
			String content = result.getResponse().getContentAsString();
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CREATED.value();
			//http status 201 created!
			Assert.assertEquals("failure - expected HTTP status "+expected, expected, status);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Test repeated user*/
		try {
			MvcResult result =  mvc.perform(post(new_user_uri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(ko_parameters1)
					                ).andReturn();
		
			String content = result.getResponse().getContentAsString();
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.CONFLICT.value();

			Assert.assertEquals("failure - expected HTTP status "+expected, expected, status);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Test bad password*/
		try {
			MvcResult result =  mvc.perform(post(new_user_uri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(ko_parameters2)
					                ).andReturn();
		
			String content = result.getResponse().getContentAsString();
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.BAD_REQUEST.value();

			Assert.assertEquals("failure - expected HTTP status "+expected, expected, status);
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		/*Test bad email*/
		try {
			MvcResult result =  mvc.perform(post(new_user_uri)
					                .contentType(MediaType.APPLICATION_JSON_VALUE)
					                .content(ko_parameters3)
					                ).andReturn();
		
			String content = result.getResponse().getContentAsString();
			int status = result.getResponse().getStatus();
			
			int expected = HttpStatus.FORBIDDEN.value();

			Assert.assertEquals("failure - expected HTTP status "+expected, expected, status);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link com.fullteaching.backend.user.UserController#changePassword(java.lang.String[])}.
	 */
	@Test
	@Transactional
//	@WithMockCustomUser
	public void testChangePassword() {
		
		String[] roles = {"STUDENT"};
		String user_parameters = "[\"fakeemail@gmail.com\", \"Mock66666\", \"fakeUser\", \"IGNORE\"]";
		String pass_parameters = "[\"Mock66666\", \"Mock77777\"]";
		String bad1_parameters = "[\"Mock66666\", \"Mock77777\"]";
		String bad2_parameters = "[\"Mock77777\", \"notvalid\"]";


		
		try {
			/*Create new user*/
			LoginUtils.registerUserIfNotExists(mvc, user_parameters);
			
			/*Login user*/
			HttpSession session = LoginUtils.logIn(mvc, "fakeemail@gmail.com", "Mock66666");
		
			/*Test change password OK*/
			MvcResult result_pass = mvc.perform(put(change_password_uri)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(pass_parameters)
					.session((MockHttpSession) session)
				).andReturn();
			
			int status_pass = result_pass.getResponse().getStatus();
			Assert.assertTrue("failure login - expected HTTP status "+
													HttpStatus.OK.value() +
													" but was: "+status_pass, 
								status_pass==HttpStatus.OK.value());
			
			/*Test change password bad initial password*/
			MvcResult result_bad1 = mvc.perform(put(change_password_uri)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(bad1_parameters)
					.session((MockHttpSession) session)
				).andReturn();
			
			int status_bad1 = result_bad1.getResponse().getStatus();
			Assert.assertTrue("failure login - expected HTTP status "+
													HttpStatus.CONFLICT.value() +
													" but was: "+status_bad1, 
										status_bad1==HttpStatus.CONFLICT.value());
			
			/*Test change password bad initial password*/
			MvcResult result_bad2 = mvc.perform(put(change_password_uri)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(bad2_parameters)
					.session((MockHttpSession) session)
				).andReturn();
			
			int status_bad2 = result_bad2.getResponse().getStatus();
			Assert.assertTrue("failure login - expected HTTP status "+
													HttpStatus.NOT_MODIFIED.value() +
													" but was: "+status_bad2, 
										status_bad2==HttpStatus.NOT_MODIFIED.value());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertTrue(true);
	}
	
	

}
