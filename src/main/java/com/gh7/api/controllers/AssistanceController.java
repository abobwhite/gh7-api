package com.gh7.api.controllers;

import com.gh7.api.models.ASSISTANCE_CAPABILITY;
import com.gh7.api.models.UserAssistanceRequest;
import com.gh7.api.models.UserPhoneHelpRequest;
import com.gh7.api.services.AssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/assistance")
public class AssistanceController {

  private final AssistanceService assistanceService;

  @Autowired
  public AssistanceController(final AssistanceService assistanceService) {
    this.assistanceService = assistanceService;
  }

  @PostMapping("/request")
  public void createNewAssistanceRequest(@RequestBody() UserAssistanceRequest userAssistanceRequest) {
    userAssistanceRequest.createdAt = Instant.now();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUser = authentication.getName();
    userAssistanceRequest.requestingUserId = authenticatedUser;

    this.assistanceService.handleNewAssistanceRequest(userAssistanceRequest);
  }

  @PostMapping("/phonehelp")
  public void initiateAssistancePhoneHelpCall() {

    UserPhoneHelpRequest userPhoneHelpRequest = new UserPhoneHelpRequest();
    userPhoneHelpRequest.createdAt = Instant.now();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    userPhoneHelpRequest.requestingUserId = authentication.getName();

    this.assistanceService.handleNewAssistancePhoneHelpRequest(userPhoneHelpRequest);
  }

}
