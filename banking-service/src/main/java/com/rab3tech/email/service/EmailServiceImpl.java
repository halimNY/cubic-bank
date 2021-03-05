package com.rab3tech.email.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.CustomerTransactionsService;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerTransactionVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.EmailVO;

@Service
public class EmailServiceImpl implements EmailService{
	
	//
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
    private SpringTemplateEngine templateEngine;
	
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerService customerService ;
	@Autowired
	private CustomerTransactionsService customerTransactionsService ;
	
	@Override
	@Async("threadPool")
	public String sendRegistrationEmail(EmailVO mail)  {
		
		
		
		 try {
		    MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
	        Context context = new Context();
	        Map<String,Object> props=new HashMap<>();
	        
	
	        props.put("name", mail.getName());
	        props.put("sign", "By Cubic Bank");
	        props.put("location", "Fremont CA100 , USA");
	        props.put("email", "javahunk2020@gmail.com");
	        props.put("registrationlink", mail.getRegistrationlink());
	        context.setVariables(props);
	        String html = templateEngine.process("send-registration-link", context);
	        helper.setTo(mail.getTo());
	        helper.setText(html, true);
	        helper.setSubject(mail.getSubject());
	        helper.setFrom(mail.getFrom());
	        
		        File cfile=new ClassPathResource("images/registration-banner.png", EmailServiceImpl.class.getClassLoader()).getFile();
		        byte[] cbytes=Files.readAllBytes(cfile.toPath());
		        InputStreamSource cimageSource =new ByteArrayResource(cbytes);
	        helper.addInline("cb", cimageSource, "image/png");
	        
	        
	        File file=new ClassPathResource("images/bank-icon.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] bytes=Files.readAllBytes(file.toPath());
	        InputStreamSource imageSource =new ByteArrayResource(bytes);
	        helper.addInline("bankIcon", imageSource, "image/png");
	        
	        javaMailSender.send(message);
	        
	       
		 }catch (Exception e) {
			e.printStackTrace();
		 }   
	        return "done";
	}
	
	

	@Override
	@Async("threadPool")
	public String sendLockAndUnlockEmail(EmailVO mail)  {
		
		 try {
		    MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
	        Context context = new Context();
	        Map<String,Object> props=new HashMap<>();
	        props.put("name", mail.getName());
	        props.put("message", mail.getBody());
	        props.put("sign", "Banking Application");
	        props.put("location", "Fremont CA100 , USA");
	        props.put("email", "javahunk2020@gmail.com");
	        context.setVariables(props);
	        
	        String html = templateEngine.process("account-lock-unlock", context);
	        helper.setTo(mail.getTo());
	        helper.setText(html, true);
	        helper.setSubject("Regarding your account status.");
	        helper.setFrom(mail.getFrom());
	       Customer customer= customerRepository.findByEmail(mail.getTo()).get();
	        InputStreamSource cimageSource =new ByteArrayResource(customer.getImage());
	        helper.addInline("photo", cimageSource, "image/png");
	        
	        File file=new ClassPathResource("images/bank-icon.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] bytes=Files.readAllBytes(file.toPath());
	        InputStreamSource imageSource =new ByteArrayResource(bytes);
	        helper.addInline("bankIcon", imageSource, "image/png");
	        
	        javaMailSender.send(message);
		 }catch (Exception e) {
			e.printStackTrace();
		 }   
	        return "done";
	}
	
	
	@Override
	@Async("threadPool")
	public String sendUsernamePasswordEmail(EmailVO mail)  {
		
		 try {
				System.out.println(" user name from sendUsernamePasswordEmail :::: "+mail.getCheckId());
			 

		    MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
	        Context context = new Context();
	        Map<String,Object> props=new HashMap<>();
	        props.put("name", mail.getName());
	        props.put("username", mail.getUsername());
	        props.put("password", mail.getPassword());
	        props.put("sign", "Banking Application");
	        props.put("location", "Fremont CA100 , USA");
	        props.put("email", "javahunk2020@gmail.com");
	        System.out.println("from email form:::::"+mail.getUsername());
	        String userName=mail.getUsername();
	        CustomerAccountInfoVO customerAccountInfo=customerService.findCustomerAccountInfo(userName);
	        props.put("accoutNumber", customerAccountInfo.getAccountNumber());
	        props.put("balance", customerAccountInfo.getAvBalance());
	        props.put("accountType", customerAccountInfo.getAccountType());
	        props.put("accountStatus", "active");
	   
	        context.setVariables(props);
	        String html = templateEngine.process("password-email-template", context);
	        helper.setTo(mail.getTo());
	        helper.setText(html, true);
	        helper.setSubject("Regarding your banking username and password.");
	        helper.setFrom(mail.getFrom());
	        File cfile=new ClassPathResource("images/password.jpg", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] cbytes=Files.readAllBytes(cfile.toPath());
	        InputStreamSource cimageSource =new ByteArrayResource(cbytes);
	        helper.addInline("cb", cimageSource, "image/png");
	        
	        File file=new ClassPathResource("images/bank-icon.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] bytes=Files.readAllBytes(file.toPath());
	        InputStreamSource imageSource =new ByteArrayResource(bytes);
	        helper.addInline("bankIcon", imageSource, "image/png");
	        
	        javaMailSender.send(message);
		 }catch (Exception e) {
			e.printStackTrace();
		 }   
	        return "done";
	}

	
	@Override
	@Async("threadPool")
	public String sendEquiryEmail(EmailVO mail)  {
		
		 try {
		    MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
	        Context context = new Context();
	        Map<String,Object> props=new HashMap<>();
	        props.put("name", mail.getName());
	        props.put("sign", "Banking Application");
	        props.put("location", "Fremont CA100 , USA");
	        props.put("email", "javahunk2020@gmail.com");
	        context.setVariables(props);
	        String html = templateEngine.process("enquiry-email-template", context);
	        helper.setTo(mail.getTo());
	        helper.setText(html, true);
	        helper.setSubject("Regarding Account enquiry to open an account.");
	        helper.setFrom(mail.getFrom());
	        
	        
	        File cfile=new ClassPathResource("images/cb1.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] cbytes=Files.readAllBytes(cfile.toPath());
	        InputStreamSource cimageSource =new ByteArrayResource(cbytes);
	        helper.addInline("cb", cimageSource, "image/png");
	        
	        
	        File file=new ClassPathResource("images/bank-icon.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] bytes=Files.readAllBytes(file.toPath());
	        InputStreamSource imageSource =new ByteArrayResource(bytes);
	        helper.addInline("bankIcon", imageSource, "image/png");
	        
	        javaMailSender.send(message);
		 }catch (Exception e) {
			e.printStackTrace();
		 }   
	        return "done";
	}



	@Override
	public String sendCustomerTransactions(EmailVO mail) {
		 try {
				System.out.println(" username email service  :::: "+mail.getUsername());
			 

		    MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
	        String userName=mail.getUsername();
	        CustomerAccountInfoVO customerAccountInfo=customerService.findCustomerAccountInfo(userName);
		   
	        List<CustomerTransactionVO> customerTransactionVOList =customerTransactionsService.findAllTransactions(customerAccountInfo.getAccountNumber());	
	        Context context = new Context();
	        Map<String,Object> props=new HashMap<>();
	        props.put("name", mail.getName());
	        props.put("username", mail.getUsername());
	        props.put("sign", "Banking Application");
	        props.put("location", "Fremont CA100 , USA");
	        props.put("email", "javahunk2020@gmail.com");

	        for (CustomerTransactionVO transaction : customerTransactionVOList) {
	        	props.put("bank",transaction.getBankName());
	        	props.put("ammount",transaction.getAmount());
	        	props.put("date",transaction.getDot());
	        	props.put("from",transaction.getFromAccount());
	        
			}	
	    	context.setVariables(props);    
	    	context.setVariable("list", customerTransactionVOList);    
	        String html = templateEngine.process("transactions-email-template", context);
	        helper.setTo(mail.getTo());
	        helper.setText(html, true);
	        helper.setSubject("Regarding your banking username and password.");
	        helper.setFrom(mail.getFrom());
	        //Sending customer photo in the email
	        Customer customer= customerRepository.findByEmail(mail.getTo()).get();
	        System.out.println("customer from email ::::"+customer);
	        byte[] photo=new byte[]{};
	        if(customer.getImage()!=null){
	        	photo=customer.getImage();
	        }
	        System.out.println("photo from email ::::"+photo);
	        InputStreamSource cimageSource =new ByteArrayResource(photo);
	        helper.addInline("photo", cimageSource, "image/png");
	        
	        File file=new ClassPathResource("images/bank-icon.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] bytes=Files.readAllBytes(file.toPath());
	        InputStreamSource imageSource =new ByteArrayResource(bytes);
	        helper.addInline("bankIcon", imageSource, "image/png");
	        
	        javaMailSender.send(message);
		 }catch (Exception e) {
			e.printStackTrace();
		 }   
	        return "done";
		
	}



	@Override
	public String customerSendEmail(EmailVO mail) {
		
		 try {
		    MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message,
	                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
	                StandardCharsets.UTF_8.name());
		   
	        Context context = new Context();
	        Map<String,Object> props=new HashMap<>();
	        props.put("sender", mail.getFrom());
	        props.put("subject", mail.getSubject());
	        props.put("body", mail.getBody());
	        
	    	context.setVariables(props);      
	        String html = templateEngine.process("customer-send-email-template", context);
	        helper.setTo(mail.getTo());
	        helper.setText(html, true);
	        helper.setSubject("Regarding your banking username and password.");
	        helper.setFrom(mail.getFrom());
	        File cfile=new ClassPathResource("images/password.jpg", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] cbytes=Files.readAllBytes(cfile.toPath());
	        InputStreamSource cimageSource =new ByteArrayResource(cbytes);
	        helper.addInline("cb", cimageSource, "image/png");
	        File file=new ClassPathResource("images/bank-icon.png", EmailServiceImpl.class.getClassLoader()).getFile();
	        byte[] bytes=Files.readAllBytes(file.toPath());
	        InputStreamSource imageSource =new ByteArrayResource(bytes);
	        helper.addInline("bankIcon", imageSource, "image/png");
	        
	        javaMailSender.send(message);
		 }catch (Exception e) {
			e.printStackTrace();
		 }   
	        return "done";
	}

}
