package com.isa.cottages.Controller;

import com.isa.cottages.Model.CottageOwner;
import com.isa.cottages.Service.impl.CottageOwnerServiceImpl;
import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/cottageOwner")
public class CottageOwnerController {

    private CottageOwnerServiceImpl cottageOwnerService;
    private UserServiceImpl userService;

    @Autowired
    public CottageOwnerController (CottageOwnerServiceImpl cottageOwnerService, UserServiceImpl userService){
        this.cottageOwnerService = cottageOwnerService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/profile/{id}")
    public ModelAndView showProfile(Model model, @PathVariable("id") Long id) throws Exception {
        CottageOwner cottageOwner = cottageOwnerService.findById(id);
        model.addAttribute("principal", cottageOwner);
        return new ModelAndView("cottage/profile");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/profile/{id}/editProfile")
    public ModelAndView updateProfile(Model model, @PathVariable("id") Long id) throws Exception {
        CottageOwner cottageOwner = this.cottageOwnerService.findById(id);
        model.addAttribute("principal", cottageOwner);
        return new ModelAndView("cottage/editProfile");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/profile/{id}/editProfile/submit")
    public ModelAndView updateProfile(@PathVariable("id") Long id, Model model, @ModelAttribute CottageOwner cottageOwner) {
        try {
            this.cottageOwnerService.updateProfile(cottageOwner);
            model.addAttribute("principal", cottageOwner);
            return new ModelAndView("redirect:/cottageOwner/profile/" + id.toString());
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/defineUnavailability")
    public ModelAndView defineUnavailability(Model model, @PathVariable Long id) throws Exception {
        CottageOwner cottageOwner = cottageOwnerService.findById(id);
        model.addAttribute("id", id);
        model.addAttribute("principal", cottageOwner);

        return new ModelAndView("cottage/defineUnavailability");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/{id}/defineUnavailability/submit")
    public ModelAndView defineUnavailability(Model model, @PathVariable Long id, @ModelAttribute CottageOwner cottageOwner) throws Exception {
        model.addAttribute("id", id);
        model.addAttribute("principal", cottageOwner);

        this.cottageOwnerService.defineUnavailability(cottageOwner);

        return new ModelAndView("redirect:/cottageReservations/viewCalendar/{id}/");
    }

}
