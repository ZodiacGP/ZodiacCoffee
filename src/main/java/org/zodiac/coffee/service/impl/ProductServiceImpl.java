package org.zodiac.coffee.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.model.ProductEvent;
import org.zodiac.coffee.repository.ProductRepository;
import org.zodiac.coffee.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;

	@Override
	public Flux<Product> getProducts() {
		return productRepository.findAll();
	}

	@Override
	public Mono<Product> getProduct(String id) {
		return productRepository.findById(id);
	}

	@Override
	public Mono<Product> saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public Mono<Product> updateProduct(String id, Product product) {
		return productRepository.findById(id)
				.flatMap(foundedProduct -> {
					foundedProduct.setName(product.getName());
					foundedProduct.setPrice(product.getPrice());
					return productRepository.save(foundedProduct);
				});
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteProduct(String id) {
		return productRepository.findById(id)
				.flatMap(entity -> productRepository.delete(entity)
						.then(Mono.just(ResponseEntity.ok().build()))
				);
	}

	@Override
	public Mono<Void> deleteAllProducts() {
		return productRepository.deleteAll();
	}

	@Override
	public Flux<ProductEvent> getProductEvents() {
		return Flux.interval(Duration.ofSeconds(1))
				.map(val -> new ProductEvent().setId(val).setType("Product event"));
	}
}
