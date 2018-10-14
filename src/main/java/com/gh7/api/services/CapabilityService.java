package com.gh7.api.services;

import com.gh7.api.models.ServiceCapability;
import com.gh7.api.repositories.ServiceCapabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CapabilityService {

  private ServiceCapabilityRepository serviceCapabilityRepository;

  @Autowired
  public CapabilityService(ServiceCapabilityRepository serviceCapabilityRepository) {
    this.serviceCapabilityRepository = serviceCapabilityRepository;
  }

  public List<ServiceCapability> getAllServiceCapabilities() {
    return this.serviceCapabilityRepository.findAll();
  }

  public ServiceCapability addServiceCapability(ServiceCapability newServiceCapability) {
    return this.serviceCapabilityRepository.insert(newServiceCapability);
  }
}
