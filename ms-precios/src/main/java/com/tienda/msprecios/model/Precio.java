package com.tienda.msprecios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "precio")
public class Precio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "precio_base", nullable = false)
    private Double precioBase;

    public Precio() {
    }

    public Precio(Long id, String sku, Double precioBase) {
        this.id = id;
        this.sku = sku;
        this.precioBase = precioBase;
    }

    public Precio(String sku, Double precioBase) {
        this.sku = sku;
        this.precioBase = precioBase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    @Override
    public String toString() {
        return "Precio{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", precioBase=" + precioBase +
                '}';
    }
}

