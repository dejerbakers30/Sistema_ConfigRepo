package com.tienda.msprecios.controller;

import com.tienda.msprecios.model.Precio;
import com.tienda.msprecios.repository.PrecioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/precios")
public class PrecioController {

    private final PrecioRepository precioRepository;

    @Value("${precios.impuesto:0.18}")
    private Double impuesto;

    @Value("${mensaje.global:Mensaje global por defecto}")
    private String mensajeGlobal;

    public PrecioController(PrecioRepository precioRepository) {
        this.precioRepository = precioRepository;
    }

    // 1. GET lista de todos los precios con cálculo de impuesto y precio final
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos() {
        List<Precio> precios = precioRepository.findAll();

        List<Map<String, Object>> items = precios.stream()
                .map(this::construirDetallePrecio)
                .collect(Collectors.toList());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensajeGlobal", mensajeGlobal);
        respuesta.put("tasaImpuestoConfigurada", impuesto);
        respuesta.put("totalItems", items.size());
        respuesta.put("precios", items);

        return ResponseEntity.ok(respuesta);
    }

    // 2. GET por SKU inyectando el impuesto de Git y calculando el precio final
    @GetMapping("/{sku}")
    public ResponseEntity<?> obtenerPorSku(@PathVariable String sku) {
        Optional<Precio> precioOpt = precioRepository.findBySku(sku);
        if (precioOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Precio para el SKU '" + sku + "' no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Map<String, Object> detalle = construirDetallePrecio(precioOpt.get());
        detalle.put("mensajeGlobal", mensajeGlobal);
        return ResponseEntity.ok(detalle);
    }

    // 3. POST crear nuevo registro de precio
    @PostMapping
    public ResponseEntity<?> crearPrecio(@RequestBody Precio precio) {
        if (precio.getSku() != null && precioRepository.existsBySku(precio.getSku())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Ya existe un registro de precio para el SKU: " + precio.getSku());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        Precio guardado = precioRepository.save(precio);
        return ResponseEntity.status(HttpStatus.CREATED).body(construirDetallePrecio(guardado));
    }

    // 4. PUT actualizar precio por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPrecio(@PathVariable Long id, @RequestBody Precio datosActualizados) {
        return precioRepository.findById(id).map(precioExistente -> {
            precioExistente.setSku(datosActualizados.getSku());
            precioExistente.setPrecioBase(datosActualizados.getPrecioBase());
            Precio actualizado = precioRepository.save(precioExistente);
            return ResponseEntity.ok((Object) construirDetallePrecio(actualizado));
        }).orElseGet(() -> {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Precio con ID " + id + " no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        });
    }

    // 5. DELETE eliminar precio por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPrecio(@PathVariable Long id) {
        if (!precioRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Precio con ID " + id + " no existe.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        precioRepository.deleteById(id);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Precio con ID " + id + " eliminado con éxito.");
        return ResponseEntity.ok(respuesta);
    }

    // Helper para formatear y calcular precios con 2 decimales
    private Map<String, Object> construirDetallePrecio(Precio precio) {
        BigDecimal base = BigDecimal.valueOf(precio.getPrecioBase()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tasa = BigDecimal.valueOf(impuesto).setScale(4, RoundingMode.HALF_UP);
        BigDecimal montoImpuesto = base.multiply(BigDecimal.valueOf(impuesto)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal precioFinal = base.add(montoImpuesto).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> mapa = new HashMap<>();
        mapa.put("id", precio.getId());
        mapa.put("sku", precio.getSku());
        mapa.put("precioBase", base);
        mapa.put("tasaImpuesto", tasa);
        mapa.put("montoImpuesto", montoImpuesto);
        mapa.put("precioFinal", precioFinal);
        return mapa;
    }
}

