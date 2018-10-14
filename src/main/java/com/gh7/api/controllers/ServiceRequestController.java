package com.gh7.api.controllers;

import com.gh7.api.models.UserServiceRequest;
import com.gh7.api.services.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/service-requests")
public class ServiceRequestController {

  private ServiceRequestService serviceRequestService;

  @Autowired
  public ServiceRequestController(ServiceRequestService serviceRequestService) {
    this.serviceRequestService = serviceRequestService;
  }

  @GetMapping("")
  public ResponseEntity<List<UserServiceRequest>> findUserSearchRequests(@RequestParam(value = "requestingUserId", required = false) String requestingUserId,
                                                         @RequestParam(value = "serviceCapabilityId", required = false) String serviceCapabilityId) {
    if (requestingUserId != null) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!requestingUserId.equalsIgnoreCase(authentication.getName())) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      List<UserServiceRequest> requests = this.serviceRequestService.getRequestsFromUserId(requestingUserId);
      return new ResponseEntity<>(requests, HttpStatus.OK);
    }
    else if (serviceCapabilityId != null) {
      List<UserServiceRequest> requests = this.serviceRequestService.getRequestsByCapabilityId(serviceCapabilityId);
      return new ResponseEntity<>(requests, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @PostMapping("")
  public UserServiceRequest createNewServiceRequest(@RequestBody UserServiceRequest newUserServiceRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    newUserServiceRequest.requestingUserId = authentication.getName();
    newUserServiceRequest.createdAt = Instant.now();

    return this.serviceRequestService.createNewUserServiceRequest(newUserServiceRequest);
  }
}
