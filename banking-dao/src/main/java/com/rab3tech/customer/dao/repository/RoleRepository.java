package com.rab3tech.customer.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rab3tech.dao.entity.Login;
import com.rab3tech.dao.entity.Role;

/**
 * 
 * @author nagendra
 * 
 * Spring Data JPA repository
 *
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	
}

