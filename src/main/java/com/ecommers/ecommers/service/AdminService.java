package com.ecommers.ecommers.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommers.ecommers.model.Admin;
import com.ecommers.ecommers.model.Barang;
import com.ecommers.ecommers.repository.AdminRepository;
import com.ecommers.ecommers.repository.BarangRepository;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BarangRepository barangRepository;


    public Admin getAdminByPin(String pin){
        return adminRepository.findAdminByPin(pin);
    }

    public boolean adminIsEmpty(Integer id){
        return adminRepository.findById(id).isEmpty();
    }

    public Barang findById(Integer id){
        return barangRepository.findById(id).orElse(null);
    }

    public List<Admin> getAllAdmin(){
        return adminRepository.findAll();
    }

    public void save(Admin admin){
        adminRepository.save(admin);
    }

}
