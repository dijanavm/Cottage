package com.isa.cottages.Controller;

import com.isa.cottages.Model.BoatOwner;
import com.isa.cottages.Model.Client;
import com.isa.cottages.Model.CottageOwner;
import com.isa.cottages.Model.User;
import com.isa.cottages.Service.impl.ClientServiceImpl;
import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/client")
public class ClientController {

    private ClientServiceImpl clientService;
    private UserServiceImpl userService;

    @Autowired
    public ClientController(ClientServiceImpl clientService, UserServiceImpl userService){
        this.clientService = clientService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/home")
    public ModelAndView showHomePage(Model model) throws Exception {
        model.addAttribute("user", this.userService.getUserFromPrincipal());
        return new ModelAndView("client/home");
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'COTTAGE_OWNER', 'BOAT_OWNER')")
    @GetMapping("/profile/{id}")
    public ModelAndView showProfile(Model model, @PathVariable("id") Long id) throws Exception {
        Client client = clientService.findById(id);
        model.addAttribute("user", client);
        User user = userService.getUserFromPrincipal();
        model.addAttribute("owner", user);
        return new ModelAndView("client/profile");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/profile/{id}/editProfile")
    public ModelAndView updateProfile(Model model, @PathVariable("id") Long id) throws Exception {
        Client client = this.clientService.findById(id);
        model.addAttribute("user", client);
        return new ModelAndView("client/profileUpdateForm");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/profile/{id}/editProfile")
    public ModelAndView updateProfile(@PathVariable("id") Long id, Model model, @ModelAttribute Client client) {
        try {
            this.clientService.updateProfile(client);
            model.addAttribute("user", client);
            return new ModelAndView("redirect:/client/profile/" + id.toString());
        } catch (Exception e) {

            // TODO: Namesti redirektovanje ka stranici sa informacijom o gresci umesto ka pocetnoj strani
            return new ModelAndView("home");
        }
    }
}
