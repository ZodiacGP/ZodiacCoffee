package org.zodiac.coffee.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.model.ProductEvent;
import org.zodiac.coffee.service.WebClientService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {
	private final WebClient webClient;

	@Override
	public Mono<ResponseEntity<Product>> postNewProduct(Product product) {
		return webClient.post()
				.body(Mono.just(product), Product.class)
				.exchangeToMono(response -> response.toEntity(Product.class))
				.doOnSuccess(o -> System.out.println("POST " + o));
	}

	@Override
	public Flux<Product> getAllProducts() {
		return webClient.get()
				.retrieve()
				.bodyToFlux(Product.class)
				.doOnNext(o -> System.out.println("GET " + o));
	}

	@Override
	public Mono<Product> updateProduct(Product product) {
		String id = product.getId();
		return webClient.put()
				.uri("/{id}", id)
				.body(Mono.just(new Product().setName(product.getName()).setPrice(product.getPrice())), Product.class)
				.retrieve()
				.bodyToMono(Product.class)
				.doOnSuccess(o -> System.out.println("PUT " + o));
	}

	@Override
	public Mono<Void> deleteProduct(String id) {
		return webClient.delete()
				.uri("/{id}", id)
				.retrieve()
				.bodyToMono(Void.class)
				.doOnSuccess(o -> System.out.println("DELETE " + o));
	}

	@Override
	public Flux<ProductEvent> getAllEvents() {
		return webClient.get()
				.uri("/events")
				.retrieve()
				.bodyToFlux(ProductEvent.class);
	}
}
