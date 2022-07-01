package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.*;
import com.isa.cottages.Repository.AdditionalServiceRepository;
import com.isa.cottages.Service.AdditionalServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private AdditionalServiceRepository additionalServiceRepository;
    private CottageServiceImpl cottageService;
    private UserServiceImpl userService;
    private BoatServiceImpl boatService;

    @Autowired
    public AdditionalServiceServiceImpl (AdditionalServiceRepository additionalServiceRepository,
                                         CottageServiceImpl cottageService, UserServiceImpl userService,
                                         BoatServiceImpl boatService) {
        this.additionalServiceRepository = additionalServiceRepository;
        this.cottageService = cottageService;
        this.userService = userService;
        this.boatService = boatService;
    }

    @Override
    public AdditionalService findById(Long id) throws Exception {
        if (this.additionalServiceRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(additionalService service)");
        }
        return this.additionalServiceRepository.findById(id).get();
    }

    @Override
    public AdditionalService findOne(Long id) {
        return additionalServiceRepository.getOne(id);
    }

    @Override
    public AdditionalService save(AdditionalService additionalService) throws Exception {
        AdditionalService as = new AdditionalService();
        as.setName(additionalService.getName());
        as.setPrice(additionalService.getPrice());
        as.setCottage(additionalService.getCottage());
        as.setBoat(additionalService.getBoat());

        this.additionalServiceRepository.save(as);

        return as;
    }

    @Override
    public void removeAdditionalServiceFromBoat(AdditionalService additionalService, Long id) throws Exception {
        Boat boat = boatService.findById(id);

        AdditionalService as = findById(additionalService.getId());

        Set<AdditionalService> additionalServices = boat.getAdditionalServices();
        additionalServices.remove(as);
        boat.setAdditionalServices(additionalServices);
        additionalService.setDeleted(true);

        as.setBoat(null);
        this.boatService.updateAdditionalServices(boat);
    }

    @Override
    public void removeAdditionalServiceFromCottage(AdditionalService additionalService, Long id) throws Exception {
        Cottage cottage = cottageService.findById(id);

        AdditionalService as = findById(additionalService.getId());

        Set<AdditionalService> additionalServices = cottage.getAdditionalServices();
        additionalServices.remove(as);
        cottage.setAdditionalServices(additionalServices);
        additionalService.setDeleted(true);

        as.setCottage(null);
        this.cottageService.updateAdditionalServices(cottage);
    }

    @Override
    public List<AdditionalService> findByCottage(Long id) throws Exception{
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        Cottage cottage = cottageService.findById(id);
        List<AdditionalService> all = this.additionalServiceRepository.findByCottage(id);
        List<AdditionalService> myAdditionalServices = new ArrayList<AdditionalService>();

        for (AdditionalService as:all) {
            if(Objects.equals(as.getCottage().getId(), cottage.getId())) {
                myAdditionalServices.add(as);
            }
        }
        return myAdditionalServices;
    }

    @Override
    public List<AdditionalService> findByBoat(Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        Boat boat = boatService.findById(id);
        List<AdditionalService> all = this.additionalServiceRepository.findByBoat(id);
        List<AdditionalService> myAdditionalServices = new ArrayList<AdditionalService>();

        for (AdditionalService as : all) {
            if (Objects.equals(as.getBoat().getId(), boat.getId())) {
                myAdditionalServices.add(as);
            }
        }
        return myAdditionalServices;
    }

    @Override
    public Set<Long> getIds(Set<AdditionalService> services) {
        Set<Long> ids = new HashSet<>();
        for (AdditionalService s : services) { ids.add(s.getId()); }
        return ids;
    }

}
