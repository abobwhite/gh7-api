package com.gh7.api.controllers;

import com.gh7.api.models.ServiceCapability;
import com.gh7.api.services.CapabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/capabilities")
public class CapabilityController {

  private CapabilityService capabilityService;

  @Autowired
  public CapabilityController(CapabilityService capabilityService) {
    this.capabilityService = capabilityService;
  }

  @GetMapping("/service-capabilities")
  public List<ServiceCapability> getAllServiceCapabilities() {
    return this.capabilityService.getAllServiceCapabilities();
  }

  @PostMapping("/service-capabilities")
  public ServiceCapability createServiceCapability(@RequestBody ServiceCapability newServiceCapability) {
    return this.capabilityService.addServiceCapability(newServiceCapability);
  }
}
