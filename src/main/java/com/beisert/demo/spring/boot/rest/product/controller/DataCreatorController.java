package com.beisert.demo.spring.boot.rest.product.controller;

import com.beisert.demo.spring.boot.rest.product.entities.Order;
import com.beisert.demo.spring.boot.rest.product.entities.Product;
import com.beisert.demo.spring.boot.rest.product.repo.OrderRepository;
import com.beisert.demo.spring.boot.rest.product.repo.ProductRepository;
import io.swagger.annotations.Api;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple restcontroller that creates some test data.
 */
@Api(description = "Rest service to create some test data")
@RestController
@RequestMapping("/dataCreator")
public class DataCreatorController {

    private ProductRepository productRepository;

    private OrderRepository orderRepository;

    @Autowired
    public DataCreatorController(
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/init/products/{count}")
    public DataCreationResponse initData(@PathVariable("count") int count) {
        DataCreationResponse response = new DataCreationResponse();

        for (int i = 0; i < count; i++) {
            Product product = Product.builder().name("David" + i).price(new BigDecimal("10"))
                    .build();
            response.products.add(this.productRepository.save(product));
            Order order = Order.builder().buyersEmail("beisdog@web.de").build();
            order.setProducts(response.products);
            response.orders.add(this.orderRepository.save(order));
        }

        return response;
    }

    @Data
    public class DataCreationResponse {
        private List<Product> products = new ArrayList<>();
        private List<Order> orders = new ArrayList<>();
    }
}
