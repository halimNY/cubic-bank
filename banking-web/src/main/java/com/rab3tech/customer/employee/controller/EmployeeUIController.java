package com.rab3tech.customer.employee.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.utils.BankHttpUtils;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.EmailVO;
import com.rab3tech.vo.RoleVO;

@Controller
public class EmployeeUIController {
	
    private static final Logger logger = LoggerFactory.getLogger(EmployeeUIController.class);
	
	@Autowired
	private CustomerEnquiryService customerEnquiryService;
	
	@Autowired
	private CustomerService customerService;
	
	@Value("${customer.registration.url}")
	private String registrationURL;
	
	@Autowired
	private EmailService emailService;

	
	@GetMapping("/customer/lock")
	public String customerLock(@RequestParam String userid) {
	   customerService.updateCustomerLockStatus(userid, "yes");
	   return "redirect:/employee/customersLockUnlock";
	}
	
	@GetMapping("/customer/unlock")
	public String customerUnlock(@RequestParam String userid) {
	   customerService.updateCustomerLockStatus(userid, "no");
	   return "redirect:/employee/customersLockUnlock";
	}
	//---------------------------------------------------------------------------------
	@GetMapping("/employee/customersLockUnlock")
	public String showCustomerLockUnlock(Model model) {

	   List<CustomerVO> customerVOs=customerService.findCustomers();

	   model.addAttribute("customerVOs", customerVOs);


	   return "employee/customers";
	}
	//---------------------------------------------------------------------------------
	@GetMapping("/employee/customers")
	public String showCustomer(Model model) {
		
		List<CustomerVO> customerVOs=customerService.findCustomers();
		System.out.println("customer list ::::::"+customerVOs);
		model.addAttribute("customerVOs", customerVOs);
		
		return "employee/customers-nv";
	}
	//----------------------------- desc-------------------------------------------------
	@GetMapping("/customer/descendingByEmail")
	public String  customerListDescendingByEmail(Model model) {
		   List<CustomerVO> customerVOs=customerService.findCustomers();
		 List<CustomerVO> custmoers = customerVOs.stream().sorted((c1,c2)->c2.getEmail().compareTo(c1.getEmail())).collect(Collectors.toList());
		  model.addAttribute("customerVOs", custmoers);	
		return "/employee/customers-nv";
	}
	
	//------------------------------- Asc -----------------------------------------------
	@GetMapping("/customer/ascendingByEmail")
	public String  customerListAscendingByEmail(Model model) {
		   List<CustomerVO> customerVOs=customerService.findCustomers();
		 List<CustomerVO> custmoers = customerVOs.stream().sorted((c1,c2)->c1.getEmail().compareTo(c2.getEmail())).collect(Collectors.toList());
		  model.addAttribute("customerVOs", custmoers);	
		return "/employee/customers-nv";
	}
	//----------------------------- desc-------------------------------------------------
	@GetMapping("/customer/descendingByname")
	public String  customerListDescendingByname(Model model) {
		   List<CustomerVO> customerVOs=customerService.findCustomers();
		 List<CustomerVO> custmoers = customerVOs.stream().sorted((c1,c2)->c2.getName().compareTo(c1.getName())).collect(Collectors.toList());
		  model.addAttribute("customerVOs", custmoers);	
		return "/employee/customers-nv";
	}
	//------------------------------- Asc -----------------------------------------------
	@GetMapping("/customer/ascendingByname")
	public String  customerListAscendingByname(Model model) {
		   List<CustomerVO> customerVOs=customerService.findCustomers();
		 List<CustomerVO> custmoers = customerVOs.stream().sorted((c1,c2)->c1.getName().compareTo(c2.getName())).collect(Collectors.toList());
		  model.addAttribute("customerVOs", custmoers);	
		return "/employee/customers-nv";
	}
	
	//------------------------------------------------------------------------------
	
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@GetMapping("/customers/account/approve")
	public String customerAccountApproveGet(@RequestParam int csaid) {
		CustomerAccountInfoVO accountInfoVO=customerService.createBankAccount(csaid);
		System.out.println(accountInfoVO);
		return "redirect:/customer/accounts/approved";
	}
	
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@PostMapping("/customers/account/approve")
	public String customerAccountApprove(@RequestParam int csaid) {
		CustomerAccountInfoVO accountInfoVO=customerService.createBankAccount(csaid);
		System.out.println(accountInfoVO);
		return "redirect:/customer/accounts/approved";
	}
	
	//This is showing customers who are already registered and account is not created so far
	@GetMapping(value= {"/customer/accounts/approved"})
    @PreAuthorize("hasAuthority('EMPLOYEE')")
	public String showCustomerAccountsApproved(Model model) {
		logger.info("showCustomerAccountsApproved is called!!!");
		List<CustomerSavingVO> pendingApplications = customerEnquiryService.findRegisteredEnquiry();
		model.addAttribute("applicants", pendingApplications);
		return "employee/customerAccountsApproved";	//login.html
	}
	
	
	@GetMapping(value= {"/customer/enquiries"})
    @PreAuthorize("hasAuthority('EMPLOYEE')")
	public String showCustomerEnquiry(Model model) {
		logger.info("showCustomerEnquiry is called!!!");
		List<CustomerSavingVO> pendingApplications = customerEnquiryService.findPendingEnquiry();
		model.addAttribute("applicants", pendingApplications);
		return "employee/customerEnquiryList";	//login.html
	}
	
	@PostMapping("/customers/enquiry/approve")
	public String customerEnquiryApprove(@RequestParam int csaid,HttpServletRequest request) {
		CustomerSavingVO customerSavingVO=customerEnquiryService.changeEnquiryStatus(csaid, "APPROVED");
		String cuuid=BankHttpUtils.generateToken();
		customerEnquiryService.updateEnquiryRegId(csaid, cuuid);
		String registrationLink=BankHttpUtils.getServerBaseURL(request)+"/"+registrationURL+cuuid;
		//String registrationLink ="http://localhost:8080/v3/customer/registration/complete";
		EmailVO mail=new EmailVO(customerSavingVO.getEmail(),"javahunk2020@gmail.com","Regarding Customer "+customerSavingVO.getName()+"  Account registration","",customerSavingVO.getName());
		mail.setRegistrationlink(registrationLink);
		emailService.sendRegistrationEmail(mail);
		return "redirect:/customer/enquiries";
	}
	//--------------------------------- delete ---------------------------------------------
	@GetMapping("customer/deleteCustomer")
	public String deleteCostumer(@RequestParam String email, HttpSession session) {
		CustomerAccountInfoVO customerAccountInfo=customerService.findCustomerAccountInfo(email);
		
	    if (customerAccountInfo == null) {
	    customerService.deleteCustmoer(email) ;
		return "redirect:/employee/customers-nv";
	    }else {
		long id=customerAccountInfo.getId() ;
		customerService.deleteCustmoerAccount(id);
		customerService.deleteCustmoer(email) ;
	 	return "redirect:/employee/customers-nv";
	    }
	}
	//--------------------------------------------------------------------------------------
	
	
	
	//--------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------


}
