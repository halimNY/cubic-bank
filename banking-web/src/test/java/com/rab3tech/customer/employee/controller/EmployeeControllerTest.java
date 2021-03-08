package com.rab3tech.customer.employee.controller;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.vo.CustomerVO;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {
	
	
	
		
	 @Mock
		private CustomerEnquiryService customerEnquiryService;
		
	 @Mock
		private CustomerService customerService;	
	 @Mock
		private EmailService emailService;
	 @Before
		public void initializer(){
		
			MockitoAnnotations.initMocks(this); 
		}
	 @Test
		public void testShowCustome(){
	/*	 public String showCustomer(Model model) {	
				List<CustomerVO> customerVOs=customerService.findCustomers();
				System.out.println("customer list ::::::"+customerVOs);
				model.addAttribute("customerVOs", customerVOs);				
				return "employee/customers-nv";
			}  */
		 
	 }
	 

}
