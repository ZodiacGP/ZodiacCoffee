package org.zodiac.coffee.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zodiac.coffee.model.Product;
import org.zodiac.coffee.model.ProductEvent;
import org.zodiac.coffee.repository.ProductRepository;
import org.zodiac.coffee.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductControllerTest {
	private WebTestClient webTestClient;
	private String id;

	@MockBean
	private ProductRepository productRepository;

	@Autowired
	private ProductService productService;

	@BeforeEach
	void before() {
		webTestClient = WebTestClient.bindToController(new ProductController(productService))
				.configureClient()
				.baseUrl("/products")
				.build();

		id = UUID.randomUUID().toString();
	}

	@Test
	public void testGetProducts() {
		List<Product> productList = List.of(new Product().setName("Test product").setPrice(3.0));
		when(productRepository.findAll()).thenReturn(Flux.fromIterable(productList));

		webTestClient.get()
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Product.class)
				.isEqualTo(productList);
	}

	@Test
	public void testGetProduct_NotFound() {
		when(productRepository.findById(anyString())).thenReturn(Mono.empty());

		webTestClient.get()
				.uri("/wrongid")
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testSaveProduct() {
		Product productToSave = new Product().setName("Test product").setPrice(3.0);
		when(productRepository.save(productToSave)).thenReturn(Mono.just(productToSave.setId(id)));

		webTestClient.post()
				.body(Mono.just(productToSave), Product.class)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Product.class)
				.isEqualTo(productToSave.setId(id));
	}

	@Test
	public void testUpdateProduct() {
		Product existingProduct = new Product().setName("Test product").setPrice(3.0);
		when(productRepository.findById(anyString())).thenReturn(Mono.just(existingProduct));
		Product updatedProduct = existingProduct.setName("New test product");
		when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

		webTestClient.put()
				.uri("/{id}", id)
				.body(Mono.just(updatedProduct), Product.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Product.class)
				.isEqualTo(updatedProduct);
	}

	@Test
	public void testUpdateProduct_NotFound() {
		Product existingProduct = new Product().setName("Test product").setPrice(3.0);
		when(productRepository.findById(anyString())).thenReturn(Mono.empty());
		Product updatedProduct = existingProduct.setName("New test product");

		webTestClient.put()
				.uri("wrongid")
				.body(Mono.just(updatedProduct), Product.class)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testProductEvents() {
		ProductEvent expectedEvent = new ProductEvent().setId(0L).setType("Product event");

		FluxExchangeResult<ProductEvent> result = webTestClient.get()
				.uri("/events")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductEvent.class);

		StepVerifier.create(result.getResponseBody())
				.expectNext(expectedEvent)
				.expectNextCount(1)
				.consumeNextWith(event -> assertEquals(2L, event.getId()))
				.thenCancel()
				.verify();
	}

	@Test
	public void testDeleteProduct() {
		Product existingProduct = new Product().setName("Test product").setPrice(3.0);
		when(productRepository.findById(anyString())).thenReturn(Mono.just(existingProduct));
		when(productRepository.delete(existingProduct)).thenReturn(Mono.empty());

		webTestClient.delete()
				.uri("/{id}", id)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Void.class);

		verify(productRepository, times(1)).delete(existingProduct);
	}

	@Test
	public void testDeleteProduct_NotFound() {
		when(productRepository.findById(anyString())).thenReturn(Mono.empty());

		webTestClient.delete()
				.uri("/wrongid")
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testDeleteAllProducts() {
		webTestClient.delete()
				.exchange()
				.expectStatus().isOk()
				.expectBody(Void.class);

		verify(productRepository, times(1)).deleteAll();
	}
}