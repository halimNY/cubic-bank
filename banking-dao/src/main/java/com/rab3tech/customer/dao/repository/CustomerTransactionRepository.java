package com.rab3tech.customer.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rab3tech.dao.entity.CustomerTransaction;

/**
 * 
 * @author nagendra
 * comment
 * 
 * Spring Data JPA repository
 *
 */

public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, Integer> {
	
	public List<CustomerTransaction> findByFromAccount(String fromAccount);
	
}

