package com.rab3tech.customer.service;

import java.util.List;
import java.util.Optional;

import com.rab3tech.vo.AccountTypeVO;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.CustomerUpdateVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.FundTransferVO;
import com.rab3tech.vo.PayeeApproveVO;
import com.rab3tech.vo.PayeeInfoVO;
import com.rab3tech.vo.RoleVO;
import com.rab3tech.vo.UpdatePayeeVO;

public interface CustomerService {

	CustomerVO createAccount(CustomerVO customerVO);

	CustomerAccountInfoVO createBankAccount(int csaid);

	List<CustomerVO> findCustomers();

	byte[] findPhotoByid(int cid);

	void updateProfile(CustomerUpdateVO customerVO);
	
	public List<RoleVO> getRoles();

	CustomerVO searchCustomer(String name);
	
	List<AccountTypeVO> findAccountTypes();

	String findCustomerByEmail(String email);
	CustomerVO findCustomer(String email);

	String findCustomerByMobile(String mobile);

	void addPayee(PayeeInfoVO payeeInfoVO);

	List<PayeeInfoVO> pendingPayeeList();

	List<PayeeInfoVO> registeredPayeeList(String customerId);
	

	String approveDisApprovePayee(PayeeApproveVO payeeApproveVO);

	byte[] findPhotoByAC(String accountId);

	CustomerAccountInfoVO findCustomerAccountInfo(String customerId);

	void updateCustomerLockStatus(String userid, String status);

	void deletePayee(int payeeId);

	void updatePayee(UpdatePayeeVO updatePayeeVO);
	void deleteCustmoer(String email);

     void deleteCustmoerAccount(long id) ;

	void updatePhoto(CustomerVO customerVO);

	FundTransferVO executeTransaction(FundTransferVO fundTransferVO);

	

}
