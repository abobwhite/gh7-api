package com.gh7.api.controllers;

import com.gh7.api.config.TwilioConfig;
import com.gh7.api.services.TwilioAdapter;
import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Redirect;
import com.twilio.twiml.voice.Say;
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

    @PostMapping(value="/callscript", produces = "application/xml; charset=utf-8")
    public String getCallScript() {

        String response = this.twilioAdapter.getAssistPromptVoiceScript().toXml();
        System.out.println(response);
        return response;
    }


    @PostMapping(value = "/gather", produces = "application/xml; charset=utf-8")
    public String handlerGatherResponse(@RequestParam(value = "Digits") String digits) {

        String response = this.twilioAdapter.handleGatherResponse(digits).toXml();
        System.out.println(response);
        return response;
    }
}
