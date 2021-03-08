package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import com.rab3tech.customer.dao.repository.CustomerAccountInfoRepository;
import com.rab3tech.customer.dao.repository.CustomerTransactionRepository;
import com.rab3tech.dao.entity.CustomerTransaction;
import com.rab3tech.utils.AccountStatusEnum;
import com.rab3tech.vo.CustomerTransactionVO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerTransactionsServiceImplTest {
	
	@Mock
	private CustomerTransactionRepository customerTransactionRepository;

	@Mock
	private CustomerAccountInfoRepository customerAccountInfoRepository;

	@InjectMocks
	private CustomerTransactionsServiceImpl customerTransactionServiceImpl;
	
	@Before
	public void initializer(){
	
		MockitoAnnotations.initMocks(this); 
	}
	@Test
	public void testFindCustomerTransaction() {
		/*
		 public List<CustomerTransactionVO> findAllTransactions(String fromAccount) {
		 List<CustomerTransactionVO> customerTransactionVOList = new  ArrayList<CustomerTransactionVO>();
		List<CustomerTransaction> customerTransactionList=customerTransactionRepository.findByFromAccount(fromAccount);
		for (CustomerTransaction customerTransaction : customerTransactionList) {
			CustomerTransactionVO customerTransactionVO= new CustomerTransactionVO();
			BeanUtils.copyProperties(customerTransaction, customerTransactionVO);
			customerTransactionVOList.add(customerTransactionVO);
		}  return customerTransactionVOList; */
		
		//We are writing JUnit for findCustomerTransaction
		List<CustomerTransaction> customerTransactionList = new ArrayList<CustomerTransaction>();
		CustomerTransaction customerTransaction=new CustomerTransaction();
		customerTransaction.setAmount(122);
		customerTransaction.setBankName("Aba");
		customerTransaction.setFromAccount("9383726262");
		customerTransaction.setTransactionId("Tx828272");
		customerTransactionList.add(customerTransaction);
		CustomerTransaction customerTransaction2=new CustomerTransaction();
		customerTransaction2.setAmount(34);
		customerTransaction2.setBankName("Magic Tech");
		customerTransaction2.setFromAccount("9383726262");
		customerTransaction2.setTransactionId("tx723636633");
		customerTransactionList.add(customerTransaction2);

		
		when(customerTransactionRepository.findByFromAccount("9383726262")).thenReturn(customerTransactionList);
		List<CustomerTransactionVO> findAllTransactions=customerTransactionServiceImpl.findAllTransactions("9383726262");
		assertTrue(findAllTransactions!=null);
		assertTrue(findAllTransactions.size()==2);
		assertEquals("Tx828272", findAllTransactions.get(0).getTransactionId());
		assertEquals("Aba", findAllTransactions.get(0).getBankName());
		assertEquals("tx723636633", findAllTransactions.get(1).getTransactionId());
		assertEquals("Magic Tech", findAllTransactions.get(1).getBankName());
	
		
		
	}
	

	
}
