package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.Complaint;
import com.isa.cottages.Repository.ComplaintRepository;
import com.isa.cottages.Service.ComplaintService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private ComplaintRepository complaintRepository;

    @Override
    public List<Complaint> findAll() { return this.complaintRepository.findAll(); }

    @Override
    public Complaint findById(Long id) throws Exception {
        if (this.complaintRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(Complaint service)");
        }
        return this.complaintRepository.findById(id).get();
    }

    @Override
    public Complaint save(Complaint complaint) { return this.complaintRepository.save(complaint); }

    @Override
    public Complaint update(Complaint complaint) {
        Complaint c = this.complaintRepository.getById(complaint.getId());
        c.setText(complaint.getText());
        c.setClient(complaint.getClient());
        c.setComplaintType(complaint.getComplaintType());
        c.setIsAnswered(complaint.getIsAnswered());
        c.setResponse(complaint.getResponse());

        return this.complaintRepository.save(c);
    }
}
