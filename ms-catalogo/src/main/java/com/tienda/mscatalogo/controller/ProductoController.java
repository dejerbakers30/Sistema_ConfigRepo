package com.tienda.mscatalogo.controller;

import com.tienda.mscatalogo.model.Producto;
import com.tienda.mscatalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/catalogo")
public class ProductoController {

    private final ProductoRepository productoRepository;

    @Value("${mensaje.global:Mensaje global por defecto}")
    private String mensajeGlobal;

    @Value("${catalogo.items-por-pagina:10}")
    private Integer itemsPorPagina;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // 1. GET lista de productos + metadatos inyectados desde Spring Cloud Config
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarProductos() {
        List<Producto> productos = productoRepository.findAll();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensajeGlobal", mensajeGlobal);
        respuesta.put("itemsPorPaginaConfigurados", itemsPorPagina);
        respuesta.put("totalProductos", productos.size());
        respuesta.put("productos", productos);

        return ResponseEntity.ok(respuesta);
    }

    // 2. GET por SKU
    @GetMapping("/{sku}")
    public ResponseEntity<?> obtenerPorSku(@PathVariable String sku) {
        Optional<Producto> productoOpt = productoRepository.findBySku(sku);
        if (productoOpt.isPresent()) {
            return ResponseEntity.ok(productoOpt.get());
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Producto con SKU '" + sku + "' no encontrado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 3. POST crear producto
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        if (producto.getSku() != null && productoRepository.existsBySku(producto.getSku())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ya existe un producto con el SKU: " + producto.getSku());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // 4. PUT actualizar producto por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto datosActualizados) {
        return productoRepository.findById(id).map(productoExistente -> {
            productoExistente.setSku(datosActualizados.getSku());
            productoExistente.setNombre(datosActualizados.getNombre());
            productoExistente.setCategoria(datosActualizados.getCategoria());
            Producto actualizado = productoRepository.save(productoExistente);
            return ResponseEntity.ok((Object) actualizado);
        }).orElseGet(() -> {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Producto con ID " + id + " no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        });
    }

    // 5. DELETE eliminar producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Producto con ID " + id + " no existe.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        productoRepository.deleteById(id);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Producto con ID " + id + " eliminado con éxito.");
        return ResponseEntity.ok(respuesta);
    }
}

