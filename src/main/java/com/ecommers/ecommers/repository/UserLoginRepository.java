package com.ecommers.ecommers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommers.ecommers.model.UserLogin;
// import com.ecommers.ecommers.model.Users;

public interface UserLoginRepository extends JpaRepository<UserLogin, Integer> {
    UserLogin findByGmail(String gmail);

}
