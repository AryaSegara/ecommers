package com.ecommers.ecommers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.model.Keranjang;
import com.ecommers.ecommers.model.Users;

public interface KeranjangRepository extends JpaRepository<Keranjang, Integer>{
    List<Keranjang> findByUsers(Users user);

    List<Keranjang> findByUsersId(Integer id);

    Keranjang findByUsersAndBarang(Users user , Barang barang);

}
