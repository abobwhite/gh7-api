package com.gh7.api.services;

import com.gh7.api.config.TwilioConfig;
import com.gh7.api.models.ASSISTANCE_CAPABILITY;
import com.gh7.api.models.User;
import com.gh7.api.models.UserAssistanceRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.*;
import com.twilio.twiml.voice.Number;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;

@Service
public class TwilioAdapter {

  private static final String assistanceRequestScriptEndpoint = "/api/twilio/scripts/assistance-request/";
  private static final String assistanceRequestGatherCallbackEndpoint = "/api/twilio/callbacks/assistance-request/";

  private static final String assistanceIVRScriptEndpoint = "/api/twilio/scripts/assistance-ivr/";
  private static final String assistanceIVRGatherCallbackEndpoint = "/api/twilio/callbacks/assistance-ivr/";

  private TwilioConfig twilioConfig;
  private final TranslationResourceBundleService translationResourceBundleService;

  @Autowired
  public TwilioAdapter(TwilioConfig twilioConfig, final TranslationResourceBundleService translationResourceBundleService) {
    this.twilioConfig = twilioConfig;
    this.translationResourceBundleService = translationResourceBundleService;
    Twilio.init(twilioConfig.accountSid, twilioConfig.authToken);
  }

  public VoiceResponse generateGenericErrorResponse() {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    responseBuilder.pause(new Pause.Builder().length(1).build());
    responseBuilder.say(new Say.Builder("An unexpected error has occurred in Beacon. We're very sorry for any inconvenience and will work to correct the issue.").build());
    responseBuilder.hangup(new Hangup.Builder().build());
    return responseBuilder.build();
  }

  public void makeVolunteerCall(UserAssistanceRequest userAssistanceRequest, User volunteerToCall) {
    PhoneNumber toNumber = convertUserPhoneToTwilioPhone(volunteerToCall.phoneNumber);

    try {
      URI callbackURI = new URI(twilioConfig.host + assistanceRequestScriptEndpoint + userAssistanceRequest.id + "/" + encodeValue(volunteerToCall.id));
      makeOutgoingCall(toNumber, callbackURI);
    } catch (URISyntaxException ex) {
      System.out.println(ex.toString());
    }
  }

  public VoiceResponse getAssistPromptVoiceScript(UserAssistanceRequest userAssistanceRequest, User volunteer) {

    Say.Language language = this.convertUserPreferredLanguageToTwilioLanguage(volunteer);
    String contentKey = "twilio.assist-prompt";
    switch (userAssistanceRequest.requestedCapability) {
      case LAW_ENFORCEMENT_TRANSLATION:
        contentKey += ".law-enforcement";
        break;
      case MEDICAL_TRANSLATION:
        contentKey += ".medical";
        break;
    }
    String content = this.translate(contentKey, volunteer.preferredLanguage);

    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    responseBuilder.pause(new Pause.Builder().length(1).build());

    try {
      Say prompt = new Say.Builder(content)
          .language(language)
          .build();

      Gather gather = new Gather.Builder()
          .action(new URI(twilioConfig.host +
              assistanceRequestGatherCallbackEndpoint +
              userAssistanceRequest.id + "/" +
              encodeValue(volunteer.id)))
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

  public VoiceResponse respondToApprovedAssistanceRequest(User requestingUser, User volunteer) {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();

    String content = translate("twilio.assist-confirmed", volunteer.preferredLanguage);
    Say say = new Say.Builder(content)
        .language(convertUserPreferredLanguageToTwilioLanguage(volunteer))
        .build();

    responseBuilder.say(say);
    responseBuilder.pause(new Pause.Builder().length(1).build());
    PhoneNumber phoneNumber = convertUserPhoneToTwilioPhone(requestingUser.phoneNumber);
    Number outgoingNumber = new Number.Builder(phoneNumber).build();
    responseBuilder.dial(new Dial.Builder().number(outgoingNumber).build());
    return responseBuilder.build();
  }

  public VoiceResponse respondToDeclinedAssistanceRequest(User requestingUser) {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();

    String content = translate("twilio.assist-denied", requestingUser.preferredLanguage);
    Say say = new Say.Builder(content)
        .language(convertUserPreferredLanguageToTwilioLanguage(requestingUser))
        .build();

    responseBuilder.say(say);
    responseBuilder.hangup(new Hangup.Builder().build());
    return responseBuilder.build();
  }

  public void makePhoneHelpCall(User user) {
    PhoneNumber toNumber = this.convertUserPhoneToTwilioPhone(user.phoneNumber);

    try {
      URI callbackURI = new URI(twilioConfig.host + assistanceIVRScriptEndpoint + encodeValue(user.id));
      makeOutgoingCall(toNumber, callbackURI);
    } catch (URISyntaxException ex) {
      System.out.println(ex.toString());
    }
  }

  public VoiceResponse getAssistanceIVRScript(User requestingUser) {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    responseBuilder.pause(new Pause.Builder().length(1).build());

    String content = translate("twilio.phone-help-ivr-menu",
        requestingUser.preferredLanguage);

    try {
      Say prompt = new Say.Builder(content)
          .language(convertUserPreferredLanguageToTwilioLanguage(requestingUser))
          .build();

      Gather gather = new Gather.Builder()
          .action(new URI(twilioConfig.host +
              assistanceIVRGatherCallbackEndpoint +
              encodeValue(requestingUser.id)))
          .input(Gather.Input.DTMF)
          .timeout(10)
          .numDigits(1)
          .say(prompt)
          .build();

      /*
      Redirect redirect = new Redirect.Builder(new URI(twilioConfig.host +
          assistanceIVRScriptEndpoint +
          encodeValue(requestingUser.id)))
          .method(HttpMethod.POST)
          .build();
          */

      responseBuilder.gather(gather);//.redirect(redirect);
    } catch (URISyntaxException ex) {
      System.out.print(ex.toString());
    }

    return responseBuilder.build();
  }

  public VoiceResponse respondToIVRResponse(User requestor, ASSISTANCE_CAPABILITY requiredCapability) {
    VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();
    String contentKey = "twilio.ivr-response";
    switch (requiredCapability) {
      case LAW_ENFORCEMENT_TRANSLATION:
        contentKey += ".law-enforcement";
        break;

      case MEDICAL_TRANSLATION:
        contentKey += ".medical";
    }
    String content = translate(contentKey, requestor.preferredLanguage);

    Say say = new Say.Builder(content)
        .language(convertUserPreferredLanguageToTwilioLanguage(requestor))
        .build();
    responseBuilder.say(say);

    return responseBuilder.build();
  }

  private void makeOutgoingCall(PhoneNumber toNumber, URI callScriptLocation) {
    Call.creator(toNumber,
        new PhoneNumber(twilioConfig.outboundNumber),
        callScriptLocation).create();
  }

  private PhoneNumber convertUserPhoneToTwilioPhone(com.gh7.api.models.PhoneNumber userPhone) {
    String countryCode = userPhone.countryCode;
    String areaCode = userPhone.areaCode;
    String exchange = userPhone.exchange;
    String lineCode = userPhone.lineNumber;
    return new PhoneNumber("+" + countryCode + areaCode + exchange + lineCode);
  }

  private Say.Language convertUserPreferredLanguageToTwilioLanguage(User user) {

    String localeId = MessageFormat.format("{0}-{1}",
        user.preferredLanguage.getLanguage(),
        user.preferredLanguage.getCountry());

    System.out.println("Converting user locale to Twilio locale: " + user.preferredLanguage.toString() + " : " + localeId);
    switch (localeId) {
      case "es-ES":
        return Say.Language.ES_ES;
      default:
        return Say.Language.EN_US;
    }
  }

  private String encodeValue(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException ex) {
      System.out.print("Unsupported Encoding Exception Thrown...");
      return value;
    }
  }

  @Cacheable("translation")
  public String translate(String translationKey, Locale locale) {
    return translationResourceBundleService.getTranslationResourceBundle(locale).getString(translationKey);
  }
}
