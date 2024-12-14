package org.studyeasy.SpringRestDemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.studyeasy.SpringRestDemo.Entities.Account;
import org.studyeasy.SpringRestDemo.Sevice.AccountService;
import org.studyeasy.SpringRestDemo.Util.Constants.Authority;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"org.studyeasy.SpringRestDemo.Repositories"})
@SecurityScheme(name = "studyeasy-demo-api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class SpringRestDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringRestDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AccountService accountService){
		return (args) -> {
			Account user = new Account();
			user.setEmail("admin@admin.com");
			user.setPassword("pass987");
			user.setAuthorities(Authority.ADMIN.toString());

			accountService.saveUser(user);

			Account user2 = new Account();
			user2.setEmail("alinusya@user.com");
			user2.setPassword("pass987");
			user2.setAuthorities(Authority.ADMIN.toString() + " " + Authority.NYASHNIY_ADMINCHICK.toString());

			accountService.saveUser(user2);

			Account user3 = new Account();
			user3.setEmail("andr@user.com");
			user3.setPassword("pass987");
			user3.setAuthorities(Authority.USER.toString());

			accountService.saveUser(user3);
		};
	}
}
