package com.gh7.api.models;

import org.springframework.data.annotation.Id;

import java.time.Instant;

public class UserPhoneHelpRequest {

  @Id
  public String id;

  public String requestingUserId;
  public Instant createdAt;
}
