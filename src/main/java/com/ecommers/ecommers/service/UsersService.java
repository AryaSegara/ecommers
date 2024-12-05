package com.ecommers.ecommers.service;

import com.ecommers.ecommers.model.Users;
import com.ecommers.ecommers.repository.UsersRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

  @Autowired
  private UsersRepository usersRepository;


  public void save(Users users) {
    usersRepository.save(users);
  }

  public List<Users> getAllUsers() {
    return usersRepository.findAll();
  }



}
