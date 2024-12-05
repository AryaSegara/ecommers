package com.ecommers.ecommers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.Admin;

public interface AdminRepository extends JpaRepository <Admin,Integer> {
    
    Admin findAdminByPin(String pin);
}
