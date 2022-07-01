package com.isa.cottages.Controller;

import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.AdditionalServiceServiceImpl;
import com.isa.cottages.Service.impl.CottageReservationServiceImpl;
import com.isa.cottages.Service.impl.CottageServiceImpl;
import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping(value = "/cottages")
public class CottageController {

    private CottageServiceImpl cottageService;
    private UserServiceImpl userService;
    private AdditionalServiceServiceImpl additionalServiceService;
    private CottageReservationServiceImpl reservationService;

    @Autowired
    public CottageController(CottageServiceImpl cottageService, UserServiceImpl userService,
                             AdditionalServiceServiceImpl additionalServiceService,
                             CottageReservationServiceImpl reservationService
    ) {
        this.cottageService = cottageService;
        this.userService = userService;
        this.additionalServiceService = additionalServiceService;
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/addCottage/{id}")
    public ModelAndView addCottageForm(@PathVariable Long id, Model model) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        Cottage cottage = new Cottage();
        model.addAttribute("cottage", cottage);

        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(id);
        model.addAttribute("cottages", cottages);

        return new ModelAndView("cottage/addCottageForm");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/addCottage/{id}/submit")
    public ModelAndView addCottage(@PathVariable Long id, @ModelAttribute Cottage cottage,
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
                cottage.setImageUrl(list);
                model.addAttribute("cottage", cottage);
                model.addAttribute("imageUrl", cottage.getImageUrl());
                model.addAttribute("list", list);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(id);
        model.addAttribute("cottages", cottages);
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        cottage.setCottageOwner((CottageOwner) this.userService.getUserFromPrincipal());

        this.cottageService.saveCottage(cottage);
        return new ModelAndView("redirect:/cottages/allMyCottages/{id}/");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/addAdditionalService")
    public ModelAndView addAdditionalService(Model model, @PathVariable Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        AdditionalService additionalService = new AdditionalService();
        Collection<AdditionalService> additionalServices = additionalServiceService.findByCottage(id);
        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("additionalService", additionalService);
        Cottage cottage = cottageService.findById(id);
        model.addAttribute("cottage", cottage);

        return new ModelAndView("cottage/addAdditionalService");

    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/{id}/addAdditionalService/submit")
    public ModelAndView addAdditionalServiceSubmit(Model model, @PathVariable Long id,
                                                   @ModelAttribute AdditionalService additionalService) throws Exception {
        Cottage cottage = cottageService.findById(id);
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        model.addAttribute("cottage", cottage);
        Collection<AdditionalService> additionalServices = additionalServiceService.findByCottage(id);
        model.addAttribute("additionalServices", additionalServices);
        model.addAttribute("additionalService", additionalService);
        additionalService.setCottage(cottage);
        additionalServiceService.save(additionalService);

        return new ModelAndView("redirect:/cottages/{id}/");
    }


    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/allMyCottages/{id}")
    public ModelAndView getAllMyCottages (@PathVariable Long id, Model model, String keyword) throws Exception{
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        if(cottageOwner == null) {
            throw new Exception("Cottage owner does not exist.");
        }
        if (keyword != null) {
            model.addAttribute("cottages", this.cottageService.findMyByKeyword(keyword, id));
        } else {
            model.addAttribute("cottages", cottageService.findByCottageOwner(id));
        }
        return new ModelAndView("cottage/allMyCottages");
    }

    @GetMapping("/{id}")
    public ModelAndView showCottage(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        Cottage cottage = this.cottageService.findById(id);
        model.addAttribute("cottage", this.cottageService.findById(id));

        return new ModelAndView("cottage/cottage");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/edit")
    public ModelAndView edit(Model model, @PathVariable Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        Cottage cottage = this.cottageService.findById(id);
        model.addAttribute("cottage", cottage);

        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(id);
        model.addAttribute("cottages", cottages);

        return new ModelAndView("cottage/editCottage");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/allMyCottages/{id}/editCottage/{cid}")
    public ModelAndView updateCottage(Model model, @PathVariable("id") Long id,
                                      @PathVariable("cid") Long cid) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        Cottage cottage = this.cottageService.findById(cid);
        model.addAttribute("cottage", cottage);

        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(id);
        model.addAttribute("cottages", cottages);
        return new ModelAndView("cottage/editCottage");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/{id}/edit/submit")
    public ModelAndView edit(@PathVariable Long id, Model model, Cottage cottage,
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
                cottage.setImageUrl(list);
                model.addAttribute("cottage", cottage);
                model.addAttribute("imageUrl", cottage.getImageUrl());
                model.addAttribute("list", list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(id);
        model.addAttribute("cottages", cottages);
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        cottage.setCottageOwner((CottageOwner) this.userService.getUserFromPrincipal());

        this.cottageService.updateCottage(cottage);

        boolean update = this.cottageService.canUpdateOrDelete(id);
        if (!update) {
            return new ModelAndView("cottage/errors/errorUpdateCottage");
        } else {
            this.cottageService.updateCottage(cottage);
        }

        return new ModelAndView("redirect:/cottages/{id}/");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping(value = "/allMyCottages/{id}/remove/{cid}")
    public ModelAndView removeCottage(@PathVariable Long cid,
                                      @PathVariable Long id,
                                      Model model) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        Cottage cottage = this.cottageService.findById(cid);

        boolean delete = this.cottageService.canUpdateOrDelete(cid);
        if (!delete) {
            return new ModelAndView("cottage/errors/errorDeleteCottage");
        } else {
            this.cottageService.removeCottage(cottage, id);
            cottage.setDeleted(true);
            this.cottageService.updateCottage(cottage);
        }
        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(cid);
        model.addAttribute("cottages", cottages);

        return new ModelAndView("redirect:/cottages/allMyCottages/{id}" );
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping(value = "/{cid}/removeAdditionalService/{asid}")
    public ModelAndView removeAdditionalService(@PathVariable Long asid,
                                                @PathVariable Long cid,
                                                Model model) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        Cottage cottage = cottageService.findById(cid);
        model.addAttribute("cottage", cottage);

        AdditionalService additionalService = this.additionalServiceService.findById(asid);
        model.addAttribute("asid", asid);

        boolean update = this.cottageService.canUpdateOrDelete(cid);
        if (!update) {
            return new ModelAndView("cottage/errors/errorUpdateCottage");
        } else {
            this.additionalServiceService.removeAdditionalServiceFromCottage(additionalService, cid);
            additionalService.setDeleted(true);
            this.cottageService.updateAdditionalServices(cottage);
            this.cottageService.updateCottage(cottage);
        }

        return new ModelAndView("redirect:/cottages/{cid}" );
    }


    @GetMapping("/allCottages")
    public ModelAndView getAllCottages(Model model, String keyword) throws Exception {
        if (keyword != null) {
            model.addAttribute("cottages", this.cottageService.findByKeyword(keyword));
        } else {
            model.addAttribute("cottages", this.cottageService.findAll());
        }

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            //System.out.println("error all cottages");
            return new ModelAndView("cottagesGuests");
        }
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/defineAvailability")
    public ModelAndView defineAvailability(Model model, @PathVariable Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        Cottage cottage = this.cottageService.findById(id);
        model.addAttribute("cottage", cottage);

        return new ModelAndView("cottage/defineAvailability");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/{id}/defineAvailability/submit")
    public ModelAndView defineAvailability(Model model, @PathVariable Long id, @ModelAttribute Cottage cottage) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        Collection<Cottage> cottages = this.cottageService.findByCottageOwner(id);
        model.addAttribute("cottages", cottages);
        model.addAttribute("cottage", cottage);

        cottage.setCottageOwner((CottageOwner) this.userService.getUserFromPrincipal());
        this.cottageService.defineAvailability(cottage);

        return new ModelAndView("redirect:/cottages/{id}/");
    }

    @GetMapping("/allCottages/sortByNameDesc")
    public ModelAndView sortByNameDesc(Model model) {
        List<Cottage> sorted = this.cottageService.orderByNameDesc();
        model.addAttribute("cottages", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            // return new ModelAndView("cottage/cottagesGuests");
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allCottages/sortByNameAsc")
    public ModelAndView sortByNameAsc(Model model) {
        List<Cottage> sorted = this.cottageService.orderByNameAsc();
        model.addAttribute("cottages", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            // return new ModelAndView("cottage/cottagesGuests");
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allCottages/sortByRatingDesc")
    public ModelAndView sortByRatingDesc(Model model) {
        List<Cottage> sorted = this.cottageService.orderByRatingDesc();
        model.addAttribute("cottages", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            // return new ModelAndView("cottage/cottagesGuests");
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allCottages/sortByRatingAsc")
    public ModelAndView sortByRatingAsc(Model model) {
        List<Cottage> sorted = this.cottageService.orderByRatingAsc();
        model.addAttribute("cottages", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            // return new ModelAndView("cottage/cottagesGuests");
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allCottages/SortByAddressAsc")
    public ModelAndView orderByAddressAsc(Model model) {
        List<Cottage> sorted = this.cottageService.orderByAddressAsc();
        model.addAttribute("cottages", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            // return new ModelAndView("cottage/cottagesGuests");
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allCottages/SortByAddressDesc")
    public ModelAndView orderByAddressDesc(Model model) {
        List<Cottage> sorted = this.cottageService.orderByAddressDesc();
        model.addAttribute("cottages", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("cottage/cottages");
        } catch (Exception e) {
            // return new ModelAndView("cottage/cottagesGuests");
            return new ModelAndView("home");
        }
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/averageRating")
    public ModelAndView reportOfAverageRating (Model model, @PathVariable Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        model.addAttribute("cottages", cottageService.findByCottageOwner(id));

        return new ModelAndView("cottage/reports/averageRating");
    }



    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{oid}/{rid}/incomes")
    public ModelAndView reportOfIncomes (Model model, @PathVariable Long oid,
                                         @PathVariable Long rid) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        model.addAttribute("cottages", cottageService.findByCottageOwner(oid));

        List<CottageReservation> cottageReservations = reservationService.getOwnersPastReservations(oid);
        CottageReservation cottageReservation = reservationService.findOne(rid);
        Double price = cottageReservation.getPrice();
        Integer numberOfReservations = cottageReservations.size();
        model.addAttribute("numberOfReservations", numberOfReservations);

        return new ModelAndView("cottage/reports/incomes");
    }
}
