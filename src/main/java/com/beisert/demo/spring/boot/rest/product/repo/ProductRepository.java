package com.beisert.demo.spring.boot.rest.product.repo;

import com.beisert.demo.spring.boot.rest.product.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
