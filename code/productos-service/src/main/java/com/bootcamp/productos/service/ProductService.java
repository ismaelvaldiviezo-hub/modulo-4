package com.bootcamp.productos.service;

import com.bootcamp.productos.client.catalog.model.CatalogResponse;
import com.bootcamp.productos.client.discount.model.DiscountResponse;
import com.bootcamp.productos.model.ProductDetailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final com.bootcamp.productos.client.catalog.api.DefaultApi catalogApiClient;
    private final com.bootcamp.productos.client.discount.api.DefaultApi discountApiClient;

    public ProductService(
            com.bootcamp.productos.client.catalog.api.DefaultApi catalogApiClient,
            com.bootcamp.productos.client.discount.api.DefaultApi discountApiClient) {
        this.catalogApiClient = catalogApiClient;
        this.discountApiClient = discountApiClient;
    }

    public ProductDetailResponse getProductDetails(Long id) {
        // 1. Llamar al microservicio de catálogo
        log.debug("Consultando microservicio de catalogo para ID: {}", id);
        CatalogResponse catalog = catalogApiClient.getCatalogByProductId(id);
        
        // 2. Llamar al microservicio de descuento
        Double discountPercentage = 0.0;
        try {
            log.debug("Consultando microservicio de descuento para ID: {}", id);
            DiscountResponse discount = discountApiClient.getDiscountByProductId(id);
            if (discount != null && discount.getPercentage() != null) {
                discountPercentage = discount.getPercentage();
                log.info("Descuento aplicado del {}% para producto ID: {}", discountPercentage, id);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.info("El producto ID {} no tiene descuento activo (404 en descuento-service)", id);
            discountPercentage = 0.0;
        } catch (Exception e) {
            log.warn("Falla al consultar descuento-service para el producto ID {}: {}. Se asume 0% de descuento.", id, e.getMessage());
            discountPercentage = 0.0;
        }

        // 3. Consolidar los resultados
        double basePrice = catalog.getBasePrice();
        double finalPrice = basePrice - (basePrice * (discountPercentage / 100.0));

        ProductDetailResponse product = new ProductDetailResponse();
        product.setId(catalog.getProductId());
        product.setName(catalog.getName());
        product.setBasePrice(basePrice);
        product.setDiscountPercentage(discountPercentage);
        product.setFinalPrice(finalPrice);
        product.setStock(catalog.getStock());

        return product;
    }
}