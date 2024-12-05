package com.ecommers.ecommers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.OrderAddress;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Integer> {
    
}
