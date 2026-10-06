package com.bootcamp.catalogo.controller;

import com.bootcamp.catalogo.api.ApiApi;
import com.bootcamp.catalogo.model.CatalogResponse;
import com.bootcamp.catalogo.repository.CatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController implements ApiApi {

    // Instancia del logger para esta clase
    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogRepository repository;

    public CatalogController(CatalogRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<CatalogResponse> getCatalogByProductId(Long productId) {
        // 1. Log de entrada (INFO): Permite rastrear qué petición llegó y con qué parámetros
        log.info("Consultando catálogo para el producto ID: {}", productId);

        return repository.findById(productId)
                .map(entity -> {
                    // 2. Log de éxito (DEBUG / INFO): Confirmación del hallazgo
                    log.info("Producto encontrado en catálogo: {} (Stock: {})", entity.getName(), entity.getStock());

                    CatalogResponse response = new CatalogResponse();
                    response.setProductId(entity.getProductId());
                    response.setName(entity.getName());
                    response.setBasePrice(entity.getBasePrice());
                    response.setStock(entity.getStock());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    // 3. Log de advertencia (WARN): Evento de negocio donde no existe el recurso
                    log.warn("Producto no encontrado en catálogo para el ID: {}", productId);
                    return ResponseEntity.notFound().build();
                });
    }
}