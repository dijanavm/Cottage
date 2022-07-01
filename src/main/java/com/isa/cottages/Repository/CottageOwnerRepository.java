package com.isa.cottages.Repository;

import com.isa.cottages.Model.CottageOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CottageOwnerRepository extends JpaRepository<CottageOwner, Long> {

    CottageOwner findByEmail(String email);
}

