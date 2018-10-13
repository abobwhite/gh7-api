package com.gh7.api.models;

import java.time.Instant;

public class UserAssistanceRequest {
  public String requestingUserId;
  public ASSISTANCE_CAPABILITY requestedCapability;
  public Instant createdAt;
}
