package com.ecommers.ecommers.service;

import com.ecommers.ecommers.model.UserLogin;
// import com.ecommers.ecommers.model.Users;
import com.ecommers.ecommers.repository.UserLoginRepository;
// import com.ecommers.ecommers.repository.UsersRepository;
// import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.ui.Model;

@Service
public class UserLoginService {

  @Autowired
  private UserLoginRepository userLoginRepository;

  public boolean userLoginIsEmpty() {
    return userLoginRepository.findAll().isEmpty();
  }

  public void save(UserLogin userLogin) {
    userLoginRepository.save(userLogin);
  }

  public void delete() {
    userLoginRepository.deleteAll();
  }
}
