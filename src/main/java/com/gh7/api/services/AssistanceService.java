package com.gh7.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssistanceService {

  private TwilioAdapter twilioAdapter;

  @Autowired
  public AssistanceService(TwilioAdapter twilioAdapter) {
    this.twilioAdapter = twilioAdapter;
  }

  public void handleNewAssistanceRequest() {
    twilioAdapter.makeVolunteerCall();
  }

  public void handleNewAssistancePhoneHelpRequest() {
    twilioAdapter.makePhoneHelpCall();
  }

}
