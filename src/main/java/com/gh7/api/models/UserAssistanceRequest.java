package com.gh7.api.models;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserAssistanceRequest {

  @Id
  public String id;

  public String requestingUserId;
  public ASSISTANCE_CAPABILITY requestedCapability;
  public Instant createdAt;

  public List<String> rejectedVolunteerIds = new ArrayList<>();
}
