package com.isa.cottages.Controller;

import com.isa.cottages.Model.Instructor;
import com.isa.cottages.Service.impl.InstructorServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/instructor")
public class InstructorController {

    private InstructorServiceImpl instructorService;


    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/profile/{id}")
    public ModelAndView showProfile(Model model, @PathVariable("id") Long id) throws Exception {
        Instructor instructor = instructorService.findById(id);
        model.addAttribute("principal", instructor);
        return new ModelAndView("instructor/profile");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/profile/{id}/editProfile")
    public ModelAndView updateProfile(Model model, @PathVariable("id") Long id) throws Exception {
        Instructor instructor = this.instructorService.findById(id);
        model.addAttribute("principal", instructor);
        return new ModelAndView("instructor/editProfile");
    }
}
