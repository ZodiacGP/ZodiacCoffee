package org.zodiac.coffee.service;

import org.springframework.http.ResponseEntity;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.model.ProductEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WebClientService {
	Mono<ResponseEntity<Product>> postNewProduct(Product product);

	Flux<Product> getAllProducts();

	Mono<Product> updateProduct(Product product);

	Mono<Void> deleteProduct(String id);

	Flux<ProductEvent> getAllEvents();
}
