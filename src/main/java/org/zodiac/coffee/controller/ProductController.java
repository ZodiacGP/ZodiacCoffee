package org.zodiac.coffee.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.model.ProductEvent;
import org.zodiac.coffee.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;

	@GetMapping
	public Flux<Product> getProducts() {
		return productService.getProducts();
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
		return productService.getProduct(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Product> saveProduct(@RequestBody Product product) {
		return productService.saveProduct(product);
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String id, @RequestBody Product product) {
		return productService.updateProduct(id, product)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id) {
		return productService.deleteProduct(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping
	public Mono<Void> deleteAllProducts() {
		return productService.deleteAllProducts();
	}

	@GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ProductEvent> getProductEvents() {
		return productService.getProductEvents();
	}
}
