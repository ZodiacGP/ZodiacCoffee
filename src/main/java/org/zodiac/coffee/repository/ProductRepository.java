package org.zodiac.coffee.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.zodiac.coffee.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
