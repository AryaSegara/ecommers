package com.ecommers.ecommers.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data  // Anotasi dari Lombok yang secara otomatis menghasilkan getter, setter, toString, equals, dan hashCode
@Entity //  Menandakan bahwa kelas ini adalah entitas JPA yang akan direpresentasikan sebagai tabel di database
public class ProductOrder {

    @Id // Menandakan bahwa field ini adalah primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key akan dibuat secara otomatis oleh database dengan strategi IDENTITY.
    private Integer id;

    private String orderId;

    private LocalDate orderDate;

    @ManyToOne // Menunjukkan relasi many-to-one (banyak pesanan bisa memiliki produk yang sama).
    @JoinColumn(name = "product_id", referencedColumnName = "id") //Menentukan nama kolom foreign key (product_id) yang mengacu ke kolom id di entitas Barang.
    private Barang barang;

    private Integer harga;

    private Integer jumlah;

    @ManyToOne //Menunjukkan relasi many-to-one (banyak pesanan bisa memiliki produk yang sama).
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users users;

    private String status;

    private String paymentType;


    // cascade = cascadeType.ALL =
    //  Semua operasi seperti persist, merge, dan remove pada entitas ProductOrder akan memengaruhi entitas OrderAddress.
    @OneToOne(cascade = CascadeType.ALL) //Menunjukkan relasi one-to-one (satu pesanan memiliki satu alamat pengiriman).
    private OrderAddress orderAddress;
    
}

