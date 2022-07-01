package com.isa.cottages.Controller;

import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/complaints")
@AllArgsConstructor
public class ComplaintController {

    private ComplaintServiceImpl complaintService;
    private UserServiceImpl userService;
    private CottageReservationServiceImpl cottageReservationService;
    private InstructorReservationsServiceImpl instructorReservationService;
    private BoatReservationServiceImpl boatReservationService;
    private BoatServiceImpl boatService;
    private CottageServiceImpl cottageService;
    private FishingInstructorAdventureServiceImpl instructorService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/boat/new/{id}")
    public ModelAndView makeNewBoatComplaint(@PathVariable Long id, Model model) throws Exception{
        Client client = (Client) this.userService.findByEmail(this.userService.getUserFromPrincipal().getEmail());
        Complaint complaint = new Complaint();
        complaint.setBoat(this.boatService.findById(id));

        model.addAttribute("principal", client);
        model.addAttribute("complaint", complaint);
        model.addAttribute("entity_id", id);

        return new ModelAndView("complaint/new");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/boat/new/{id}")
    public ModelAndView submitBoatComplaint(@PathVariable Long id, @ModelAttribute(name = "complaint") Complaint complaint,
                                            Model model) throws Exception {
        Client client = (Client) this.userService.findByEmail(this.userService.getUserFromPrincipal().getEmail());
        complaint.setComplaintType(ComplaintType.BOAT);
        complaint.setClient(client);
        complaint.setBoat(this.boatService.findById(id));
        if (complaint.getComplaintType() == null || complaint.getClient() == null || complaint.getBoat() == null) {
            return new ModelAndView("complaint/error");
        }

        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        this.complaintService.save(complaint);

        return new ModelAndView("redirect:/boatReservations/history");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/cottage/new/{id}")
    public ModelAndView makeNewCottageComplaint(@PathVariable Long id, Model model) throws Exception{
        Client client = (Client) this.userService.findByEmail(this.userService.getUserFromPrincipal().getEmail());
        Complaint complaint = new Complaint();
        complaint.setCottage(this.cottageService.findById(id));

        model.addAttribute("principal", client);
        model.addAttribute("complaint", complaint);
        model.addAttribute("entity_id", id);

        return new ModelAndView("complaint/new");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cottage/new/{id}")
    public ModelAndView submitCottageComplaint(@PathVariable Long id, @ModelAttribute(name = "complaint") Complaint complaint,
                                            Model model) throws Exception {
        Client client = (Client) this.userService.findByEmail(this.userService.getUserFromPrincipal().getEmail());
        complaint.setComplaintType(ComplaintType.COTTAGE);
        complaint.setClient(client);
        complaint.setCottage(this.cottageService.findById(id));
        if (complaint.getComplaintType() == null || complaint.getClient() == null || complaint.getCottage() == null) {
            return new ModelAndView("complaint/error");
        }

        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        this.complaintService.save(complaint);

        return new ModelAndView("redirect:/cottageReservations/history");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/instructor/new/{id}")
    public ModelAndView makeNewInstructotComplaint(@PathVariable Long id, Model model) throws Exception{
        Client client = (Client) this.userService.findByEmail(this.userService.getUserFromPrincipal().getEmail());
        Complaint complaint = new Complaint();
        complaint.setInstructor(this.instructorService.findById(id));

        model.addAttribute("principal", client);
        model.addAttribute("complaint", complaint);
        model.addAttribute("entity_id", id);

        return new ModelAndView("complaint/new");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/instructor/new/{id}")
    public ModelAndView submitInstructorComplaint(@PathVariable Long id, @ModelAttribute(name = "complaint") Complaint complaint,
                                               Model model) throws Exception {
        Client client = (Client) this.userService.findByEmail(this.userService.getUserFromPrincipal().getEmail());
        complaint.setComplaintType(ComplaintType.INSTRUCTOR);
        complaint.setClient(client);
        complaint.setInstructor(this.instructorService.findById(id));
        if (complaint.getComplaintType() == null || complaint.getClient() == null || complaint.getInstructor() == null) {
            return new ModelAndView("complaint/error");
        }

        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        this.complaintService.save(complaint);

        return new ModelAndView("redirect:/instructorReservations/history");
    }

}
