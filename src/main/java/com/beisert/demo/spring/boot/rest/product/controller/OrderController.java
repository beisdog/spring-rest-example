package com.beisert.demo.spring.boot.rest.product.controller;

import com.beisert.demo.spring.boot.rest.product.entities.Order;
import com.beisert.demo.spring.boot.rest.product.entities.Product;
import com.beisert.demo.spring.boot.rest.product.repo.OrderRepository;
import com.beisert.demo.spring.boot.rest.product.repo.ProductRepository;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.BiFunction;

/**
 * CRUD Rest Controller for {@link Order}.
 */
@Api(description = "Rest service to manage orders")
@RestController
@RequestMapping("/api/orders")
public class OrderController extends CrudControllerSupport<Order, Long> {

    private ProductRepository productRepository;

    @Autowired
    public OrderController(OrderRepository repository, ProductRepository productRepository) {
        init(repository);
        this.productRepository = productRepository;
    }

    @PostMapping("/{id}/products/")
    public ResponseEntity<ModifyProductsResponse> addProducts(
            @PathVariable("id") Long id,
            @RequestBody List<Long> productIds
    ) {
        return modifyProductList(
                id,
                productIds,
                (order, product) -> order.getProducts().add(product)
        );
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<ModifyProductsResponse> deleteProducts(
            @PathVariable("id") Long id,
            @PathVariable("productId") Long productId
    ) {
        return modifyProductList(
                id,
                Arrays.asList(productId),
                (order, product) -> order.getProducts().remove(product)
        );
    }

    private ResponseEntity<ModifyProductsResponse> modifyProductList(
            Long orderId,
            List<Long> productIds,
            BiFunction<Order, Product, ?> function
    ) {
        Order order = getRepository().findOne(orderId);
        ModifyProductsResponse response = ModifyProductsResponse.builder().order(order)
                .productIds(productIds).build();

        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (Long productId : productIds) {
            Product product = productRepository.findOne(productId);
            if (product == null) {
                response.message = new ErrorMessage("Product with Id=" + productId + " not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            if (order.getProducts() == null) {
                order.setProducts(new ArrayList<>());
            }
            function.apply(order, product);
        }

        response.order = getRepository().save(order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override protected void applyMapToEntity(
            Map<String, Object> map, Order entityInDb
    ) {
        Map<String, Object> cloned = new LinkedHashMap<>(map);
        // ignore the products map
        Object products = cloned.remove("products");
        super.applyMapToEntity(cloned, entityInDb);
    }

    @GetMapping("/fromTo")
    public Iterable<Order> findFromTo(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            Pageable pageable
    ) {
        return ((OrderRepository) getRepository()).fromTo(from, to, pageable);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ModifyProductsResponse {
        private Order order;
        private List<Long> productIds;
        private ErrorMessage message;
    }
}
