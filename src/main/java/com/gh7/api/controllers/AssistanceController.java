package com.gh7.api.controllers;

import com.gh7.api.services.AssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistance")
public class AssistanceController {

    private final AssistanceService assistanceService;

    @Autowired
    public AssistanceController(final AssistanceService assistanceService) {
        this.assistanceService = assistanceService;
    }

    @PostMapping("/request")
    public void createNewAssistanceRequest() {
        this.assistanceService.handleNewAssistanceRequest();
    }

}
