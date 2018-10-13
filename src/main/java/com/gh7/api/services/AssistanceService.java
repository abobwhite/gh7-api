package com.gh7.api.services;

import com.gh7.api.models.User;
import com.gh7.api.models.UserAssistanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class AssistanceService {

  private TwilioAdapter twilioAdapter;
  private UserService userService;

  @Autowired
  public AssistanceService(UserService userService, TwilioAdapter twilioAdapter) {
    this.userService = userService;
    this.twilioAdapter = twilioAdapter;
  }

  public void handleNewAssistanceRequest(UserAssistanceRequest userAssistanceRequest) {
    locateVolunteerForRequest(userAssistanceRequest);
  }

  public void handleNewAssistancePhoneHelpRequest() {
    twilioAdapter.makePhoneHelpCall();
  }

  @Async
  public void locateVolunteerForRequest(UserAssistanceRequest userAssistanceRequest) {

    List<User> availableVolunteers = userService.findOnCallUsersWithCapability(userAssistanceRequest.requestedCapability);
    if (availableVolunteers.size() == 0) {
      System.out.print("NO MATCHING VOLUNTEERS FOUND FOR REQUEST!!");
    }

    Iterator<User> currentVolunteer = availableVolunteers.iterator();
    boolean requestAccepted = false;
    while (!requestAccepted && currentVolunteer.hasNext()) {
      User volunteerToCall = currentVolunteer.next();
      twilioAdapter.makeVolunteerCall(volunteerToCall);
      requestAccepted = true;
    }
  }

}
