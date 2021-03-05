package com.rab3tech.customer.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rab3tech.customer.dao.repository.CustomerAccountInfoRepository;
import com.rab3tech.customer.dao.repository.CustomerTransactionRepository;
import com.rab3tech.customer.service.CustomerTransactionsService;
import com.rab3tech.dao.entity.CustomerTransaction;
import com.rab3tech.vo.CustomerTransactionVO;

@Service
@Transactional
public class CustomerTransactionsServiceImpl  implements CustomerTransactionsService{
	
	@Autowired
	private CustomerTransactionRepository customerTransactionRepository ;
	

	@Override
	public List<CustomerTransactionVO> findAllTransactions(String fromAccount) {
		
		List<CustomerTransactionVO> customerTransactionVOList = new  ArrayList<CustomerTransactionVO>();
		List<CustomerTransaction> customerTransactionList=customerTransactionRepository.findByFromAccount(fromAccount);
		
		for (CustomerTransaction customerTransaction : customerTransactionList) {
			CustomerTransactionVO customerTransactionVO= new CustomerTransactionVO();
			BeanUtils.copyProperties(customerTransaction, customerTransactionVO);
			customerTransactionVOList.add(customerTransactionVO);
		}

		return customerTransactionVOList;
	}
	


}
