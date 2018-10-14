package com.gh7.api.controllers;

import com.gh7.api.exceptions.UserNotFoundException;
import com.gh7.api.models.ASSISTANCE_CAPABILITY;
import com.gh7.api.models.User;
import com.gh7.api.models.UserAssistanceRequest;
import com.gh7.api.repositories.UserAssistanceRequestRepository;
import com.gh7.api.services.AssistanceService;
import com.gh7.api.services.TwilioAdapter;
import com.gh7.api.services.UserService;
import com.twilio.twiml.VoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/twilio")
public class TwilioController {

  private TwilioAdapter twilioAdapter;
  private UserService userService;
  private UserAssistanceRequestRepository userAssistanceRequestRepository;
  private AssistanceService assistanceService;

  @Autowired
  public TwilioController(TwilioAdapter twilioAdapter,
                          UserService userService,
                          UserAssistanceRequestRepository userAssistanceRequestRepository,
                          AssistanceService assistanceService) {
    this.twilioAdapter = twilioAdapter;
    this.userService = userService;
    this.userAssistanceRequestRepository = userAssistanceRequestRepository;
    this.assistanceService = assistanceService;
  }

  @PostMapping(value = "/scripts/assistance-request/{userAssistanceRequestId}/{volunteerId}",
               produces = "application/xml; charset=utf-8")
  public String getAssistanceRequestScript(@PathVariable() String userAssistanceRequestId,
                                           @PathVariable() String volunteerId) {

    Optional<UserAssistanceRequest> optionalRequest = Optional.empty();
    User volunteer = null;
    try {
      optionalRequest = this.userAssistanceRequestRepository.findById(userAssistanceRequestId);
      volunteer = this.userService.getUserById(volunteerId);
    }
    catch (UserNotFoundException ex) {
    }

    if (!optionalRequest.isPresent() || volunteer == null) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
      return this.twilioAdapter.generateGenericErrorResponse().toXml();
    }
    UserAssistanceRequest request = optionalRequest.get();

    String response = this.twilioAdapter.getAssistPromptVoiceScript(request, volunteer).toXml();
    System.out.println(response);
    return response;
  }

  @PostMapping(value = "/callbacks/assistance-request/{userAssistanceRequestId}/{volunteerId}",
               produces = "application/xml; charset=utf-8")
  public String handleAssistanceRequestGatherResponse(@PathVariable() String userAssistanceRequestId,
                                                      @PathVariable() String volunteerId,
                                                      @RequestParam(value = "Digits") String digits) {
    UserAssistanceRequest request = null;
    User volunteer = null;
    User requestor = null;
    try {
      Optional<UserAssistanceRequest> optionalRequest = this.userAssistanceRequestRepository.findById(userAssistanceRequestId);
      if (optionalRequest.isPresent()) {
        request = optionalRequest.get();
        volunteer = this.userService.getUserById(volunteerId);
        requestor = this.userService.getUserById(request.requestingUserId);
      }
    }
    catch (UserNotFoundException ex) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
    }

    if (request == null || volunteer == null || requestor == null) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
      return this.twilioAdapter.generateGenericErrorResponse().toXml();
    }

    VoiceResponse voiceResponse;
    switch (digits) {
      case "1":
        voiceResponse = this.twilioAdapter.respondToApprovedAssistanceRequest(requestor, volunteer);
        break;

      default:
        voiceResponse = this.twilioAdapter.respondToDeclinedAssistanceRequest(volunteer);
        request.rejectedVolunteerIds.add(volunteerId);
        this.userAssistanceRequestRepository.save(request);
        this.assistanceService.locateVolunteerForRequest(request);
        break;
    }

    String response = voiceResponse.toXml();
    System.out.println(response);
    return response;
  }

  @PostMapping(value = "/scripts/assistance-ivr/{userId}",
               produces = "application/xml; charset=utf-8")
  public String getAssistanceIVRScript(@PathVariable() String userId) {

    User requestor = null;
    try {
      requestor = this.userService.getUserById(userId);
    }
    catch (UserNotFoundException ex) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
    }

    if (requestor == null) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
      return this.twilioAdapter.generateGenericErrorResponse().toXml();
    }

    String response = this.twilioAdapter.getAssistanceIVRScript(requestor).toXml();
    System.out.println(response);
    return response;
  }

  @PostMapping(value = "/callbacks/assistance-ivr/{userId}",
               produces = "application/xml; charset=utf-8")
  public String handleAssistanceIVRGatherResponse(@PathVariable() String userId,
                                                  @RequestParam(value = "Digits") String digits) {

    User requestor = null;
    try {
      requestor = this.userService.getUserById(userId);
    }
    catch (UserNotFoundException ex) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
    }

    if (requestor == null) {
      System.out.print("Failed to retrieve the related objects for a Twilio Callback");
      return this.twilioAdapter.generateGenericErrorResponse().toXml();
    }

    ASSISTANCE_CAPABILITY capability = ASSISTANCE_CAPABILITY.GENERAL_TRANSLATION;
    switch (digits) {
      case "1":
        capability = ASSISTANCE_CAPABILITY.LAW_ENFORCEMENT_TRANSLATION;
        break;

      case "2":
        capability = ASSISTANCE_CAPABILITY.MEDICAL_TRANSLATION;
        break;
    }

    String response = this.twilioAdapter.respondToIVRResponse(requestor, capability).toXml();
    System.out.println(response);

    UserAssistanceRequest userAssistanceRequest = new UserAssistanceRequest();
    userAssistanceRequest.requestedCapability = capability;
    userAssistanceRequest.requestingUserId = requestor.id;
    userAssistanceRequest.createdAt = Instant.now();
    this.assistanceService.handleNewAssistanceRequest(userAssistanceRequest);

    return response;
  }
}
