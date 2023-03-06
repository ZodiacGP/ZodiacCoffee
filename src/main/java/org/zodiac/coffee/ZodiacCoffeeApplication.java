package org.zodiac.coffee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.service.WebClientService;

@SpringBootApplication
public class ZodiacCoffeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZodiacCoffeeApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(WebClientService webClientService) {
		return args -> webClientService.postNewProduct(new Product().setName("Black tea").setPrice(1.99))
				.thenMany(webClientService.getAllProducts())
				.take(1)
				.flatMap(p -> webClientService.updateProduct(new Product().setId(p.getId()).setName("White tea").setPrice(0.99)))
				.thenMany(webClientService.getAllProducts())
				.flatMap(p -> webClientService.deleteProduct(p.getId()))
				.thenMany(webClientService.getAllProducts())
				.thenMany(webClientService.getAllEvents())
				.subscribe(System.out::println);
	}
}
