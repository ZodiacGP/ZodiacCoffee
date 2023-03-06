package org.zodiac.coffee.service;

import org.springframework.http.ResponseEntity;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.model.ProductEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
	Flux<Product> getProducts();

	Mono<Product> getProduct(String id);

	Mono<Product> saveProduct(Product product);

	Mono<Product> updateProduct(String id, Product product);

	Mono<ResponseEntity<Void>> deleteProduct(String id);

	Mono<Void> deleteAllProducts();

	Flux<ProductEvent> getProductEvents();
}
