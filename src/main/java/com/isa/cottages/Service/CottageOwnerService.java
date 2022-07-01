package com.isa.cottages.Service;

import com.isa.cottages.Model.CottageOwner;

public interface CottageOwnerService {

    CottageOwner findById(Long id) throws Exception;

    CottageOwner updateProfile(CottageOwner cottageOwner) throws Exception;

    CottageOwner updateCottages(CottageOwner cottageOwner) throws Exception;

    CottageOwner defineUnavailability(CottageOwner cottageOwner) throws Exception;

    CottageOwner getCottageOwnerFromPrincipal() throws Exception;
    CottageOwner findCottageOwnerByEmail(String email) throws Exception;
}
