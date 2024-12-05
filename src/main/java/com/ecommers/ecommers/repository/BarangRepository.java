package com.ecommers.ecommers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.model.Category;

public interface BarangRepository extends JpaRepository<Barang, Integer> {
    List<Barang> findByCategory(Category category);
    List<Barang> findAllByName(String name);
    List<Barang> findByNameContainingIgnoreCase(String name);

    // List<Barang> findByName(String name);
     Barang findByName(String name);
    List<Barang> findByStok(int stok);
    List<Barang> findByHarga(int harga);
    List<Barang> findByDescription(String description);

    List<Barang> findAllByOrderByNameAsc();
    List<Barang> findAllByOrderByNameDesc();

    List<Barang> findAllByOrderByHargaAsc();

    List<Barang> findAllByOrderByHargaDesc();
    
}
