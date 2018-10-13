package com.gh7.api.controllers;

import com.gh7.api.services.TwilioAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/twilio")
public class TwilioController {

  private TwilioAdapter twilioAdapter;

  @Autowired
  public TwilioController(TwilioAdapter twilioAdapter) {
    this.twilioAdapter = twilioAdapter;
  }

  @PostMapping(value = "/scripts/assistance-request", produces = "application/xml; charset=utf-8")
  public String getAssistanceRequestScript() {

    String response = this.twilioAdapter.getAssistPromptVoiceScript().toXml();
    System.out.println(response);
    return response;
  }

  @PostMapping(value = "/callbacks/assistance-request", produces = "application/xml; charset=utf-8")
  public String handleAssistanceRequestGatherResponse(@RequestParam(value = "Digits") String digits) {

    String response = this.twilioAdapter.handleAssistanceRequestGatherResponse(digits).toXml();
    System.out.println(response);
    return response;
  }

  @PostMapping(value = "/scripts/assistance-ivr", produces = "application/xml; charset=utf-8")
  public String getAssistanceIVRScript() {

    String response = this.twilioAdapter.getAssistanceIVRScript().toXml();
    System.out.println(response);
    return response;
  }

  @PostMapping(value = "/callbacks/assistance-ivr", produces = "application/xml; charset=utf-8")
  public String handleAssistanceIVRGatherResponse(@RequestParam(value = "Digits", required = false) String digits) {

    String response = this.twilioAdapter.handleAssitanceIVRGatherResponse(digits).toXml();
    System.out.println(response);
    return response;
  }
}
