package com.bootcamp.productos.controller;

import com.bootcamp.productos.api.ApiApi;
import com.bootcamp.productos.model.ProductDetailResponse;
import com.bootcamp.productos.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class ProductController implements ApiApi {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<ProductDetailResponse> getProductById(Long id) {
        log.info("Recibida peticion de detalle para el producto ID: {}", id);

        try {
            ProductDetailResponse response = productService.getProductDetails(id);
            log.info("Detalle consolidado exitosamente para el producto ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("No se pudo consolidar el producto ID {}: No existe en el catalogo", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error inesperado al procesar la peticion del producto ID: {}", id, e);
            throw e;
        }
    }
}