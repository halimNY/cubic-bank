package com.rab3tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*@SpringBootApplication
public class SpringBootRunner extends SpringBootServletInitializer {
	
	

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRunner.class, args);
		
	}
	
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	        return builder.sources(SpringBootRunner.class);
	    }

}
*/

@SpringBootApplication
//@EnableDiscoveryClient
public class SpringBootRunner  {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootRunner.class, args);
	}
	
}
