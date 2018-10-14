package com.gh7.api.repositories;

import com.gh7.api.models.ServiceCapability;
import com.gh7.api.models.UserServiceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(exported = false)
public interface UserServiceRequestRepository extends MongoRepository<UserServiceRequest, String> {
  List<UserServiceRequest> findByRequestingUserId(String requestingUserId);
  List<UserServiceRequest> findByRequestedService(ServiceCapability serviceCapability);
}
