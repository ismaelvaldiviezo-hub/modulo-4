package com.bootcamp.descuento.controller;

import com.bootcamp.descuento.api.ApiApi;
import com.bootcamp.descuento.model.DiscountResponse;
import com.bootcamp.descuento.repository.DiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscountController implements ApiApi {

    private static final Logger log = LoggerFactory.getLogger(DiscountController.class);

    private final DiscountRepository repository;

    public DiscountController(DiscountRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<DiscountResponse> getDiscountByProductId(Long productId) {
        log.info("Buscando descuento para el producto ID: {}", productId);

        return repository.findById(productId)
                .map(entity -> {
                    log.info("Descuento encontrado para producto ID {}: {}% - {}", 
                            productId, entity.getPercentage(), entity.getDescription());

                    DiscountResponse response = new DiscountResponse();
                    response.setProductId(entity.getProductId());
                    response.setPercentage(entity.getPercentage());
                    response.setDescription(entity.getDescription());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("No se encontro descuento aplicable para el producto ID: {}", productId);
                    return ResponseEntity.notFound().build();
                });
    }
}