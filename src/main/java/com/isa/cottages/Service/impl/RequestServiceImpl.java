package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.Request;
import com.isa.cottages.Repository.RequestRepository;
import com.isa.cottages.Service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl implements RequestService {

    private RequestRepository requestRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Request save(Request request) {
        Request r = new Request();
        r.setApproved(request.getApproved());
        r.setText(request.getText());

        this.requestRepository.save(r);
        return r;
    }
}
