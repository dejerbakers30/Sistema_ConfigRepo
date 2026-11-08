package com.tienda.msprecios.repository;

import com.tienda.msprecios.model.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {

    Optional<Precio> findBySku(String sku);

    boolean existsBySku(String sku);

    void deleteBySku(String sku);
}

