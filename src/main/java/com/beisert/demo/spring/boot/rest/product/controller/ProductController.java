package com.beisert.demo.spring.boot.rest.product.controller;

import com.beisert.demo.spring.boot.rest.product.entities.Product;
import com.beisert.demo.spring.boot.rest.product.repo.ProductRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD Rest controller for {@link Product}.
 */
@Api(description = "Rest service to manage products")
@RestController
@RequestMapping("/api/products")
public class ProductController extends CrudControllerSupport<Product, Long> {

    @Autowired
    public ProductController(ProductRepository repository) {
        init(repository);
    }
}
