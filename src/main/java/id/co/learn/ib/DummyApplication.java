package id.co.learn.ib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-12-08
*/
@SpringBootApplication
@EnableSwagger2
@EntityScan ("id.co.learn.ib.entity")
@EnableJpaRepositories ("id.co.learn.ib.repository")
@EnableScheduling
public class DummyApplication {

	public static void main(String [] args) {
		SpringApplication.run(DummyApplication.class, args);
	}

}
