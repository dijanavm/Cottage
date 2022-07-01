package com.isa.cottages.Controller;

import com.isa.cottages.Model.Boat;
import com.isa.cottages.Model.Client;
import com.isa.cottages.Model.Cottage;
import com.isa.cottages.Model.FishingInstructorAdventure;
import com.isa.cottages.Service.impl.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private UserServiceImpl userService;
    private ClientServiceImpl clientService;
    private CottageServiceImpl cottageService;
    private BoatServiceImpl boatService;
    private FishingInstructorAdventureServiceImpl instructorService;

    @GetMapping("/boat")
    public ModelAndView getAllBoat(Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        model.addAttribute("subscriptions", client.getBoatSubscriptions());
        model.addAttribute("principal", client);
        return new ModelAndView("subscription/boat");
    }

    @GetMapping("/cottage")
    public ModelAndView getAllCottage(Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        model.addAttribute("subscriptions", client.getCottageSubscriptions());
        model.addAttribute("principal", client);
        return new ModelAndView("subscription/cottage");
    }

    @GetMapping("/instructor")
    public ModelAndView getAllInstructor(Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        model.addAttribute("subscriptions", client.getInstructorSubscriptions());
        model.addAttribute("principal", client);
        return new ModelAndView("subscription/instructor");
    }

    @GetMapping("/cottage/new/{id}")
    public ModelAndView makeCottageSubscription(@PathVariable Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        Cottage cottage = this.cottageService.findById(id);

        client.getCottageSubscriptions().add(cottage);
        this.clientService.update(client);

        model.addAttribute("cottage", cottage);
        model.addAttribute("principal", client);

        return new ModelAndView("redirect:/cottages/" + id);
    }

    @GetMapping("/boat/new/{id}")
    public ModelAndView makeBoatSubscription(@PathVariable Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        Boat boat = this.boatService.findById(id);

        client.getBoatSubscriptions().add(boat);
        this.clientService.update(client);

        model.addAttribute("boat", boat);
        model.addAttribute("principal", client);

        return new ModelAndView("redirect:/boats/" + id);
    }

    @GetMapping("/instructor/new/{id}")
    public ModelAndView makeInstructorSubscription(@PathVariable Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        FishingInstructorAdventure instructor = this.instructorService.findById(id);

        client.getInstructorSubscriptions().add(instructor);
        this.clientService.update(client);

        model.addAttribute("instructor", instructor);
        model.addAttribute("principal", client);

        return new ModelAndView("redirect:/adventures/" + id);
    }

    @GetMapping("/cottage/unsubscribe/{id}")
    public ModelAndView removeCottageSubscription(@PathVariable Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        Cottage cottage = this.cottageService.findById(id);

        client.getCottageSubscriptions().remove(cottage);
        this.clientService.update(client);

        model.addAttribute("subscriptions", client.getCottageSubscriptions());
        model.addAttribute("principal", client);
        return new ModelAndView("redirect:/subscriptions/cottage");
    }

    @GetMapping("/boat/unsubscribe/{id}")
    public ModelAndView removeBoatSubscription(@PathVariable Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        Boat boat = this.boatService.findById(id);

        client.getBoatSubscriptions().remove(boat);
        this.clientService.update(client);

        model.addAttribute("subscriptions", client.getBoatSubscriptions());
        model.addAttribute("principal", client);
        return new ModelAndView("redirect:/subscriptions/boat");
    }

    @GetMapping("/instructor/unsubscribe/{id}")
    public ModelAndView removeInstructorSubscription(@PathVariable Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        FishingInstructorAdventure instructor = this.instructorService.findById(id);

        client.getInstructorSubscriptions().remove(instructor);
        this.clientService.update(client);

        model.addAttribute("subscriptions", client.getInstructorSubscriptions());
        model.addAttribute("principal", client);
        return new ModelAndView("redirect:/subscriptions/instructor");
    }

}
