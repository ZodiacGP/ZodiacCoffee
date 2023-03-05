package org.zodiac.coffee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.repository.ProductRepository;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ZodiacCoffeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZodiacCoffeeApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository repository) {
		return args -> {
			repository.deleteAll().subscribe();
			Flux<Product> productFlux = Flux.just(
							new Product().setName("Big Latte").setPrice(2.99),
							new Product().setName("Big Decaf").setPrice(2.49),
							new Product().setName("Green Tea").setPrice(1.99))
					.flatMap(repository::save);

			productFlux.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}

}
