package com.tienda.mscatalogo.repository;

import com.tienda.mscatalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findBySku(String sku);

    boolean existsBySku(String sku);

    void deleteBySku(String sku);
}

