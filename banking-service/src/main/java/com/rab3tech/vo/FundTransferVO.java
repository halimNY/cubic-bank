package com.rab3tech.vo;

import java.sql.Timestamp;

public class FundTransferVO {
	private String fromAccount;
	private String toAccount;
	private String remarks;
	private double amount;
	private int otp;
	private Timestamp dot;
	private String transactionId;
	
	public Timestamp getDot() {
		return dot;
	}

	public void setDot(Timestamp dot) {
		this.dot = dot;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "FundTransferVO [fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", remarks=" + remarks
				+ ", amount=" + amount + "]";
	}

}
