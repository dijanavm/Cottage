package com.isa.cottages.Controller;

import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.FishingInstructorAdventureServiceImpl;
import com.isa.cottages.Service.impl.InstructorReservationsServiceImpl;
import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/instructorReservations")
public class InstructorReservationController {

    private final UserServiceImpl userService;
    private final InstructorReservationsServiceImpl reservationService;
    private final FishingInstructorAdventureServiceImpl instructorService;

    @Autowired
    public InstructorReservationController(UserServiceImpl userService,
                                           InstructorReservationsServiceImpl reservationService,
                                           FishingInstructorAdventureServiceImpl instructorService) {
        this.userService = userService;
        this.reservationService = reservationService;
        this.instructorService = instructorService;
    }


    @GetMapping("/chooseTime")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView chooseDate(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());
        model.addAttribute("numPersons", 1);
        model.addAttribute("res_type", "instructor");

        return new ModelAndView("reservation/chooseTime");
    }

    @PostMapping("/chooseTime")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView chooseTime(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                   @RequestParam("numPersons") Integer numPersons) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("redirect:/instructorReservations/available");
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailable(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                      @RequestParam("numPersons") Integer numPersons) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("adventures", this.instructorService.findAllAvailable(sd, ed, numPersons));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("instructor/available");
    }

    @GetMapping("/available/byPriceAsc")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailableSortedByPriceAsc(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                      @RequestParam("numPersons") Integer numPersons) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("adventures", this.instructorService.findAllAvailableSorted(sd, ed, numPersons, true, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("instructor/available");
    }

    @GetMapping("/available/byPriceDesc")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailableSortedByPriceDesc(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                       @RequestParam("numPersons") Integer numPersons) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("adventures", this.instructorService.findAllAvailableSorted(sd, ed, numPersons, false, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("instructor/available");
    }

    @GetMapping("/available/byRatingAsc")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailableSortedByRatingAsc(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                       @RequestParam("numPersons") Integer numPersons) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("adventures", this.instructorService.findAllAvailableSorted(sd, ed, numPersons, true, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("instructor/available");
    }

    @GetMapping("/available/byRatingDesc")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailableSortedByRatingDesc(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                        @RequestParam("numPersons") Integer numPersons) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("adventures", this.instructorService.findAllAvailableSorted(sd, ed, numPersons, false, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("instructor/available");
    }
    
    @GetMapping("/select/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView selectEntity(@PathVariable Long id, Model model, @RequestParam("startDate") String startDate,
                                     @RequestParam("endDate") String endDate, @RequestParam("numPersons") Integer numPersons) throws Exception {

        Client client = (Client) this.userService.getUserFromPrincipal();

        model.addAttribute("principal", client);
        model.addAttribute("services", this.instructorService.findById(id).getAdditionalServices());
        model.addAttribute("entity_id", id);
        model.addAttribute("startDateString", startDate);
        model.addAttribute("endDateString", endDate);
        model.addAttribute("numPersons", numPersons);

        InstructorReservation reservation = new InstructorReservation();
        model.addAttribute("reservation", reservation);
        model.addAttribute("res_type", "instructor");
        model.addAttribute("sLength", this.instructorService.findById(id).getAdditionalServices().size());

        return new ModelAndView("additionalServices");
    }

    @PostMapping("/done/{entity_id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView makeReservation(@PathVariable("entity_id") Long entity_id, Model model, @ModelAttribute("reservation") InstructorReservation reservation) throws Exception {

        FishingInstructorAdventure instructor = this.instructorService.findById(entity_id);
        InstructorReservation res = this.reservationService.makeReservation(reservation, instructor);

        model.addAttribute("reservation", res);
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("redirect:/instructorReservations/success");
    }

    @GetMapping("/success")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView reservationConfirmation(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("reservation/success");
    }

    @GetMapping("/onDiscount/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showOffersOnDiscount(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("reservations", this.reservationService.getAllWithDiscount(id));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("instructor/resOnDiscount");
    }

    @PostMapping("/onDiscount/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView selectOffersOnDiscount(@PathVariable("id") Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        InstructorReservation reservation = this.reservationService.makeReservationOnDiscount(id);

        model.addAttribute("principal", client);
        return new ModelAndView("redirect:/instructorReservations/success");
    }

    @RequestMapping("/cancel/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView cancelReservation(@PathVariable Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (this.reservationService.canCancel(id)) {
            this.reservationService.cancel(id);
            return new ModelAndView("redirect:/instructorReservations/upcoming");
        }

        return new ModelAndView("reservation/cancellationError");
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showReservationHistory(Model model, String keyword) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (keyword != null) {
            //model.addAttribute("reservations", );
        } else {
            model.addAttribute("reservations", this.reservationService.getPastReservations());
        }
        return new ModelAndView("instructor/reservationHistory");
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showUpcomingReservations(Model model, String keyword) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (keyword != null) {
            //model.addAttribute("reservations", );
        } else {
            model.addAttribute("reservations", this.reservationService.getUpcomingReservations());
        }
        return new ModelAndView("instructor/upcomingReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDateAsc")
    public ModelAndView sortPastReservationsByDateAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByStartTimeAsc());

        return new ModelAndView("instructor/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDateDesc")
    public ModelAndView sortPastReservationsByDateDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByStartTimeDesc());

        return new ModelAndView("instructor/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDurationAsc")
    public ModelAndView sortPastReservationsByDurationAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByDurationAsc());

        return new ModelAndView("instructor/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDurationDesc")
    public ModelAndView sortPastReservationsByDurationDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByDurationDesc());

        return new ModelAndView("instructor/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByPriceAsc")
    public ModelAndView sortPastReservationsByPriceAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByPriceAsc());

        return new ModelAndView("instructor/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByPriceDesc")
    public ModelAndView sortPastReservationsByPriceDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByPriceDesc());

        return new ModelAndView("instructor/reservationHistory");
    }
}
