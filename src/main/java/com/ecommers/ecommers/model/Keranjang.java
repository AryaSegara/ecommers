package com.ecommers.ecommers.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data

public class Keranjang{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Barang barang;

    private Integer jumlah;

    private Integer totalHarga;

    public Integer getTotalHarga(){
        return barang.getHarga() * jumlah;
    }
 
    // @Transient
    // private Double totalOrderPrice;
    

}