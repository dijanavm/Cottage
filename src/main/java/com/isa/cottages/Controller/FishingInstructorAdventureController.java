package com.isa.cottages.Controller;

import com.isa.cottages.Model.AdditionalService;
import com.isa.cottages.Model.FishingInstructorAdventure;
import com.isa.cottages.Model.Instructor;
import com.isa.cottages.Service.impl.FishingInstructorAdventureServiceImpl;
import com.isa.cottages.Service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
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
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Controller
@AllArgsConstructor
@RequestMapping("/adventures")
public class FishingInstructorAdventureController {

    private FishingInstructorAdventureServiceImpl adventureService;
    private UserServiceImpl userService;

    @GetMapping("/allAdventures")
    public ModelAndView getAllAdventures(Model model, String keyword) {
        if (keyword != null) {
            model.addAttribute("instructors", this.adventureService.findByKeyword(keyword));
        } else {
            model.addAttribute("instructors", this.adventureService.findAll());
        }

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            System.out.println("\n\n\n error all adventures \n\n\n ");
            return new ModelAndView("home");
        }
    }

    @GetMapping("/{id}")
    public ModelAndView showAdventure(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("instructor", this.adventureService.findById(id));
        return new ModelAndView("instructor/instructor");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/allMyAdventures/{id}")
    public ModelAndView getAllMyAdventures(@PathVariable Long id, Model model, String keyword) throws Exception {
        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);
        if (instructor == null) {
            throw new Exception("Instructor does not exist.");
        }
        if (keyword != null) {
            model.addAttribute("adventures", this.adventureService.findByKeyword(keyword));
        } else {
            model.addAttribute("adventures", adventureService.findByInstructor(id));
        }
        return new ModelAndView("instructor/allMyAdventures");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/addAdventure/{id}")
    public ModelAndView addAdventureForm(@PathVariable Long id, Model model) throws Exception {
        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);
        FishingInstructorAdventure adventure = new FishingInstructorAdventure();
        model.addAttribute("adventure", adventure);

        Collection<FishingInstructorAdventure> adventures = this.adventureService.findByInstructor(id);
        model.addAttribute("adventures", adventures);

        return new ModelAndView("instructor/addAdventureForm");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/addAdventure/{id}/submit")
    public ModelAndView addAdventure(@PathVariable Long id, @ModelAttribute FishingInstructorAdventure adventure,
                                     @RequestParam("image") MultipartFile image,
                                     Model model) throws Exception {

        Path path = Paths.get("C:\\Users\\User\\Pictures\\Cottages");
        try {
            InputStream inputStream = image.getInputStream();
            Files.copy(inputStream, path.resolve(Objects.requireNonNull(image.getOriginalFilename())),
                    StandardCopyOption.REPLACE_EXISTING);
            adventure.setImageUrl(image.getOriginalFilename().toLowerCase());
            this.adventureService.saveAdventure(adventure);
            model.addAttribute("adventure", adventure);
            model.addAttribute("imageUrl", adventure.getImageUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }

        Collection<FishingInstructorAdventure> adventures = this.adventureService.findByInstructor(id);
        model.addAttribute("adventures", adventures);

        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);

        adventure.setInstructor((Instructor) this.userService.getUserFromPrincipal());

        this.adventureService.saveAdventure(adventure);
        return new ModelAndView("redirect:/adventures/allMyAdventures/{id}/");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/allMyAdventures/{id}/editAdventure/{aid}")
    public ModelAndView updateAdventure(Model model, @PathVariable("id") Long id,
                                        @PathVariable("aid") Long aid) throws Exception {
        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);

        FishingInstructorAdventure adventure = this.adventureService.findById(aid);
        model.addAttribute("adventure", adventure);

        Collection<FishingInstructorAdventure> adventures = this.adventureService.findByInstructor(id);
        model.addAttribute("adventures", adventures);
        return new ModelAndView("instructor/editAdventure");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/allMyAdventures/{id}/additionalServices/{aid}")
    public ModelAndView additionalServices(Model model, @PathVariable("id") Long id,
                                           @PathVariable("aid") Long aid) throws Exception {
        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);

        FishingInstructorAdventure adventure = this.adventureService.findById(aid);
        model.addAttribute("adventure", adventure);

        Collection<AdditionalService> services = this.adventureService.findServicesByAdventure(adventure);
        model.addAttribute("services", services);

        AdditionalService newService = new AdditionalService();
        model.addAttribute("newService", newService);

        return new ModelAndView("instructor/additionalServices");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/addAdventure/{id}/submit/{adventureId}")
    public ModelAndView addService(@PathVariable Long id, @PathVariable Long adventureId, @ModelAttribute AdditionalService service,
                                   Model model) throws Exception {

        service.setAdventure(this.adventureService.findById(adventureId));
        this.adventureService.saveService(service);


        Collection<FishingInstructorAdventure> adventures = this.adventureService.findByInstructor(id);
        model.addAttribute("adventures", adventures);

        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);
        return new ModelAndView("redirect:/adventures/allMyAdventures/{id}/");
    }


    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/{id}/edit/submit")
    public ModelAndView edit(@PathVariable Long id, Model model, FishingInstructorAdventure adventure) throws Exception {
        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", instructor);

        Collection<FishingInstructorAdventure> adventures = this.adventureService.findByInstructor(id);
        model.addAttribute("adventures", adventures);

        adventure.setInstructor((Instructor) this.userService.getUserFromPrincipal());

        boolean update = this.adventureService.canUpdateOrDelete(id);
        if (!update) {
            return new ModelAndView("instructor/errors/errorUpdateAdventure");
        } else {
            this.adventureService.updateAdventure(adventure);
        }

        return new ModelAndView("redirect:/adventures/{id}/");
    }


    @GetMapping("/allAdventures/sortByInstructorNameDesc")
    public ModelAndView sortByInstructorNameDesc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.sortByInstructorInfo(false);
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allAdventures/sortByInstructorNameAsc")
    public ModelAndView sortByInstructorNameAsc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.sortByInstructorInfo(true);
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allAdventures/sortByAdventureNameDesc")
    public ModelAndView sortByAdventureNameDesc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.findByOrderByAdventureNameDesc();
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allAdventures/sortByAdventureNameAsc")
    public ModelAndView sortByAdventureNameAsc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.findByOrderByAdventureNameAsc();
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }
    @GetMapping("/allAdventures/sortByRatingDesc")
    public ModelAndView sortByRatingDesc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.findByOrderByRatingDesc();
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allAdventures/sortByRatingAsc")
    public ModelAndView sortByRatingAsc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.findByOrderByRatingAsc();
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allAdventures/SortByAddressAsc")
    public ModelAndView orderByAddressAsc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.findByOrderByAddressAsc();
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }

    @GetMapping("/allAdventures/SortByAddressDesc")
    public ModelAndView orderByAddressDesc(Model model) {
        List<FishingInstructorAdventure> sorted = this.adventureService.findByOrderByAddressDesc();
        model.addAttribute("instructors", sorted);

        try {
            model.addAttribute("principal", this.userService.getUserFromPrincipal());
            return new ModelAndView("instructor/instructors");
        } catch (Exception e) {
            return new ModelAndView("home");
        }
    }
}
