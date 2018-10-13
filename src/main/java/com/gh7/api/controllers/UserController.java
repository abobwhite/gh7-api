package com.gh7.api.controllers;

import com.gh7.api.models.User;
import com.gh7.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @PostMapping()
  public ResponseEntity<User> createUser(User user) {
    try {
      user = userService.createUser(user);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }
  }
}
