package com.ecommers.ecommers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.model.Customer;
import com.ecommers.ecommers.model.Users;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    List<Customer> findByBarang(Barang barang); 
    List<Customer> findByUsers(Users users); 
    List<Customer> findByJumlah(int jumlah); 
}
