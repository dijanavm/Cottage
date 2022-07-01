package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.BoatOwner;
import com.isa.cottages.Repository.BoatOwnerRepository;
import com.isa.cottages.Service.BoatOwnerService;
import com.isa.cottages.authFasace.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoatOwnerServiceImpl implements BoatOwnerService {

    private BoatOwnerRepository boatOwnerRepository;
    private AuthenticationFacade facade;

    @Autowired
    public BoatOwnerServiceImpl (BoatOwnerRepository boatOwnerRepository, AuthenticationFacade facade) {
        this.boatOwnerRepository = boatOwnerRepository;
        this.facade = facade;
    }

    @Override
    public BoatOwner findById(Long id) throws Exception {
        if (this.boatOwnerRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(BoatOwner service)");
        }
        return this.boatOwnerRepository.findById(id).get();
    }

    @Override
    public BoatOwner updateBoats(BoatOwner boatOwner) throws Exception {
        BoatOwner forUpdate = this.findById(boatOwner.getId());
        if(forUpdate == null) { throw new Exception("Boat owner does not exist."); }

        forUpdate.setBoats(boatOwner.getBoats());
        this.boatOwnerRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public BoatOwner updateProfile(BoatOwner boatOwner) throws Exception {
        BoatOwner forUpdate = this.findById(boatOwner.getId());

        forUpdate.setFirstName(boatOwner.getFirstName());
        forUpdate.setLastName(boatOwner.getLastName());
        forUpdate.setPhoneNumber(boatOwner.getPhoneNumber());
        forUpdate.setResidence(boatOwner.getResidence());
        forUpdate.setCity(boatOwner.getCity());
        forUpdate.setState(boatOwner.getState());

        this.boatOwnerRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public BoatOwner defineUnavailability(BoatOwner boatOwner) throws Exception {
        BoatOwner forUpdate = findById(boatOwner.getId());

        forUpdate.setUnavailableFrom(boatOwner.getUnavailableFrom());
        forUpdate.setUnavailableUntil(boatOwner.getUnavailableUntil());

        this.boatOwnerRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public BoatOwner findBoatOwnerByEmail(String email) throws Exception {
        BoatOwner boatOwner = this.boatOwnerRepository.findByEmail(email);
        if (boatOwner == null) {
            throw new Exception("BoatOwner with this email does not exist");
        }
        return boatOwner;
    }

    @Override
    public BoatOwner getBoatOwnerFromPrincipal() throws Exception {
        String principal = this.facade.getPrincipalEmail();

        return findBoatOwnerByEmail(principal);
    }
}
