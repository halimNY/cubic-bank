package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.admin.dao.repository.AccountTypeRepository;
import com.rab3tech.admin.dao.repository.MagicCustomerRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountApprovedRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountEnquiryRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountInfoRepository;
import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.customer.dao.repository.CustomerTransactionRepository;
import com.rab3tech.customer.dao.repository.LoginRepository;
import com.rab3tech.customer.dao.repository.PayeeRepository;
import com.rab3tech.customer.dao.repository.RoleRepository;
import com.rab3tech.dao.entity.AccountType;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.dao.entity.PayeeInfo;
import com.rab3tech.dao.entity.PayeeStatus;
import com.rab3tech.dao.entity.Role;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.PayeeInfoVO;

public class CustomerServiceImplTest {
	@Mock
	private MagicCustomerRepository customerRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private AccountStatusRepository accountStatusRepository;

	@Mock
	private AccountTypeRepository accountTypeRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	private CustomerAccountEnquiryRepository customerAccountEnquiryRepository;

	@Mock
	private CustomerAccountApprovedRepository customerAccountApprovedRepository;

	@Mock
	private CustomerAccountInfoRepository customerAccountInfoRepository;
	
	@Mock
	private CustomerRepository CustomerRepository;
	
	@Mock
	private PayeeRepository payeeRepository;
	
	@Mock
	private EmailService emailService;
	
	@Mock
	private LoginRepository loginRepository;
	//@Autowired
	//private CustomerTransaction customerTransaction ;
	@Mock
	private CustomerTransactionRepository customerTransactionRepository ;
	
	@InjectMocks CustomerServiceImpl customerServiceImpl;
	@Before
	public void initializer(){
	
		MockitoAnnotations.initMocks(this); 
	}
	@Test
	public void  testShowCustomer() {
		
		List<Customer> customers = new ArrayList<Customer>() ;	
	//----------------------------------------------------	
		Customer customer1 = new Customer();	
		Set<Role>  roles = new HashSet<>(); 
		Role Role = new Role();
		Role.setRid(3);
		roles.add(Role);
		Optional<Login> login=Optional.of(new Login());	
		login.get().setRoles(roles);
		
		customer1.setId(111);
		customer1.setAge(23);
		customer1.setGender("mal");
		customer1.setEmail("test@gmail.com");
		customer1.setLogin(login.get());
		customers.add(customer1);

		when(loginRepository.findByLoginid("test@gmail.com")).thenReturn(login);
	
	//----------------------------------------------------		
	
		Customer customer2 = new Customer();
		customer2.setId(222);
		customer2.setAge(33);
		customer2.setGender("mal");
		customer2.setEmail("walid@gmail.com");
		Set<Role>  roles1 = new HashSet<>(); 
		Role Role1 = new Role();
		Role1.setRid(3);
		roles1.add(Role1);
		Optional<Login> login1=Optional.of(new Login());	
		login1.get().setRoles(roles1);
		customer2.setLogin(login1.get());
		customers.add(customer2);

	when(loginRepository.findByLoginid("walid@gmail.com")).thenReturn(login1);
	//----------------------------------------------------			

	
	  
		when(customerRepository.findAll()).thenReturn(customers);
		
		List<CustomerVO> customerOnly =customerServiceImpl.findCustomers();
		assertTrue(customerOnly!=null);
		assertTrue(customerOnly.size()==2);
		assertEquals("test@gmail.com", customerOnly.get(0).getEmail());
		assertEquals(111, customerOnly.get(0).getId() );
		assertEquals(23, customerOnly.get(0).getAge() );
		
		assertEquals("walid@gmail.com", customerOnly.get(1).getEmail());
		assertEquals(222, customerOnly.get(1).getId() );
	    assertEquals(33, customerOnly.get(1).getAge() ); 	 
	}
	
	@Test
	public void  testCustomersListEqualNull() {
		
		List<Customer> customers = new ArrayList<Customer>() ;	
		customers=null;
		when(customerRepository.findAll()).thenReturn(customers);
		List<CustomerVO> customerOnly =customerServiceImpl.findCustomers();


		  assertEquals(null, customerOnly );
	}
	@Test
	public void  testRegisteredPayeeList() {
		 List<PayeeInfo> payeeInfoList = new ArrayList<PayeeInfo>() ;
		 PayeeStatus PayeeStatus =new PayeeStatus();
		 PayeeStatus.setId(11);
		 PayeeStatus.setName("saving");
		 PayeeStatus.setCode("12345");
		 
		 PayeeStatus PayeeStatus1 =new PayeeStatus(); 
		 PayeeStatus1.setId(22);
		 PayeeStatus1.setName("cheking");
		 PayeeStatus1.setCode("45678");
		 
		PayeeInfo payeeInfo1 = new PayeeInfo();
		payeeInfo1.setId(111);
		payeeInfo1.setPayeeName("halim");
		payeeInfo1.setPayeeAccountNo("12121234");
		payeeInfo1.setPayeeStatus(PayeeStatus);
	
		
		PayeeInfo payeeInfo2 = new PayeeInfo() ;
		payeeInfo2.setId(222);
		payeeInfo2.setPayeeName("walid");
		payeeInfo2.setPayeeAccountNo("23232323");	
		payeeInfo2.setPayeeStatus(PayeeStatus1);
		
		
		payeeInfoList.add(payeeInfo1);
		payeeInfoList.add(payeeInfo2);
		
		
		String customerId="123";
		 
		 
		  when( payeeRepository.findRegisteredPayee(customerId)).thenReturn(payeeInfoList); 
		  
		  
		  
		  List<PayeeInfoVO> payeeInfoVOList = customerServiceImpl.registeredPayeeList(customerId) ;
		
		 assertTrue(payeeInfoVOList!=null);
			assertTrue(payeeInfoVOList.size()==2);
			assertEquals("12121234", payeeInfoVOList.get(0).getPayeeAccountNo()) ;
			assertEquals(111, payeeInfoVOList.get(0).getId() );
			assertEquals("halim", payeeInfoVOList.get(0).getPayeeName());
			
			assertEquals("23232323", payeeInfoVOList.get(1).getPayeeAccountNo());
			assertEquals(222, payeeInfoVOList.get(1).getId() );
			assertEquals("walid", payeeInfoVOList.get(1).getPayeeName());
	
	}
	

	@Test
	public void  testFindCustomerAccountInfo() {
		AccountType AccountType = new AccountType();
		AccountType.setCode("5684");
		AccountType.setName("saving");
		AccountType.setDescription("des");
	
		   Optional<CustomerAccountInfo> customerAccountInfo=Optional.of(new CustomerAccountInfo());
		   customerAccountInfo.get().setAccountNumber("1232343456");
		   customerAccountInfo.get().setAvBalance(1000);
		   customerAccountInfo.get().setBranch("Fremont");
		   customerAccountInfo.get().setAccountType(AccountType);	
		  
		when( customerAccountInfoRepository.findByLoginId("3333")).thenReturn(customerAccountInfo);
		 CustomerAccountInfoVO accountInfoVO =customerServiceImpl.findCustomerAccountInfo("3333");
		 assertTrue(accountInfoVO!=null); 

			assertEquals("1232343456", accountInfoVO.getAccountNumber()) ;
			assertEquals("Fremont", accountInfoVO.getBranch()) ; 
		 
	}

}
