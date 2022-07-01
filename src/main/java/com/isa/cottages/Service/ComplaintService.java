package com.isa.cottages.Service;

import com.isa.cottages.Model.Complaint;

import java.util.List;

public interface ComplaintService {
    List<Complaint> findAll();
    Complaint findById(Long id) throws Exception;

    Complaint save(Complaint complaint);
    Complaint update(Complaint complaint);
}
