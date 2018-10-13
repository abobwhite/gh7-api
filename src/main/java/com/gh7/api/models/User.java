package com.gh7.api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.Locale;

public class User {
  @Id
  public String id;
  public String username; // From Auth0
  public String givenName;
  public String familyName;
  @Indexed
  public PhoneNumber phoneNumber;
  public List<Locale> knownLanguages;
  public Locale preferredLanguage;
  public Locale assistanceLanguage;
  public List<ASSISTANCE_CAPABILITY> assistanceCapabilities;
}
