package com.gh7.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class TwilioConfig {

  @Value("${twilio.host}")
  public String host;

  @Value("${twilio.account.sid}")
  public String accountSid;

  @Value("${twilio.auth_token}")
  public String authToken;

  @Value("${twilio.numbers.outbound}")
  public String outboundNumber;
}
