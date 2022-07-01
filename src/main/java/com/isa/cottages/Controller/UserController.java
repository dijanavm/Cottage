package com.isa.cottages.Controller;

import com.isa.cottages.Exception.ResourceConflictException;
import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.BoatOwnerServiceImpl;
import com.isa.cottages.Service.impl.CottageOwnerServiceImpl;
import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    private final UserServiceImpl userService;
    private final BoatOwnerServiceImpl boatOwnerService;
    private CottageOwnerServiceImpl cottageOwnerService;

    @Autowired
    public UserController(UserServiceImpl userService, BoatOwnerServiceImpl boatOwnerService,
                          CottageOwnerServiceImpl cottageOwnerService) {
        this.userService = userService;
        this.boatOwnerService = boatOwnerService;
        this.cottageOwnerService = cottageOwnerService;
    }

    @GetMapping("/index")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'COTTAGE_OWNER', 'BOAT_OWNER', 'CLIENT', 'INSTRUCTOR')")
    public ModelAndView indexPage(Authentication auth) throws Exception {
        User u = this.userService.findByEmail(auth.getName());
        if (u.getEnabled() == false) {
            // throw new Exception("Your account is not activated, please check your email.");
            return new ModelAndView("confirmEmail");
        }
//        if(AccessDeniedException.class.equals(true)) {
//        if(auth == null) {
//            return new ModelAndView("loginError");
//        }
        return new ModelAndView("indexPage");
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'COTTAGE_OWNER', 'BOAT_OWNER', 'CLIENT', 'INTRUCTOR')")
    public User loadById(@PathVariable Long userId) {
        return this.userService.findById(userId);
    }

    @GetMapping("cottage-owner/home")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView cottageOwnerHome(Model model) throws Exception {
        model.addAttribute("user", this.cottageOwnerService.getCottageOwnerFromPrincipal());
        return new ModelAndView("cottage/myHome");
    }

    @GetMapping("sys-admin/home")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public ModelAndView sysAdminHome(Model model) {
        return new ModelAndView("sys-admin-home");
    }

    @GetMapping("/boat-owner/home")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView boatOwnerHome(Model model) throws Exception {
        model.addAttribute("user", this.boatOwnerService.getBoatOwnerFromPrincipal());
        return new ModelAndView("boat/myHome");
    }

    @GetMapping("/client/home")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView clientHome(Model model, Authentication auth) {
        Client client = (Client) userService.findByEmail(auth.getName());
        model.addAttribute("user", client);
        return new ModelAndView("client/home");
    }

    @GetMapping("/instructor/home")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ModelAndView instructorHome(Model model, Authentication auth) {
        Instructor instructor = (Instructor) userService.findByEmail(auth.getName());
        model.addAttribute("user", instructor);
        return new ModelAndView("instructor/instructorHome");//todo napravi ovu stranicu
    }

    @GetMapping("/registerSystemAdmin")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public ModelAndView regSysAdminForm(Model model) {
        SystemAdministrator systemAdministrator = new SystemAdministrator();
        model.addAttribute(systemAdministrator);
        return new ModelAndView("registerSystemAdmin");
    }

    @PostMapping(value = "/registerSystemAdmin/submit")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public ModelAndView registerSystemAdmin(@ModelAttribute SystemAdministrator user) {
        if (this.userService.findByEmail(user.getEmail()) != null) {
            throw new ResourceConflictException(user.getId(), "Email already exists.");
        }
        this.userService.saveSystemAdmin(user);
        return new ModelAndView("redirect:/user/sys-admin/home");
    }
}
