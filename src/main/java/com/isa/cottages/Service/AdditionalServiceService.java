package com.isa.cottages.Service;

import com.isa.cottages.Model.AdditionalService;
import com.isa.cottages.Model.Boat;

import java.util.List;
import java.util.Set;

public interface AdditionalServiceService {

    AdditionalService findById(Long id) throws Exception;
    AdditionalService findOne(Long id);
    AdditionalService save (AdditionalService additionalService) throws Exception;

    void removeAdditionalServiceFromBoat(AdditionalService additionalService, Long id) throws Exception;
    void removeAdditionalServiceFromCottage(AdditionalService additionalService, Long id) throws Exception;

    List<AdditionalService> findByCottage(Long id) throws Exception;
    List<AdditionalService> findByBoat(Long id) throws Exception;

    Set<Long> getIds(Set<AdditionalService> services);


}
