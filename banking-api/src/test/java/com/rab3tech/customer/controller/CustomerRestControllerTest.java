package com.rab3tech.customer.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestBody;

import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.impl.CustomerTransactionsServiceImpl;
import com.rab3tech.vo.ApplicationResponseVO;
import com.rab3tech.vo.ChangePasswordRequestVO;


@RunWith(MockitoJUnitRunner.class)
public class CustomerRestControllerTest {
	
	@Mock
	private LoginService loginService;
	
	@Mock
	private JavaMailSender javaMailSender;
	
	@Mock
	private CustomerService customerService;
	@InjectMocks
      private CustomerRestController customerRestController;
	
	@Before
	public void initializer(){
	
		MockitoAnnotations.initMocks(this); 
	}
	
	@Test
	public void testUpdateCustomerPasswordIfSuccess() {
		
		ChangePasswordRequestVO changePasswordRequestVO=new ChangePasswordRequestVO();
		changePasswordRequestVO.setLoginid("12345");
		changePasswordRequestVO.setNewpassword("1234");
		changePasswordRequestVO.setPasscode("1111");
		String status ="success" ;
		ApplicationResponseVO applicationResponseVO1=new ApplicationResponseVO();
		applicationResponseVO1.setCode(200);
		applicationResponseVO1.setStatus("success");
		applicationResponseVO1.setMessage("Your password is updated successfully.");
		
		when( loginService.updatePassword(changePasswordRequestVO)).thenReturn(status);

		ApplicationResponseVO applicationResponseVO=customerRestController.updateCustomerPassword( changePasswordRequestVO) ;
		assertTrue(applicationResponseVO!=null);
	
		assertEquals(200, applicationResponseVO.getCode());
		assertEquals("success", applicationResponseVO.getStatus());
		assertEquals("Your password is updated successfully.", applicationResponseVO.getMessage());
	}
	@Test
	public void testUpdateCustomerPasswordIfFail() {
		
		ChangePasswordRequestVO changePasswordRequestVO=new ChangePasswordRequestVO();
		changePasswordRequestVO.setLoginid("12345");
		changePasswordRequestVO.setNewpassword("1234");
		changePasswordRequestVO.setPasscode("1111");
		String status ="Sorry, ! your passcode is not correct!" ;
		
		ApplicationResponseVO applicationResponseVO1=new ApplicationResponseVO();
		applicationResponseVO1.setCode(0);
		applicationResponseVO1.setStatus("fail");
		applicationResponseVO1.setMessage(status);
		
		when( loginService.updatePassword(changePasswordRequestVO)).thenReturn(status);
		
		ApplicationResponseVO applicationResponseVO=customerRestController.updateCustomerPassword( changePasswordRequestVO) ;
		assertTrue(applicationResponseVO!=null);
		
		assertEquals(0, applicationResponseVO.getCode());
		assertEquals("fail", applicationResponseVO.getStatus());
		assertEquals("Sorry, ! your passcode is not correct!", applicationResponseVO.getMessage());
	}
	

}
