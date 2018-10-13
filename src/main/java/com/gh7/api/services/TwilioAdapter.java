package com.gh7.api.services;

import com.gh7.api.config.TwilioConfig;
import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.*;
import com.twilio.twiml.voice.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TwilioAdapter {

  private static String assistanceRequestScriptEndpoint = "/api/twilio/scripts/assistance-request";
  private static String assistanceRequestGatherCallbackEndpoint = "/api/twilio/callbacks/assistance-request";

  private static String assistanceIVRScriptEndpoint = "/api/twilio/scripts/assistance-ivr";
  private static String assistanceIVRGatherCallbackEndpoint = "/api/twilio/callbacks/assistance-ivr";

  private TwilioConfig twilioConfig;

  @Autowired
  public TwilioAdapter(TwilioConfig twilioConfig) {
    this.twilioConfig = twilioConfig;
    Twilio.init(twilioConfig.accountSid, twilioConfig.authToken);
  }

  public void makeVolunteerCall() {
    String to = "+16365786943";
    PhoneNumber toNumber = new PhoneNumber(to);

    try {
      URI callbackURI = new URI(twilioConfig.host + assistanceRequestScriptEndpoint);
      makeOutgoingCall(toNumber, callbackURI);
    } catch (URISyntaxException ex) {
      System.out.println(ex.toString());
    }
  }

  public VoiceResponse getAssistPromptVoiceScript() {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    responseBuilder.pause(new Pause.Builder().length(1).build());

    try {
      Say prompt = new Say.Builder("This is Beacon, Press one if you are available to assist")
          .voice(Say.Voice.ALICE)
          .language(Say.Language.EN_US)
          .build();

      Gather gather = new Gather.Builder()
          .action(new URI(twilioConfig.host + assistanceRequestGatherCallbackEndpoint))
          .input(Gather.Input.DTMF_SPEECH)
          .timeout(10)
          .numDigits(1)
          .say(prompt)
          .build();

      responseBuilder.gather(gather);
    } catch (URISyntaxException ex) {
      System.out.print(ex.toString());
    }

    return responseBuilder.build();
  }

  public VoiceResponse handleAssistanceRequestGatherResponse(String digits) {

    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    switch (digits) {
      case "1":
        responseBuilder.say(new Say.Builder("Please stay on the line. We will connect you.").build());
        responseBuilder.pause(new Pause.Builder().length(1).build());
        Number outgoingNumber = new Number.Builder("+13149108606").build();
        responseBuilder.dial(new Dial.Builder().number(outgoingNumber).build());
        break;
      default:
        responseBuilder.say(new Say.Builder("We will find someone else. Thanks for your time.").build());
        responseBuilder.hangup(new Hangup.Builder().build());
        break;
    }

    return responseBuilder.build();
  }

  public void makePhoneHelpCall() {
    String to = "+16365786943";
    PhoneNumber toNumber = new PhoneNumber(to);

    try {
      URI callbackURI = new URI(twilioConfig.host + assistanceIVRScriptEndpoint);
      makeOutgoingCall(toNumber, callbackURI);
    } catch (URISyntaxException ex) {
      System.out.println(ex.toString());
    }
  }

  public VoiceResponse getAssistanceIVRScript() {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    responseBuilder.pause(new Pause.Builder().length(1).build());

    try {
      Say prompt = new Say.Builder(
          "This is Beacon. You requested immediate translation help." +
          "Press 1 if you need help with law enforcement." +
          "Press 2 if you need help with a medical issue." +
          "If you need other translation help, just stay on the line.")
          .voice(Say.Voice.ALICE)
          .language(Say.Language.EN_US)
          .build();

      Gather gather = new Gather.Builder()
          .action(new URI(twilioConfig.host + assistanceIVRGatherCallbackEndpoint))
          .input(Gather.Input.DTMF_SPEECH)
          .timeout(10)
          .numDigits(1)
          .say(prompt)
          .build();

      Redirect redirect = new Redirect.Builder(new URI(twilioConfig.host + assistanceIVRGatherCallbackEndpoint))
          .method(HttpMethod.POST)
          .build();

      responseBuilder.gather(gather).redirect(redirect);
    } catch (URISyntaxException ex) {
      System.out.print(ex.toString());
    }

    return responseBuilder.build();
  }

  public VoiceResponse handleAssitanceIVRGatherResponse(String digits) {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    String responseSpeech = "Your request has been received. We will call you back once we have found someone to help";
    switch (digits) {
      case "1":
        responseSpeech = "Your request for law enforcement translation help has been received. " +
                         "We will call you back as soon as possible once we have found someone to help.";
        break;

      case "2":
        responseSpeech = "Your request for medical translation help has been received. " +
                         "We will call you back as soon as possible once we have found someone to help.";
        break;

      default:
        break;
    }
    responseBuilder.say(new Say.Builder(responseSpeech).voice(Say.Voice.ALICE).language(Say.Language.EN_US).build());

    return responseBuilder.build();
  }

  private void makeOutgoingCall(PhoneNumber toNumber, URI callScriptLocation) {
    Call call = Call.creator(toNumber,
        new PhoneNumber(twilioConfig.outboundNumber),
        callScriptLocation).create();

    System.out.println(call.getSid());
  }
}
