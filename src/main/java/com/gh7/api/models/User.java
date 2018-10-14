package com.gh7.api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.Locale;

public class User {
  @Id
  public String id;
  public String email;
  public String givenName;
  public String familyName;
  public boolean capabilitiesVerified;
  public boolean onCall;
  @Indexed
  public PhoneNumber phoneNumber;
  public List<Locale> knownLanguages;
  public Locale preferredLanguage;
  public Locale assistanceLanguage;
  public List<ASSISTANCE_CAPABILITY> assistanceCapabilities;
}
