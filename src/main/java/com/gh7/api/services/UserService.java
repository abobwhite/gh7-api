package com.gh7.api.services;

import com.gh7.api.exceptions.UserNotFoundException;
import com.gh7.api.models.User;
import com.gh7.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(User user) {
    return userRepository.insert(user);
  }

  public User getUserByUsername(String username) throws UserNotFoundException {
    Optional<User> user = userRepository.findByUsername(username);
    if(!user.isPresent()) {
      throw new UserNotFoundException();
    }

    return user.get();
  }
}
