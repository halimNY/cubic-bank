package com.rab3tech.customer.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.admin.dao.repository.AccountTypeRepository;
import com.rab3tech.admin.dao.repository.MagicCustomerRepository;
import com.rab3tech.aop.advice.TimeLogger;
import com.rab3tech.customer.dao.repository.CustomerAccountApprovedRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountEnquiryRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountInfoRepository;
import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.customer.dao.repository.CustomerTransactionRepository;
import com.rab3tech.customer.dao.repository.LoginRepository;
import com.rab3tech.customer.dao.repository.PayeeRepository;
import com.rab3tech.customer.dao.repository.RoleRepository;
import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.AccountType;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.CustomerSaving;
import com.rab3tech.dao.entity.CustomerSavingApproved;
import com.rab3tech.dao.entity.CustomerTransaction;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.dao.entity.PayeeInfo;
import com.rab3tech.dao.entity.PayeeStatus;
import com.rab3tech.dao.entity.Role;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.mapper.CustomerMapper;
import com.rab3tech.utils.AccountStatusEnum;
import com.rab3tech.utils.PasswordGenerator;
import com.rab3tech.utils.TransactionIdGeneratorUtils;
import com.rab3tech.utils.Utils;
import com.rab3tech.vo.AccountTypeVO;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.CustomerUpdateVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.EmailVO;
import com.rab3tech.vo.FundTransferVO;
import com.rab3tech.vo.PayeeApproveVO;
import com.rab3tech.vo.PayeeInfoVO;
import com.rab3tech.vo.RoleVO;
import com.rab3tech.vo.UpdatePayeeVO;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private MagicCustomerRepository customerRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AccountStatusRepository accountStatusRepository;

	@Autowired
	private AccountTypeRepository accountTypeRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private CustomerAccountEnquiryRepository customerAccountEnquiryRepository;

	@Autowired
	private CustomerAccountApprovedRepository customerAccountApprovedRepository;

	@Autowired
	private CustomerAccountInfoRepository customerAccountInfoRepository;
	
	@Autowired
	private CustomerRepository CustomerRepository;
	
	@Autowired
	private PayeeRepository payeeRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private LoginRepository loginRepository;
	//@Autowired
	//private CustomerTransaction customerTransaction ;
	@Autowired
	private CustomerTransactionRepository customerTransactionRepository ;
	
	//@TimeLogger
	private CustomerAccountInfoVO createBankAccount(int csaid,String email) {
		// logic
				String customerAccount = Utils.generateCustomerAccount();
				CustomerSaving customerSaving = customerAccountEnquiryRepository.findById(csaid).get();

				CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
				customerAccountInfo.setAccountNumber(customerAccount);
				customerAccountInfo.setAccountType(customerSaving.getAccType());
				customerAccountInfo.setAvBalance(1000.0F);
				customerAccountInfo.setBranch(customerSaving.getLocation());
				customerAccountInfo.setCurrency("$");
				Customer customer = customerRepository.findByEmail(email==null?customerSaving.getEmail():email).get();
				customerAccountInfo.setCustomerId(customer.getLogin());
				customerAccountInfo.setStatusAsOf(new Date());
				customerAccountInfo.setTavBalance(1000.0F);
				CustomerAccountInfo customerAccountInfo2 = customerAccountInfoRepository.save(customerAccountInfo);

				CustomerSavingApproved customerSavingApproved = new CustomerSavingApproved();
				BeanUtils.copyProperties(customerSaving, customerSavingApproved);
				customerSavingApproved.setAccType(customerSaving.getAccType());
				customerSavingApproved.setStatus(customerSaving.getStatus());
				// saving entity into customer_saving_enquiry_approved_tbl
				customerAccountApprovedRepository.save(customerSavingApproved);

				// delete data from
				customerAccountEnquiryRepository.delete(customerSaving);

				CustomerAccountInfoVO accountInfoVO = new CustomerAccountInfoVO();
				BeanUtils.copyProperties(customerAccountInfo2, accountInfoVO);
				return accountInfoVO;
	}

	@Override
	public CustomerAccountInfoVO createBankAccount(int csaid) {
		return createBankAccount(csaid,null);
	}
		

	@Override
	public CustomerVO createAccount(CustomerVO customerVO) {

		Customer pcustomer = new Customer();
		BeanUtils.copyProperties(customerVO, pcustomer);
		Login login = new Login();
		login.setNoOfAttempt(3);
		login.setLoginid(customerVO.getEmail());
		login.setName(customerVO.getName());
		String genPassword = PasswordGenerator.generateRandomPassword(8);
		customerVO.setPassword(genPassword);
		login.setPassword(bCryptPasswordEncoder.encode(genPassword));
		login.setToken(customerVO.getToken());

		login.setLocked("no");

		Role entity = roleRepository.findById(3).get();
		Set<Role> roles = new HashSet<>();
		roles.add(entity);
		// setting roles inside login
		login.setRoles(roles);
		// setting login inside
		pcustomer.setLogin(login);
		Customer dcustomer = customerRepository.save(pcustomer);
		customerVO.setId(dcustomer.getId());
		customerVO.setUserid(customerVO.getUserid());

		Optional<CustomerSaving> optional = customerAccountEnquiryRepository.findByEmail(dcustomer.getEmail());
		if (optional.isPresent()) {
			CustomerSaving customerSaving = optional.get();
			AccountStatus accountStatus = accountStatusRepository.findByCode(AccountStatusEnum.REGISTERED.getCode())
					.get();
			customerSaving.setStatus(accountStatus);
			//Do you know database!!!
			this.createBankAccount(customerSaving.getCsaid(),pcustomer.getEmail());
		}
	
		
		return customerVO;
	}
	
	@Override
	@Transactional
	public void updateCustomerLockStatus(String userid,String status) {
		Customer customer=customerRepository.findByEmail(userid).get();
		customer.getLogin().setLocked(status);
		EmailVO  em=new EmailVO();
		em.setBody("Your account is locked!!!");
		em.setFrom("javahunk100@gmail.com");
		em.setTo(userid);
		em.setName(customer.getName());
		//em.setUsername(customer.getEmail()); // new modification 
		emailService.sendLockAndUnlockEmail(em);
		//Here write code for email service
	}

	@Override
	public List<CustomerVO> findCustomers() {
		List<Customer> customers = customerRepository.findAll();		
			 List<CustomerVO> customerOnly=new ArrayList<CustomerVO>(); 
		 
		 if (customers != null) {
		 for(Customer customer:customers) {
			 CustomerVO customerVO = new CustomerVO();
	
			 //------------- find customer role Id -----------------
				String email= customer.getEmail();
				Login login=loginRepository.findByLoginid(email).get();
				Set<Role>  roles =login.getRoles();
		    //--------------------------------------------------------	
				customerVO.setUserid(login.getLoginid());
				customerVO.setLocked(login.getLocked());
				
				for (Role role : roles) {
					if(role.getRid()==3) {
					    	 BeanUtils.copyProperties(customer, customerVO);  
						    customerOnly.add(customerVO); 	
					}			
				}
		 }
		 
		 
		 
		 return customerOnly;
		 }else
				return null;
//	 List<CustomerVO> listCustomerVO = customers.stream().map(CustomerMapper::toVO).collect(Collectors.toList()); 		 
//	 return listCustomerVO; 
		 
	}
	
	@Override
	public void updateProfile(CustomerUpdateVO customerVO) {
		//I have loaded entity inside persistence context - >>Session
		Customer customer=customerRepository.findById(customerVO.getCid()).get();
		try {
			customer.setImage(customerVO.getPhoto().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		customer.setName(customerVO.getName());
		customer.setMobile(customerVO.getMobile());
		customer.setDom(new Timestamp(new Date().getTime()));
		///customerRepository.save(customer);
	}
	
	@Override
	public byte[] findPhotoByAC(String accountNumber) {
		CustomerAccountInfo customerAccountInfo=customerAccountInfoRepository.findByAccountNumber(accountNumber).get();
		Customer customer=customerRepository.findByEmail(customerAccountInfo.getCustomerId().getLoginid()).get();
		return customer.getImage(); 
		
	}
	
	
	@Override
	public byte[] findPhotoByid(int cid) {
		Optional<Customer> optionalCustomer=customerRepository.findById(cid);
		if(optionalCustomer.isPresent()) {
			return optionalCustomer.get().getImage();
		}else {
			return null;
		}
		
	}
	
   @Override
	public List<RoleVO> getRoles(){
		
		List<Role> roles = roleRepository.findAll();
		List<RoleVO> rolesVO = new ArrayList<RoleVO>(); 
		for(Role role : roles) {
			System.out.println("MY ROLE========"+role.toString());
			RoleVO roleVO = new RoleVO();
			BeanUtils.copyProperties(role, roleVO);
			rolesVO.add(roleVO);
		}
		
		return rolesVO;
	}
	
   @Override
   public String findCustomerByEmail(String email) {
	   Optional<CustomerSaving> customer = customerAccountEnquiryRepository.findByEmail(email);  
	   String result = "";
	   if(customer.isPresent()) {
		   result = "fail";
	   }
	   else result="success";
	   
	   return result;
   }
   
   @Override
   public String findCustomerByMobile(String mobile) {
	   Optional<CustomerSaving> customer = customerAccountEnquiryRepository.findByMobile(mobile);  
	   String result = "";
	   if(customer.isPresent()) {
		   result = "fail";
	   }
	   else result="success";
	   
	   return result;
   }
   
   @Override
   public  CustomerVO searchCustomer(String searchKey){
	   Optional<Customer> customer = CustomerRepository.findByName(searchKey.trim());
	   CustomerVO customerVO = null;
	   if(customer.isPresent()) {
		   customerVO = new CustomerVO();
		   customerVO.setId(customer.get().getId());
		   customerVO.setName(customer.get().getName().trim());
		   customerVO.setEmail(customer.get().getEmail());
		   customerVO.setAddress(customer.get().getAddress());
		   customerVO.setMobile(customer.get().getMobile());
		   customerVO.setImage(customer.get().getImage());
		   
	   }
	   
	   return customerVO;
   }
   
   @Override
   public CustomerAccountInfoVO findCustomerAccountInfo(String customerId){
	   CustomerAccountInfo customerAccountInfo=customerAccountInfoRepository.findByLoginId(customerId).get();
	   CustomerAccountInfoVO accountInfoVO=new CustomerAccountInfoVO();
	   BeanUtils.copyProperties(customerAccountInfo, accountInfoVO);
	   
	   accountInfoVO.setName(customerAccountInfo.getAccountType().getName());
	   accountInfoVO.setAcccountType(customerAccountInfo.getAccountType().getName());
	   
	   return accountInfoVO;
   }

      @Override
    public List<AccountTypeVO> findAccountTypes() {
	     List<AccountType> accounts = accountTypeRepository.findAll();
	     List<AccountTypeVO> accountsVO =  new ArrayList<AccountTypeVO>();
	     for(AccountType account : accounts) {
		    AccountTypeVO accountVO = new AccountTypeVO();
		    BeanUtils.copyProperties(account, accountVO);
		     accountsVO.add(accountVO);
	      }
    	return accountsVO;
      }
 
     @Override
     public void addPayee(PayeeInfoVO payeeInfoVO) {
    	 //This is default status for payee

    	 int urn=Utils.generateURN();
    	 System.out.println("urn  = "+urn);
    	 //either send sms or email
    	 
    	 PayeeStatus payeeStatus = new PayeeStatus();
    	 payeeStatus.setId(1);
    	 
    	 PayeeInfo payeeInfo = new PayeeInfo();
		 BeanUtils.copyProperties(payeeInfoVO, payeeInfo);
		 payeeInfo.setUrn(urn);
		 payeeInfo.setPayeeStatus(payeeStatus);
		 payeeInfo.setDoe(new Timestamp(new Date().getTime()));
		 payeeInfo.setDom(new Timestamp(new Date().getTime()));
		 payeeRepository.save(payeeInfo);
     }
     
	 @Override
	 public List<PayeeInfoVO> pendingPayeeList(){
		   return payeeRepository.findAll().stream().filter(t->t.getPayeeStatus().getId()==1).map(pi->{
			    PayeeInfoVO piVO = new PayeeInfoVO();
			    piVO.setPayeeStatus(pi.getPayeeStatus().getName());
			    BeanUtils.copyProperties(pi, piVO);
			    return piVO;
		   }).collect(Collectors.toList());
		   
		// List<PayeeInfo> payeeInfoList =  payeeRepository.findAll();
/*		   
		   List<PayeeInfoVO> payeeInfoVOList = new ArrayList<PayeeInfoVO>();
		   for(PayeeInfo pi : payeeInfoList) {
			    PayeeInfoVO piVO = new PayeeInfoVO();
			    piVO.setPayeeStatus(pi.getPayeeStatus().getName());
			    BeanUtils.copyProperties(pi, piVO);
			    payeeInfoVOList.add(piVO);
		   }
		   
		   return payeeInfoVOList;
*/	   }

	 @Override
	 public List<PayeeInfoVO> registeredPayeeList(String customerId){
		   
		   List<PayeeInfo> payeeInfoList =  payeeRepository.findRegisteredPayee(customerId);
		   List<PayeeInfoVO> payeeInfoVOList = new ArrayList<PayeeInfoVO>();
		   for(PayeeInfo pi : payeeInfoList) {
			    PayeeInfoVO piVO = new PayeeInfoVO();
			    piVO.setPayeeStatus(pi.getPayeeStatus().getName());
			    BeanUtils.copyProperties(pi, piVO);
			    payeeInfoVOList.add(piVO);
		   }
		   
		   return payeeInfoVOList;
	   }
	 
	 @Override
	 public String approveDisApprovePayee(PayeeApproveVO payeeApproveVO){
		 Optional<PayeeInfo> optional=payeeRepository.findByUrnAndPayeeAccountNo(payeeApproveVO.getUrn(), payeeApproveVO.getAccountNumber());
         if(optional.isPresent()){	
        	 //Below 4 lines are updating payee status
        	 PayeeInfo payeeInfo=optional.get();
        	 PayeeStatus payeeStatus=new PayeeStatus();
        	 payeeStatus.setId(2);
        	 payeeInfo.setPayeeStatus(payeeStatus);
        	 
            return "approved";	 
         }else{
           return "notapproved"; 	 
         }
		   
	 }
	 
	 @Override
	public void updatePayee(UpdatePayeeVO updatePayeeVO) {
			//This is deleting entity by id
		Optional<PayeeInfo> optional=payeeRepository.findById(updatePayeeVO.getPayeeid());
		if(optional.isPresent()){
			optional.get().setPayeeNickName(updatePayeeVO.getPayeeNickName());
		}
		/* payeeRepository.findById(updatePayeeVO.getPayeeid()).
		 ifPresent(tt->tt.setPayeeName(updatePayeeVO.getPayeeNickName()));
		*/ 
	}

	@Override
	public void deletePayee(int payeeId) {
		//This is deleting entity by id
		payeeRepository.deleteById(payeeId);
	}

	@Override
	public  void deleteCustmoer(String email) {
		

	 customerRepository.deleteByEmail(email);
      
	
	}

	@Override
	public void deleteCustmoerAccount(long id) {
	
		customerAccountInfoRepository.deleteById(id);
	}

	@Override
	public CustomerVO findCustomer(String email) {
		Optional<Customer> customer	=customerRepository.findByEmail(email);
		 CustomerVO customerVO = null;
		   if(customer.isPresent()) {
			   customerVO = new CustomerVO();
			   customerVO.setName(customer.get().getName());
			   customerVO.setId(customer.get().getId());
			   customerVO.setEmail(customer.get().getEmail());
			   customerVO.setAddress(customer.get().getAddress());
			   customerVO.setMobile(customer.get().getMobile());
			   customerVO.setGender(customer.get().getGender());
			   customerVO.setImage(customer.get().getImage());
			   
		   }
		   System.out.println("profile:::::"+customerVO);
		  // System.out.println("photo::::::"+customerVO.getImage());
		return customerVO;
	}

	@Override
	public void updatePhoto(CustomerVO customerVO) {
	 Customer customer= new Customer()	;
		try {
			customerVO.setImage(customerVO.getFile().getBytes());
			BeanUtils.copyProperties(customerVO, customer);
			customerRepository.save(customer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public FundTransferVO executeTransaction(FundTransferVO fundTransferVO) {
		CustomerTransaction customerTransaction=new CustomerTransaction();
		customerTransaction.setAmount(fundTransferVO.getAmount());
		customerTransaction.setBankName("ICICI Bank");
		customerTransaction.setDetails(fundTransferVO.getRemarks());
		customerTransaction.setDot(new Timestamp(new Date().getTime()));
		String fromAccount=fundTransferVO.getFromAccount().split("-")[0];
		String toAccount=fundTransferVO.getToAccount().split("-")[0];
		customerTransaction.setFromAccount(fromAccount);
		customerTransaction.setToAccout(toAccount);
		customerTransaction.setTransactionSchedule("no");
		customerTransaction.setTxStatus("PROCESSED");
		customerTransaction.setTxType("IMMEDIATE");
		customerTransaction.setTransactionId("TX"+TransactionIdGeneratorUtils.randomDecimalString(16));
		customerTransactionRepository.save(customerTransaction);
		fundTransferVO.setFromAccount(fromAccount);
		fundTransferVO.setToAccount(toAccount);
		fundTransferVO.setTransactionId(customerTransaction.getTransactionId());
		fundTransferVO.setDot(customerTransaction.getDot());
		return fundTransferVO;
	
	}

}
