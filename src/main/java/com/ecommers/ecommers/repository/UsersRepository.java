package com.ecommers.ecommers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByName(String name);
    Users findByGmail(String gmail);

    List<Users>findGmailByGmail(String gmail);
}
