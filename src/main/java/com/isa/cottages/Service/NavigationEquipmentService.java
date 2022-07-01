package com.isa.cottages.Service;

import com.isa.cottages.Model.NavigationEquipment;

import java.util.List;

public interface NavigationEquipmentService {

    NavigationEquipment findById(Long id) throws Exception;
    List<NavigationEquipment> findByBoat(Long id) throws Exception;

    NavigationEquipment save(NavigationEquipment navigationEquipment) throws Exception;
    void removeNavigationEquipment(NavigationEquipment navigationEquipment, Long id) throws Exception;
}
