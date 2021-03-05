package com.rab3tech.vo;

import java.util.List;

public class EmailVO {
	private String  checkId;
	private String name;
	private String to;
	private String from;
	private String subject;
	private String body;
	private String registrationlink;
	private String username;
	private String password;
	private List<CustomerTransactionVO> transactions;

	
	public EmailVO() {}

	public EmailVO(String to, String from, String subject, String body,String name) {
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.name=name;
		this.body = body;
	}
	
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegistrationlink() {
		return registrationlink;
	}

	public void setRegistrationlink(String registrationlink) {
		this.registrationlink = registrationlink;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailVO other = (EmailVO) obj;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public List<CustomerTransactionVO> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<CustomerTransactionVO> customerTransactionVOList) {
		this.transactions = customerTransactionVOList;
	}
	
	

}
