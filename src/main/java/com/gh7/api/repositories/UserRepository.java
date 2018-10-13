package com.gh7.api.repositories;

import com.gh7.api.models.ASSISTANCE_CAPABILITY;
import com.gh7.api.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface UserRepository extends MongoRepository<User, String> {
  List<User> findByAssistanceCapabilitiesContaining(ASSISTANCE_CAPABILITY capability);
}
