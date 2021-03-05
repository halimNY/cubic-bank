package com.rab3tech.email.service;

import java.util.List;

import com.rab3tech.vo.CustomerTransactionVO;
import com.rab3tech.vo.EmailVO;

public interface EmailService {

	String sendEquiryEmail(EmailVO mail)  ;

	String sendRegistrationEmail(EmailVO mail);

	String sendUsernamePasswordEmail(EmailVO mail);

	String sendLockAndUnlockEmail(EmailVO mail);
	String sendCustomerTransactions(EmailVO mail);
	String customerSendEmail(EmailVO mail);

}
