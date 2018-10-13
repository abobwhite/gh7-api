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

    private TwilioConfig twilioConfig;

    @Autowired
    public TwilioAdapter(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.accountSid, twilioConfig.authToken);
    }

    public void makeCall() {
        String to = "+16365786943";

        try {
            URI callbackURI = new URI(twilioConfig.host + "/api/twilio/callscript");
            Call call = Call.creator(new PhoneNumber(to),
                    new PhoneNumber(twilioConfig.outboundNumber),
                    callbackURI).create();

            System.out.println(call.getSid());
        }
        catch (URISyntaxException ex) {
            System.out.println(ex.toString());
        }
    }

    public VoiceResponse getAssistPromptVoiceScript() {
        VoiceResponse.Builder responseBuilder = new VoiceResponse.Builder();

        try {
            Say prompt = new Say.Builder("This is Beacon, Press one if you are available to assist")
                    .voice(Say.Voice.ALICE)
                    .language(Say.Language.EN_US)
                    .build();

            Gather gather = new Gather.Builder()
                    .action(new URI(twilioConfig.host + "/api/twilio/gather"))
                    .input(Gather.Input.DTMF_SPEECH)
                    .timeout(10)
                    .numDigits(1)
                    .say(prompt)
                    .build();

            Redirect redirect = new Redirect.Builder(new URI(twilioConfig.host + "/api/twilio/callscript"))
                    .method(HttpMethod.POST)
                    .build();

            responseBuilder.gather(gather).redirect(redirect);
        }
        catch (URISyntaxException ex) {
            System.out.print(ex.toString());
        }

        return responseBuilder.build();
    }

    public VoiceResponse handleGatherResponse(String digits) {

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
}
