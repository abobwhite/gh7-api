package com.gh7.api.services;

import com.gh7.api.models.ServiceCapability;
import com.gh7.api.models.UserServiceRequest;
import com.gh7.api.repositories.ServiceCapabilityRepository;
import com.gh7.api.repositories.UserServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRequestService {

  private UserServiceRequestRepository userServiceRequestRepository;
  private ServiceCapabilityRepository serviceCapabilityRepository;

  @Autowired
  public ServiceRequestService(UserServiceRequestRepository userServiceRequestRepository,
                               ServiceCapabilityRepository serviceCapabilityRepository) {
    this.userServiceRequestRepository = userServiceRequestRepository;
    this.serviceCapabilityRepository = serviceCapabilityRepository;
  }

  public UserServiceRequest createNewUserServiceRequest(UserServiceRequest newUserServiceRequest) {
    return this.userServiceRequestRepository.insert(newUserServiceRequest);
  }

  public List<UserServiceRequest> getRequestsFromUserId(String requestingUserId) {
    return this.userServiceRequestRepository.findByRequestingUserId(requestingUserId);
  }

  public List<UserServiceRequest> getRequestsByCapabilityId(String serviceCapabilityId) {
    Optional<ServiceCapability> serviceCapability = this.serviceCapabilityRepository.findById(serviceCapabilityId);
    if (serviceCapability.isPresent()) {
      return this.userServiceRequestRepository.findByRequestedService(serviceCapability.get());
    }
    else {
      return new ArrayList<>();
    }

  }
}
