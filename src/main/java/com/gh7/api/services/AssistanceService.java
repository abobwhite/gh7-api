package com.gh7.api.services;

import com.gh7.api.exceptions.UserNotFoundException;
import com.gh7.api.models.User;
import com.gh7.api.models.UserAssistanceRequest;
import com.gh7.api.models.UserPhoneHelpRequest;
import com.gh7.api.repositories.UserAssistanceRequestRepository;
import com.gh7.api.repositories.UserPhoneHelpRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class AssistanceService {

  private TwilioAdapter twilioAdapter;
  private UserService userService;
  private UserPhoneHelpRequestRepository userPhoneHelpRequestRepository;
  private UserAssistanceRequestRepository userAssistanceRequestRepository;

  @Autowired
  public AssistanceService(UserService userService,
                           TwilioAdapter twilioAdapter,
                           UserPhoneHelpRequestRepository userPhoneHelpRequestRepository,
                           UserAssistanceRequestRepository userAssistanceRequestRepository) {
    this.userService = userService;
    this.twilioAdapter = twilioAdapter;
    this.userAssistanceRequestRepository = userAssistanceRequestRepository;
    this.userPhoneHelpRequestRepository = userPhoneHelpRequestRepository;
  }

  public void handleNewAssistanceRequest(UserAssistanceRequest userAssistanceRequest) {
    UserAssistanceRequest savedRequest = this.userAssistanceRequestRepository.save(userAssistanceRequest);

    locateVolunteerForRequest(savedRequest);
  }

  public void handleNewAssistancePhoneHelpRequest(UserPhoneHelpRequest userPhoneHelpRequest) {
    this.userPhoneHelpRequestRepository.save(userPhoneHelpRequest);
    try {
      User user = this.userService.getUserById(userPhoneHelpRequest.requestingUserId);
      twilioAdapter.makePhoneHelpCall(user);
    }
    catch (UserNotFoundException ex) {
      System.out.print("Failed to look up expected user!");
    }
  }

  @Async
  public void locateVolunteerForRequest(UserAssistanceRequest userAssistanceRequest) {

    // TODO: Rework all of this to remove already rejected users...
    List<User> availableVolunteers = userService.findOnCallUsersWithCapability(userAssistanceRequest.requestedCapability);
    if (availableVolunteers.size() == 0) {
      System.out.print("NO MATCHING VOLUNTEERS FOUND FOR REQUEST!!");
    }

    Iterator<User> currentVolunteer = availableVolunteers.iterator();
    boolean requestAccepted = false;
    while (!requestAccepted && currentVolunteer.hasNext()) {
      User volunteerToCall = currentVolunteer.next();
      twilioAdapter.makeVolunteerCall(userAssistanceRequest, volunteerToCall);
      requestAccepted = true;
    }
  }

}
