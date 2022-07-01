package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.BoatOwner;
import com.isa.cottages.Model.CottageOwner;
import com.isa.cottages.Model.Report;
import com.isa.cottages.Repository.ReportRepository;
import com.isa.cottages.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private ReportRepository reportRepository;
    private UserServiceImpl userService;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, UserServiceImpl userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }

    @Override
    public Report findById(Long id) throws Exception {

        if(this.reportRepository.getById(id) == null) {
            throw new Exception("Report with this id does not exist");
        }
        return this.reportRepository.getById(id);
    }

    @Override
    public List<Report> findBoatOwnersReports(Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();

        return this.reportRepository.findBoatOwnersReports(id);
    }

    @Override
    public List<Report> findCottageOwnersReports(Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();

        return this.reportRepository.findCottageOwnersReports(id);
    }

    @Override
    public Report save(Report report) throws Exception {
        Report r = new Report();
        r.setText(report.getText());
        r.setPenal(report.getPenal());
        r.setDidAppear(report.getDidAppear());
        r.setBoatOwner(report.getBoatOwner());
        r.setAdmin(report.getAdmin());
        r.setClient(report.getClient());
        r.getClient().setPenalties(report.getClient().getPenalties());
        r.setApproved(report.getApproved());

        return this.reportRepository.save(r);
    }
}
