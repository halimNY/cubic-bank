package com.rab3tech.customer.ui.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Base64;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.customer.service.CustomerTransactionsService;
import com.rab3tech.customer.service.LocationService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.customer.service.impl.SecurityQuestionService;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.vo.ChangePasswordVO;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.CustomerSecurityQueAnsVO;
import com.rab3tech.vo.CustomerTransactionVO;
import com.rab3tech.vo.CustomerVO;
import com.rab3tech.vo.EmailVO;
import com.rab3tech.vo.FundTransferVO;
import com.rab3tech.vo.LoginVO;
import com.rab3tech.vo.PayeeInfoVO;

/**
 * 
 * @author nagendra This class for customer GUI
 *
 */
@Controller
//@RequestMapping("/customer")
public class CustomerUIController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerUIController.class);

	@Autowired
	private CustomerEnquiryService customerEnquiryService;

	@Autowired
	private SecurityQuestionService securityQuestionService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private LocationService locationService;
	@Autowired
	private CustomerTransactionsService customerTransactionsService;

	@GetMapping("/customer/forget/password")
	public String forgetPassword() {
		// spring.thymeleaf.prefix=classpath:/src/main/resources/templates/
		return "customer/forgetPassword"; // forgetPassword.html
	}

	@PostMapping("/customer/changePassword")
	public String saveCustomerQuestions(@ModelAttribute ChangePasswordVO changePasswordVO, Model model,
			HttpSession session) {
		LoginVO loginVO2 = (LoginVO) session.getAttribute("userSessionVO");
		String loginid = loginVO2.getUsername();
		changePasswordVO.setLoginid(loginid);
		String viewName = "customer/dashboard";
		boolean status = loginService.checkPasswordValid(loginid, changePasswordVO.getCurrentPassword());
		if (status) {
			if (changePasswordVO.getNewPassword().equals(changePasswordVO.getConfirmPassword())) {
				viewName = "customer/dashboard";
				loginService.changePassword(changePasswordVO);
			} else {
				model.addAttribute("error", "Sorry , your new password and confirm passwords are not same!");
				return "customer/login"; // login.html
			}
		} else {
			model.addAttribute("error", "Sorry , your username and password are not valid!");
			return "customer/login"; // login.html
		}
		return viewName;
	}

	@PostMapping("/customer/securityQuestion")
	public String saveCustomerQuestions(
			@ModelAttribute("customerSecurityQueAnsVO") CustomerSecurityQueAnsVO customerSecurityQueAnsVO, Model model,
			HttpSession session) {
		LoginVO loginVO2 = (LoginVO) session.getAttribute("userSessionVO");
		String loginid = loginVO2.getUsername();
		customerSecurityQueAnsVO.setLoginid(loginid);
		securityQuestionService.save(customerSecurityQueAnsVO);
		//
		return "customer/chagePassword";
	}

	// http://localhost:444/customer/account/registration?cuid=1585a34b5277-dab2-475a-b7b4-042e032e8121603186515
	@GetMapping("/customer/account/registration")
	public String showCustomerRegistrationPage(@RequestParam String cuid, Model model) {

		logger.debug("cuid = " + cuid);
		Optional<CustomerSavingVO> optional = customerEnquiryService.findCustomerEnquiryByUuid(cuid);
		CustomerVO customerVO = new CustomerVO();

		if (!optional.isPresent()) {
			return "customer/error";
		} else {
			// model is used to carry data from controller to the view =- JSP/
			CustomerSavingVO customerSavingVO = optional.get();
			customerVO.setEmail(customerSavingVO.getEmail());
			customerVO.setName(customerSavingVO.getName());
			customerVO.setMobile(customerSavingVO.getMobile());
			customerVO.setAddress(customerSavingVO.getLocation());
			customerVO.setToken(cuid);
			logger.debug(customerSavingVO.toString());
			// model - is hash map which is used to carry data from controller to thyme
			// leaf!!!!!
			// model is similar to request scope in jsp and servlet
			model.addAttribute("customerVO", customerVO);
			return "customer/customerRegistration"; // thyme leaf
		}
	}

	@PostMapping("/customer/account/registration")
	public String createCustomer(@ModelAttribute CustomerVO customerVO, Model model) {
		// saving customer into database
		logger.debug(customerVO.toString());
		customerVO = customerService.createAccount(customerVO);
		// Write code to send email

		EmailVO mail = new EmailVO(customerVO.getEmail(), "javahunk2020@gmail.com",
				"Regarding Customer " + customerVO.getName() + "  userid and password", "", customerVO.getName());
		mail.setUsername(customerVO.getEmail());
		mail.setPassword(customerVO.getPassword());
		mail.setCheckId(customerVO.getUserid());
		emailService.sendUsernamePasswordEmail(mail);

		model.addAttribute("loginVO", new LoginVO());
		model.addAttribute("message", "Your account has been setup successfully , please check your email.");
		return "customer/login";
	}

	@GetMapping("/customers/acphoto")
	public void findCustomerPhotoByAc(@RequestParam String accNumber, HttpServletResponse response) throws IOException {
		byte[] photo = customerService.findPhotoByAC(accNumber);
		response.setContentType("image/png");
		ServletOutputStream outputStream = response.getOutputStream();
		if (photo != null) {
			outputStream.write(photo);
		} else {
			outputStream.write(new byte[] {});
		}
		outputStream.flush();
		outputStream.close();
	}

	/*
	 * @GetMapping(value = { "/customer/account/enquiry", "/", "/mocha", "/welcome"
	 * }) public String showCustomerEnquiryPage(Model model) { CustomerSavingVO
	 * customerSavingVO = new CustomerSavingVO(); // model is map which is used to
	 * carry object from controller to view model.addAttribute("customerSavingVO",
	 * customerSavingVO); return "customer/customerEnquiry"; // customerEnquiry.html
	 * }
	 */

	@GetMapping(value = { "/customer/account/enquiry", "/", "/mocha", "/welcome" })
	public String showCustomerEnquiryPage(Model model) {
		// LoadLocationAndAccountVO loadLocationAndAccountVOs = new
		// LoadLocationAndAccountVO();
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();

		model.addAttribute("customerSavingVO", customerSavingVO);
		return "customer/customerEnquiry"; // customerEnquiry.html
	}

	@PostMapping("/customer/account/enquiry")
	public String submitEnquiryData(@Valid @ModelAttribute CustomerSavingVO customerSavingVO, BindingResult result,
			Model model) {

		if (result.hasErrors()) {
			return "customer/customerEnquiry";
		}

		boolean status = customerEnquiryService.emailNotExist(customerSavingVO.getEmail());
		logger.info("Executing submitEnquiryData");
		if (status) {
			CustomerSavingVO response = customerEnquiryService.save(customerSavingVO);
			logger.debug("Hey Customer , your enquiry form has been submitted successfully!!! and appref "
					+ response.getAppref());
			model.addAttribute("message",
					"Hey Customer , your enquiry form has been submitted successfully!!! and appref "
							+ response.getAppref());
		} else {
			model.addAttribute("message", "Sorry , this email is already in use " + customerSavingVO.getEmail());
		}
		return "customer/success"; // customerEnquiry.html

	}

	@GetMapping("/customer/app/status")
	public String customerAppStatus() {

		return "customer/appstatus";
	}

	@GetMapping("/customer/customerSearch")
	public String customerSearch(Model model) {
		List<CustomerVO> customerVOs = customerService.findCustomers();
		model.addAttribute("customerVOs", customerVOs);
		return "customer/customerSearch";
	}

	@GetMapping("/customer/addPayee")
	public String customerAddPayee(Model model) {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		model.addAttribute("payeeInfoVO", payeeInfoVO);
		return "customer/addPayee";
	}

	@PostMapping("/customer/account/addPayee")
	public String newPayee(@Valid @ModelAttribute("payeeInfoVO") PayeeInfoVO payeeInfoVO, BindingResult bindingResult,
			HttpSession session, Model model) {

		if (payeeInfoVO.getAccNumberConfirm() != null
				&& !payeeInfoVO.getAccNumberConfirm().equalsIgnoreCase(payeeInfoVO.getPayeeAccountNo())) {
			// ObjectError objectError=new ObjectError("accNumberConfirm", "Hey!, your
			// account and confirm account are not same");
			bindingResult.rejectValue("accNumberConfirm", "account.msg", "An account already exists for this email.");
			// bindingResult.addError(objectError);
		}

		if (bindingResult.hasErrors()) {
			return "customer/addPayee";
		}

		LoginVO loginVO = (LoginVO) session.getAttribute("userSessionVO");
		payeeInfoVO.setCustomerId(loginVO.getUsername());
		System.out
				.println("MY CUSTOMER USERID =========================================" + payeeInfoVO.getCustomerId());
		// String loginId = loginService.findUserByName(payeeInfoVO.getPayeeName());
		// payeeInfoVO.setCustomerId(loginId);
		customerService.addPayee(payeeInfoVO);
		model.addAttribute("successMessage", "Payee added successfully");
		return "redirect:/customer/dashboard";
	}

	@GetMapping("/customer/pendingPayee")
	public String pendinPayeeList(Model model) {
		List<PayeeInfoVO> payeeInfoList = customerService.pendingPayeeList();
		model.addAttribute("payeeInfoList", payeeInfoList);
		return "customer/pendingPayee";

	}

	@GetMapping("/customer/registeredPayee")
	public String registeredPayeeList(Model model,HttpSession session) {
		LoginVO loginVO = (LoginVO) session.getAttribute("userSessionVO");
		List<PayeeInfoVO> payeeInfoList = customerService.registeredPayeeList(loginVO.getUsername());
		model.addAttribute("payeeInfoList", payeeInfoList);
		return "customer/registeredPayee";

	}
//-----------------------------------------------------------------------------------
	@GetMapping("/customer/fundTransfer")
	public String fundTransfer(Model model ) {
		FundTransferVO fundTransferVO = new FundTransferVO();
		System.out.println(" get fundTransferVO ::.:::"+fundTransferVO);
	model.addAttribute("fundTransferVO", fundTransferVO);
		return "customer/fundTransfer";
	}
	//-----------------------------------------------------------------------------------

	@PostMapping("/customer/fundTransfer")
	public String fundTransferPost(@ModelAttribute("fundTransferVO") FundTransferVO fundTransferVO, Model model) {
		System.out.println(" post fundTransferVO ::.:::"+fundTransferVO);
		return "customer/fundTransferReview";
	}
	//-----------------------------------------------------------------------------------

	@PostMapping("/customer/fundTransferSubmit")
	public String fundTransferSubmit(@ModelAttribute  FundTransferVO fundTransferVO, Model model) {
//	fundTransferVO.setFromAccount("002016940020-SAVING-SAVING");
 //   fundTransferVO.setToAccount("001992380954 - javahunk100@gmail.com");
		
		System.out.println(" validate fundTransferVO ::.:::"+fundTransferVO);
		// validate OTP
		// deduct money from sender and credit to account
		// Make a transaction history
		// make a su9mmary etc
		fundTransferVO=customerService.executeTransaction(fundTransferVO);
		//Email and SMS functionality
		model.addAttribute("fundTransferVO", fundTransferVO);
		return "customer/fundSummary";
	}
	//-----------------------------------------------------------------------------------

	private byte[] generatedCreditCard(String cardNumber, String exp, String name) {
		byte[] photo = new byte[] {};

		Resource resource = new ClassPathResource("images/credit-card-front-template.jpg");

		try {
			InputStream resourceInputStream = resource.getInputStream();

			Image src = ImageIO.read(resourceInputStream);

			int wideth = src.getWidth(null);
			int height = src.getHeight(null);

			BufferedImage tag = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = tag.createGraphics();

			g.setBackground(new Color(200, 250, 200));
			g.clearRect(0, 0, wideth, height);
			g.setColor(Color.WHITE);
			g.drawImage(src, 0, 0, wideth, height, null);

			// credit card number
			g.setFont(new Font("Lucida Console", Font.BOLD, 36));
			g.drawString(cardNumber.substring(0, 4), 40, 207);
			g.drawString(cardNumber.substring(4, 8), 150, 207);
			g.drawString(cardNumber.substring(8, 12), 260, 207);
			g.drawString(cardNumber.substring(12, 16), 370, 207);

			// exp date
			g.setFont(new Font("Lucida Console", Font.PLAIN, 24));
			g.drawString(exp, 65, 250);

			// customer name
			g.setFont(new Font("Tahoma", Font.PLAIN, 28));
			g.drawString(name.toUpperCase(), 30, 290);

			// cardType
			g.setFont(new Font("Lucida Console", Font.ITALIC, 20));
			g.drawString("VISA", 120, 20);

			// load new image
			Resource simage = new ClassPathResource("images/logo.png");
			InputStream simageInputStream = simage.getInputStream();
			Image img = ImageIO.read(simageInputStream);
			// Draw image on given card
			g.drawImage(img, 304, 255, 91, 45, null);

			g.dispose();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(tag, "jpg", baos);
			baos.flush();
			photo = baos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return photo;
	}

	private String generateCreditCardNumber() {
		Random random = new Random();
		StringBuilder number = new StringBuilder();
		number.append(String.format("%04d", random.nextInt(10000)));
		number.append(String.format("%04d", random.nextInt(10000)));
		number.append(String.format("%04d", random.nextInt(10000)));
		number.append(String.format("%04d", random.nextInt(10000)));
		return number.toString();
	}

	private String generateCreditCardExpireDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
		LocalDate date = LocalDate.now();
		date = date.plusYears(3);
		return formatter.format(date);
	}

	private String generateCCVNumber() {
		Random random = new Random();
		StringBuilder number = new StringBuilder();
		number.append(String.format("%03d", random.nextInt(1000)));
		return number.toString();
	}

	@GetMapping("/customers/credit/card")
	public void findCustomerCreditCard(@RequestParam String name, @RequestParam String email,
			HttpServletResponse response) throws IOException {
		byte[] card = generatedCreditCard(generateCreditCardNumber(), generateCreditCardExpireDate(), name);
		response.setContentType("image/png");
		ServletOutputStream outputStream = response.getOutputStream();
		if (card != null) {
			outputStream.write(card);
		} else {
			outputStream.write(new byte[] {});
		}
		outputStream.flush();
		outputStream.close();
	}

	// ------------------------------------------------------------------------------
	@GetMapping("/customers/accountSummary")
	public String customerAccountSummary(Model model, HttpSession session) {

		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");

		String userName = loginService.getUsername();
		// System.out.println(" user name from customerAccountSummary :::: "+userName);
		CustomerAccountInfoVO customerAccountInfo = customerService.findCustomerAccountInfo(userName);
		model.addAttribute("applicants", customerAccountInfo);

		return "/customer/accountSummary";
	}

	// ------------------------------------------------------------------------------
	@GetMapping("customer/customerTransactions")
	public String customerTransactions(Model model, HttpSession session) {

		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();

		CustomerAccountInfoVO customerAccountInfo = customerService.findCustomerAccountInfo(userName);

		List<CustomerTransactionVO> customerTransactionVOList = customerTransactionsService
				.findAllTransactions(customerAccountInfo.getAccountNumber());

		model.addAttribute("customerTransactionVOList", customerTransactionVOList);

		return "/customer/customerTransactions";
	}

	// ----------------------------- Desc -------------------------------------
	@GetMapping("/customer/customerTransactionsByDotDesc")
	public String customerListDescendingByname(Model model, HttpSession session) {

		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();

		CustomerAccountInfoVO customerAccountInfo = customerService.findCustomerAccountInfo(userName);

		List<CustomerTransactionVO> customerTransactionVOList = customerTransactionsService
				.findAllTransactions(customerAccountInfo.getAccountNumber());

		List<CustomerTransactionVO> transactionList = customerTransactionVOList.stream()
				.sorted((c1, c2) -> c2.getDot().compareTo(c1.getDot())).collect(Collectors.toList());

		model.addAttribute("customerTransactionVOList", transactionList);

		return "/customer/customerTransactions";
	}

	// ---------------------------------- Asc -----------------------------------------
	@GetMapping("customer/customerTransactionsByDotAsc")
	public String customerListAscendingByDot(Model model, HttpSession session) {
		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();
		CustomerAccountInfoVO customerAccountInfo = customerService.findCustomerAccountInfo(userName);
		List<CustomerTransactionVO> customerTransactionVOList = customerTransactionsService
				.findAllTransactions(customerAccountInfo.getAccountNumber());
		List<CustomerTransactionVO> transactionList = customerTransactionVOList.stream()
				.sorted((c1, c2) -> c1.getDot().compareTo(c2.getDot())).collect(Collectors.toList());
		model.addAttribute("customerTransactionVOList", transactionList);

		return "/customer/customerTransactions";

	}

	// --------------------------------- sendTransactionEmail ---------------------------------------------
	@GetMapping("customer/sendTransactionEmail")
	public String sendCustomerTransactionsToEmail(Model model, HttpSession session) {
		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		System.out.println("loginVO:::" + loginService);
		// Write code to send email
		EmailVO mail = new EmailVO(loginService.getUsername(), "javahunk2020@gmail.com",
				"Regarding Customer " + loginService.getUsername() + "  userid and password", "",
				loginService.getUsername());
		mail.setUsername(loginService.getUsername());
		emailService.sendCustomerTransactions(mail);

		return "redirect:/customer/customerTransactions";
	}

	// ------------------------------- displayBarGraph ----------------------------------------------
	@GetMapping("customer/displayBarGraph")
	public String displayBarGraph(Model model, HttpSession session) {

		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();
		CustomerAccountInfoVO customerAccountInfo = customerService.findCustomerAccountInfo(userName);
		List<CustomerTransactionVO> customerTransactionVOList = customerTransactionsService
				.findAllTransactions(customerAccountInfo.getAccountNumber());
		Map<java.sql.Timestamp, Double> surveyMap = customerTransactionVOList.stream()
				.collect(Collectors.toMap(CustomerTransactionVO::getDot, CustomerTransactionVO::getAmount));

		System.out.println(surveyMap);
		model.addAttribute("surveyMap", surveyMap);

		return "/customer/bar-graph-customer";
	}

	// ------------------------------------------------------------------------------

	@GetMapping("/customers/myProfile")
	public String customerProfile(Model model, HttpSession session) {

		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();
		CustomerVO  customerVO=customerService.findCustomer(userName);

		 byte[] b=customerVO.getImage();
		 byte[] encodeBase64 = Base64.getEncoder().encode(b); 
		 try {
			String base64DataString = new String(encodeBase64 , "UTF-8");
			customerVO.setPhoto(base64DataString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 System.out.println("profile CustomerVO :::"+customerVO);
		model.addAttribute("customerView", customerVO);
    	return "/customer/customer-profile";
	}
	// ------------------------------------------------------------------------------
	
	@GetMapping("/customers/sendEmail") 
	public String sendEmail() {

		return "/customer/customer-send-email";
	}
	// -------------------------------- update Photo ------------------------------
	
	@GetMapping("/customers/updatephoto")
	public String updatePhoto(Model model , HttpSession session) {
		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();
		CustomerVO  customerVO=customerService.findCustomer(userName);

		 byte[] b=customerVO.getImage();
		 byte[] encodeBase64 = Base64.getEncoder().encode(b); 
		 try {
			String base64DataString = new String(encodeBase64 , "UTF-8");
			customerVO.setPhoto(base64DataString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		model.addAttribute("customerView", customerVO);
		return "/customer/update-photo";
	}
	@PostMapping("/customers/update")
	public String updateNewPhoto(@ModelAttribute CustomerVO  customerPhoto,Model model , HttpSession session) {
		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();
		CustomerVO  customerVO=customerService.findCustomer(userName);
		
		customerVO.setFile(customerPhoto.getFile());
		customerService.updatePhoto(customerVO);
;
		model.addAttribute("customerView", customerVO);
		return "redirect:/customers/updatephoto";
	}
	
	// ------------------------------------------------------------------------------
	@PostMapping("/customers/custmoerSendEmail")
	public String customerSendEmail(@RequestParam String toEmail,@RequestParam String emailBody,String subject ,Model model,HttpSession session) {

		LoginVO loginService = (LoginVO) session.getAttribute("userSessionVO");
		String userName = loginService.getUsername();		
		CustomerVO  customerVO=customerService.findCustomer(userName);
		EmailVO mail = new EmailVO(toEmail,customerVO.getEmail(),subject,emailBody, customerVO.getName());
		mail.setFrom(userName);
		emailService.customerSendEmail(mail);			
		return "redirect:/customers/sendEmail";
	}

}
