package com.gh7.api.services;

import com.gh7.api.exceptions.UserNotFoundException;
import com.gh7.api.models.ASSISTANCE_CAPABILITY;
import com.gh7.api.models.User;
import com.gh7.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

  public User getUserById(String id) throws UserNotFoundException {
    Optional<User> user = userRepository.findById(id);
    if (!user.isPresent()) {
      throw new UserNotFoundException();
    }

    return user.get();
  }

  public User updateUser(User user) {
    return this.userRepository.save(user);
  }

  public List<User> findOnCallUsersWithCapability(ASSISTANCE_CAPABILITY capability) {
    return userRepository.findByAssistanceCapabilitiesContainingAndOnCall(capability, true);
  }
}
