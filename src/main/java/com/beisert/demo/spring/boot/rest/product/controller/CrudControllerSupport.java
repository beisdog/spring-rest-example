package com.beisert.demo.spring.boot.rest.product.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Map;

/**
 * Base class for rest controller that supports CRUD methods using a {@link JpaRepository}.
 *
 * @param <T>  the entity
 * @param <ID> the id type
 */
public class CrudControllerSupport<T, ID extends Serializable> {

    private JpaRepository<T, ID> repository;

    public CrudControllerSupport init(JpaRepository<T, ID> repository) {
        this.repository = repository;
        return this;
    }

    @GetMapping("/")
    public Iterable<T> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> findOne(@PathVariable("id") ID id) {
        T entityInDb = repository.findOne(id);
        if (entityInDb == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entityInDb, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<T> save(@RequestBody final T p) {
        T saved = repository.save(p);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Just update some fields
     *
     * @param id
     * @param updatedFields
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<T> update(
            @PathVariable("id") final ID id,
            @RequestBody final Map<String, Object> updatedFields
    ) {
        return replace(id, updatedFields);
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> replace(
            @PathVariable("id") final ID id,
            @RequestBody final Map<String, Object> product
    ) {

        final T entityInDb = repository.findOne(id);
        if (entityInDb == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        applyMapToEntity(product, entityInDb);

        final T result = repository.save(entityInDb);
        return new ResponseEntity<T>(result, HttpStatus.OK);
    }

    /**
     * Can be overriden to apply a the updated fields to the entity.
     * The current implementation only works for a flat entity without relations.
     *
     * @param map
     * @param entityInDb
     */
    protected void applyMapToEntity(@RequestBody Map<String, Object> map, T entityInDb) {
        BeanWrapper wrapper = new BeanWrapperImpl(entityInDb);
        wrapper.setPropertyValues(map);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<T> delete(@PathVariable("id") final ID id) {
        final T entityInDb = repository.findOne(id);
        if (entityInDb == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        repository.delete(id);
        return new ResponseEntity<T>(entityInDb, HttpStatus.OK);
    }

    public JpaRepository<T, ID> getRepository() {
        return repository;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorMessage {
        private String message;
    }
}
