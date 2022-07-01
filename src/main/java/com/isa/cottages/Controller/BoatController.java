package com.isa.cottages.Controller;

import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping(value = "/boats")
public class BoatController {

    private BoatServiceImpl boatService;
    private UserServiceImpl userService;
    private AdditionalServiceServiceImpl additionalServiceService;
    private NavigationEquipmentServiceImpl navigationEquipmentService;
    private FishingEquipmentServiceImpl fishingEquipmentService;

    @Autowired
    public BoatController(BoatServiceImpl boatService, UserServiceImpl userService,
                          AdditionalServiceServiceImpl additionalServiceService,
                          NavigationEquipmentServiceImpl navigationEquipmentService,
                          FishingEquipmentServiceImpl fishingEquipmentService) {
        this.boatService = boatService;
        this.userService = userService;
        this.additionalServiceService = additionalServiceService;
        this.navigationEquipmentService = navigationEquipmentService;
        this.fishingEquipmentService = fishingEquipmentService;
    }

    @GetMapping("/allBoats")
    public ModelAndView getAllBoats(Model model, String keyword) throws Exception {
        if (keyword != null) {
            model.addAttribute("boats", this.boatService.findByKeyword(keyword));
        } else {
            model.addAttribute("boats", this.boatService.getAll());
        }


        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            System.out.println("error all boats");
            return new ModelAndView("home");
        }
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/allMyBoats/{id}")
    public ModelAndView getAllMyBoats(@PathVariable Long id, Model model, String keyword) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        if (boatOwner == null) {
            throw new Exception("Boat owner does not exist.");
        }
        if (keyword != null) {
            model.addAttribute("boats", this.boatService.findMyByKeyword(keyword, id));
        } else {
            model.addAttribute("boats", boatService.findByBoatOwner(id));
        }
        return new ModelAndView("boat/allMyBoats");
    }

    @GetMapping("/{id}")
    public ModelAndView showBoat(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("boat", this.boatService.findById(id));
        return new ModelAndView("boat/boat");
    }

    @GetMapping("/allBoats/sortByNameDesc")
    public ModelAndView sortByNameDesc(Model model) {
        List<Boat> sorted = this.boatService.orderByNameDesc();
        model.addAttribute("boats", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allBoats/sortByNameAsc")
    public ModelAndView sortByNameAsc(Model model) {
        List<Boat> sorted = this.boatService.orderByNameAsc();
        model.addAttribute("boats", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allBoats/sortByRatingDesc")
    public ModelAndView sortByRatingDesc(Model model) {
        List<Boat> sorted = this.boatService.orderByRatingDesc();
        model.addAttribute("boats", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allBoats/sortByRatingAsc")
    public ModelAndView sortByRatingAsc(Model model) {
        List<Boat> sorted = this.boatService.orderByRatingAsc();
        model.addAttribute("boats", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allBoats/SortByAddressAsc")
    public ModelAndView orderByAddressAsc(Model model) {
        List<Boat> sorted = this.boatService.orderByAddressAsc();
        model.addAttribute("boats", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allBoats/SortByAddressDesc")
    public ModelAndView orderByAddressDesc(Model model) {
        List<Boat> sorted = this.boatService.orderByAddressDesc();
        model.addAttribute("boats", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("boat/boats");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/addBoat/{id}")
    public ModelAndView addBoatForm(@PathVariable Long id, Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        Boat boat = new Boat();
        model.addAttribute("boat", boat);

//        List<AdditionalService> additionalService = new ArrayList<>();
//        model.addAttribute("additionalService", additionalService);
//        for(int i=1; i<=3; i++) {
//            boat.addAdditionalService(new AdditionalService());
//        }

        Collection<Boat> boats = this.boatService.findByBoatOwner(id);
        model.addAttribute("boats", boats);

        return new ModelAndView("boat/addBoatForm");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/addBoat/{id}/submit")
    public ModelAndView addBoat(@PathVariable Long id, @ModelAttribute Boat boat,
                                   @RequestParam("image") MultipartFile[] image,
                                   Model model) throws Exception {
        List<String> list = new ArrayList<>();
        for (MultipartFile img:image) {
            Path path = Paths.get("C:\\Users\\Dijana\\Desktop\\Cottages\\cottages\\uploads");
            try {
                InputStream inputStream = img.getInputStream();
                Files.copy(inputStream, path.resolve(img.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                String i = img.getOriginalFilename().toLowerCase();
                list.add(i);
                boat.setImageUrl(list);
                model.addAttribute("boat", boat);
                model.addAttribute("imageUrl", boat.getImageUrl());
                model.addAttribute("list", list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collection<Boat> boats = this.boatService.findByBoatOwner(id);
        model.addAttribute("boats", boats);
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        boat.setBoatOwner((BoatOwner) this.userService.getUserFromPrincipal());
//        for(int i=1; i<=3; i++) {
//            cottage.setAdditionalServices(cottage.getAdditionalServices());
//            additionalService.setCottage(additionalService.getCottage());
//            additionalService.setCottage(cottage);
//            this.additionalServiceService.save(additionalService);
//        }

//        model.addAttribute("additionalService", additionalService);
//        model.addAttribute("additionalServices", cottage.getAdditionalServices());

        this.boatService.saveBoat(boat);
        return new ModelAndView("redirect:/boats/allMyBoats/{id}/");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/addAdditionalService")
    public ModelAndView addAdditionalService(Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        AdditionalService additionalService = new AdditionalService();
        Collection<AdditionalService> additionalServices = additionalServiceService.findByBoat(id);
        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("additionalService", additionalService);
        Boat boat = boatService.findById(id);
        model.addAttribute("boat", boat);

        return new ModelAndView("boat/addAdditionalService");

    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/addAdditionalService/submit")
    public ModelAndView addAdditionalServiceSubmit(Model model, @PathVariable Long id,
                                                   @ModelAttribute AdditionalService additionalService) throws Exception {
        Boat boat = boatService.findById(id);
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        model.addAttribute("boat", boat);
        Collection<AdditionalService> additionalServices = additionalServiceService.findByBoat(id);
        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("additionalService", additionalService);
        additionalService.setBoat(boat);
        additionalServiceService.save(additionalService);

        return new ModelAndView("redirect:/boats/{id}/");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/addNavigationEquipment")
    public ModelAndView addNavigationEquipment(Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        NavigationEquipment navigationEquipment = new NavigationEquipment();
        Collection<NavigationEquipment> navigationEquipments = navigationEquipmentService.findByBoat(id);
        model.addAttribute("navigationEquipments", navigationEquipments);
        model.addAttribute("navigationEquipment", navigationEquipment);
        Boat boat = boatService.findById(id);
        model.addAttribute("boat", boat);

        return new ModelAndView("boat/addNavigationEquipment");

    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/addNavigationEquipment/submit")
    public ModelAndView addNavigationEquipmentSubmit(Model model, @PathVariable Long id,
                                                   @ModelAttribute NavigationEquipment navigationEquipment) throws Exception {
        Boat boat = boatService.findById(id);
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        model.addAttribute("boat", boat);
        Collection<NavigationEquipment> navigationEquipments = navigationEquipmentService.findByBoat(id);
        model.addAttribute("navigationEquipment", navigationEquipments);
        model.addAttribute("navigationEquipment", navigationEquipment);
        navigationEquipment.setBoat(boat);
        navigationEquipmentService.save(navigationEquipment);

        return new ModelAndView("redirect:/boats/{id}/");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/addFishingEquipment")
    public ModelAndView addFishingEquipment(Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        FishingEquipment fishingEquipment = new FishingEquipment();
        Collection<FishingEquipment> fishingEquipments = fishingEquipmentService.findByBoat(id);
        model.addAttribute("fishingEquipments", fishingEquipments);
        model.addAttribute("fishingEquipment", fishingEquipment);
        Boat boat = boatService.findById(id);
        model.addAttribute("boat", boat);

        return new ModelAndView("boat/addFishingEquipment");

    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/addFishingEquipment/submit")
    public ModelAndView addFishingEquipmentSubmit(Model model, @PathVariable Long id,
                                                   @ModelAttribute FishingEquipment fishingEquipment) throws Exception {
        Boat boat = boatService.findById(id);
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        model.addAttribute("boat", boat);
        Collection<FishingEquipment> fishingEquipments = fishingEquipmentService.findByBoat(id);
        model.addAttribute("fishingEquipments", fishingEquipments);
        model.addAttribute("fishingEquipment", fishingEquipment);
        fishingEquipment.setBoat(boat);
        fishingEquipmentService.save(fishingEquipment);

        return new ModelAndView("redirect:/boats/{id}/");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/edit")
    public ModelAndView edit(Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = this.boatService.findById(id);
        model.addAttribute("boat", boat);

        Collection<Boat> boats = this.boatService.findByBoatOwner(id);
        model.addAttribute("boats", boats);

        return new ModelAndView("boat/editBoat");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/allMyBoats/{id}/editBoat/{bid}")
    public ModelAndView updateBoat(Model model, @PathVariable("id") Long id,
                                      @PathVariable("bid") Long bid) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = this.boatService.findById(bid);
        model.addAttribute("boat", boat);

        Collection<Boat> boats = this.boatService.findByBoatOwner(id);
        model.addAttribute("boats", boats);
        return new ModelAndView("boat/editBoat");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/edit/submit")
    public ModelAndView edit(@PathVariable Long id, Model model, Boat boat,
                             @RequestParam("image") MultipartFile[] image) throws Exception {
        List<String> list = new ArrayList<>();
        for (MultipartFile img:image) {
            Path path = Paths.get("C:\\Users\\Dijana\\Desktop\\Cottages\\cottages\\uploads");
            try {
                InputStream inputStream = img.getInputStream();
                Files.copy(inputStream, path.resolve(img.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                String i = img.getOriginalFilename().toLowerCase();
                list.add(i);
                boat.setImageUrl(list);
                model.addAttribute("boat", boat);
                model.addAttribute("imageUrl", boat.getImageUrl());
                model.addAttribute("list", list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collection<Boat> boats = this.boatService.findByBoatOwner(id);
        model.addAttribute("boats", boats);
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        boat.setBoatOwner((BoatOwner) this.userService.getUserFromPrincipal());
        this.boatService.updateBoat(boat);

        boolean update = this.boatService.canUpdateOrDelete(id);
        if (!update) {
            return new ModelAndView("boat/errors/errorUpdateBoat");
        } else {
            this.boatService.updateBoat(boat);
        }

        return new ModelAndView("redirect:/boats/{id}/");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping(value = "/allMyBoats/{id}/remove/{bid}")
    public ModelAndView removeBoat(@PathVariable Long bid,
                                      @PathVariable Long id,
                                      Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = this.boatService.findById(bid);

        boolean delete = this.boatService.canUpdateOrDelete(bid);
        if (!delete) {
            return new ModelAndView("boat/errors/errorDeleteBoat");
        } else {
            this.boatService.removeBoat(boat, id);
            boat.setDeleted(true);
            this.boatService.updateBoat(boat);
        }
        Collection<Boat> boats = this.boatService.findByBoatOwner(bid);
        model.addAttribute("boats", boats);

        return new ModelAndView("redirect:/boats/allMyBoats/{id}" );
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping(value = "/{bid}/removeAdditionalService/{asid}")
    public ModelAndView removeAdditionalService(@PathVariable Long asid,
                                   @PathVariable Long bid,
                                   Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = boatService.findById(bid);
        model.addAttribute("boat", boat);

        AdditionalService additionalService = this.additionalServiceService.findById(asid);
        model.addAttribute("asid", asid);

        boolean update = this.boatService.canUpdateOrDelete(bid);
        if (!update) {
            return new ModelAndView("boat/errors/errorUpdateBoat");
        } else {
            this.additionalServiceService.removeAdditionalServiceFromBoat(additionalService, bid);
            additionalService.setDeleted(true);
            this.boatService.updateAdditionalServices(boat);
            this.boatService.updateBoat(boat);
        }

        return new ModelAndView("redirect:/boats/{bid}" );
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping(value = "/{bid}/removeFishingEquipment/{feid}")
    public ModelAndView removeFishingEquipment(@PathVariable Long feid,
                                                @PathVariable Long bid,
                                                Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = boatService.findById(bid);
        model.addAttribute("boat", boat);

        FishingEquipment fishingEquipment = this.fishingEquipmentService.findById(feid);
        model.addAttribute("feid", feid);

        boolean update = this.boatService.canUpdateOrDelete(bid);
        if (!update) {
            return new ModelAndView("boat/errors/errorUpdateBoat");
        } else {
            this.fishingEquipmentService.removeFishingEquipment(fishingEquipment, bid);
            fishingEquipment.setDeleted(true);
            this.boatService.updateBoat(boat);
        }

        Collection<Boat> boats = this.boatService.findByBoatOwner(bid);
        model.addAttribute("boats", boats);

        return new ModelAndView("redirect:/boats/{bid}" );
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping(value = "/{bid}/removeNavigationEquipment/{neid}")
    public ModelAndView removeNavigationEquipment(@PathVariable Long neid,
                                               @PathVariable Long bid,
                                               Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = boatService.findById(bid);
        model.addAttribute("boat", boat);

        NavigationEquipment navigationEquipment = this.navigationEquipmentService.findById(neid);
        model.addAttribute("neid", neid);

        boolean update = this.boatService.canUpdateOrDelete(bid);
        if (!update) {
            return new ModelAndView("boat/errors/errorUpdateBoat");
        } else {
            this.navigationEquipmentService.removeNavigationEquipment(navigationEquipment, bid);
            navigationEquipment.setDeleted(true);
            this.boatService.updateBoat(boat);
        }

        Collection<Boat> boats = this.boatService.findByBoatOwner(bid);
        model.addAttribute("boats", boats);

        return new ModelAndView("redirect:/boats/{bid}" );
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/defineAvailability")
    public ModelAndView defineAvailability(Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = this.boatService.findById(id);
        model.addAttribute("boat", boat);

        return new ModelAndView("boat/defineAvailability");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/defineAvailability/submit")
    public ModelAndView defineAvailability(Model model, @PathVariable Long id, @ModelAttribute Boat boat) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Collection<Boat> boats = this.boatService.findByBoatOwner(id);
        model.addAttribute("boats", boats);
        model.addAttribute("boat", boat);

        boat.setBoatOwner((BoatOwner) this.userService.getUserFromPrincipal());
        this.boatService.defineAvailability(boat);

        return new ModelAndView("redirect:/boats/{id}/");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/averageRating")
    public ModelAndView reportOfAverageRating (Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        model.addAttribute("boats", boatService.findByBoatOwner(id));

        return new ModelAndView("boat/reports/averageRating");
    }
}
