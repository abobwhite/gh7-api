package com.gh7.api.controllers;

import com.gh7.api.exceptions.UserNotFoundException;
import com.gh7.api.models.User;
import com.gh7.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") String id) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUser = authentication.getName();
    if (!id.equalsIgnoreCase(authenticatedUser)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    try {
      User user = userService.getUserById(id);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (UserNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }
  }

  @PostMapping()
  public ResponseEntity<User> createUser(@RequestBody User user) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    user.id = authentication.getName();

    try {
      user = userService.createUser(user);
      return new ResponseEntity<>(user, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }
  }
}
