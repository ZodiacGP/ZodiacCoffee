package org.zodiac.coffee.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductRepository productRepository;

	@GetMapping
	public Flux<Product> getProducts() {
		return productRepository.findAll();
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
		return productRepository.findById(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Product> saveProduct(@RequestBody Product product) {
		return productRepository.save(product);
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String id, @RequestBody Product product) {
		return productRepository.findById(id)
				.flatMap(foundedProduct -> {
					foundedProduct.setName(product.getName());
					foundedProduct.setPrice(product.getPrice());
					return productRepository.save(foundedProduct);
				})
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id) {
		return productRepository.deleteById(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping
	public Mono<Void> deleteAllProducts() {
		return productRepository.deleteAll();
	}
}
