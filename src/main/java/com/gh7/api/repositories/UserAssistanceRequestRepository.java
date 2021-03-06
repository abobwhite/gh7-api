package com.gh7.api.repositories;

import com.gh7.api.models.UserAssistanceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface UserAssistanceRequestRepository extends MongoRepository<UserAssistanceRequest, String> {
}
