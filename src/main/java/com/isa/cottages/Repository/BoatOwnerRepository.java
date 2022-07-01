package com.isa.cottages.Repository;

import com.isa.cottages.Model.BoatOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoatOwnerRepository extends JpaRepository<BoatOwner, Long> {

    BoatOwner findByEmail(String email);
}
