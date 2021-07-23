package com.aline.underwritermicroservice.controller;

import com.aline.core.controller.CrudController;
import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.underwritermicroservice.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Application Controller
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController extends CrudController<ApplicationService, ApplicationResponse, Long, ApplyRequest, ApplyRequest> {

    @Autowired
    public void setService(ApplicationService service) {
        this.service = service;
    }

    @Override
    public URI createLocation(ApplicationResponse t) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(t.getId())
                .toUri();
    }
}
