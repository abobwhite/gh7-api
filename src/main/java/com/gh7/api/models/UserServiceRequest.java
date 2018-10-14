package com.gh7.api.models;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.List;

public class UserServiceRequest {
  @Id
  public String id;

  public String requestingUserId;
  public Instant createdAt;

  public ServiceCapability requestedService;
  public String notes;

  public String acceptedVolunteerId;
  public List<String> rejectedVolunteerIds;
}
