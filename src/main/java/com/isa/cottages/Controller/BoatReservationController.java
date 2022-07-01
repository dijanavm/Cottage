package com.isa.cottages.Controller;

import com.isa.cottages.Email.EmailSender;
import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/boatReservations")
public class BoatReservationController {

    private UserServiceImpl userService;
    private BoatReservationServiceImpl reservationService;
    private BoatServiceImpl boatService;
    private ReportServiceImpl reportService;
    private EmailSender emailSender;
    private ClientServiceImpl clientService;

    @Autowired
    public BoatReservationController(UserServiceImpl userService, BoatReservationServiceImpl reservationService,
                                     BoatServiceImpl boatService, ReportServiceImpl reportService,
                                     EmailSender emailSender, ClientServiceImpl clientService) {
        this.userService = userService;
        this.reservationService = reservationService;
        this.boatService = boatService;
        this.reportService = reportService;
        this.emailSender = emailSender;
        this.clientService = clientService;
    }

    @GetMapping("/chooseTime")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView chooseDate(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());
        model.addAttribute("numPersons", 1);
        model.addAttribute("res_type", "boat");

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

        return new ModelAndView("redirect:/boatReservations/available");
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailable(Model model, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                      @RequestParam("numPersons") Integer numPersons) throws Exception {
        // TODO:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("boats", this.boatService.findAllAvailable(sd, ed, numPersons));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/available");
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

        model.addAttribute("boats", this.boatService.findAllAvailableSorted(sd, ed, numPersons, true, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/available");
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

        model.addAttribute("boats", this.boatService.findAllAvailableSorted(sd, ed, numPersons, false, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/available");
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

        model.addAttribute("boats", this.boatService.findAllAvailableSorted(sd, ed, numPersons, true, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/available");
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

        model.addAttribute("boats", this.boatService.findAllAvailableSorted(sd, ed, numPersons, false, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/available");
    }

    @GetMapping("/select/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView selectEntity(@PathVariable Long id, Model model, @RequestParam("startDate") String startDate,
                                     @RequestParam("endDate") String endDate, @RequestParam("numPersons") Integer numPersons) throws Exception {

        Client client = (Client) this.userService.getUserFromPrincipal();

        model.addAttribute("principal", client);
        model.addAttribute("services", this.boatService.findById(id).getAdditionalServices());
        model.addAttribute("entity_id", id);
        model.addAttribute("startDateString", startDate);
        model.addAttribute("endDateString", endDate);
        model.addAttribute("numPersons", numPersons);

        BoatReservation reservation = new BoatReservation();
        model.addAttribute("reservation", reservation);
        model.addAttribute("res_type", "boat");
        model.addAttribute("sLength", this.boatService.findById(id).getAdditionalServices().size());

        return new ModelAndView("additionalServices");
    }

    @PostMapping("/done/{boatId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView makeReservation(@PathVariable("boatId") Long boatId, Model model, @ModelAttribute("reservation") BoatReservation reservation) throws Exception {

        Boat boat = this.boatService.findById(boatId);
        BoatReservation res = this.reservationService.makeReservation(reservation, boat);

        model.addAttribute("reservation", res);
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("redirect:/boatReservations/success");
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
        return new ModelAndView("boat/resOnDiscount");
    }

    @PostMapping("/onDiscount/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView selectOffersOnDiscount(@PathVariable("id") Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        BoatReservation reservation = this.reservationService.makeReservationOnDiscount(id);

        model.addAttribute("principal", client);
        // return new ModelAndView("redirect:/boatReservations/onDiscount/make/" + id);
        return new ModelAndView("redirect:/boatReservations/success");
    }

    @GetMapping("/onDiscount/make/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView makeReservationOnDiscount(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("reservation/success");
    }

    @RequestMapping("/cancel/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView cancelReservation(@PathVariable Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (this.reservationService.canCancel(id)) {
            this.reservationService.cancel(id);
            return new ModelAndView("redirect:/boatReservations/upcoming");
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
        return new ModelAndView("boat/reservationHistory");
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
        return new ModelAndView("boat/upcomingReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDateAsc")
    public ModelAndView sortPastReservationsByDateAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByStartTimeAsc());

        return new ModelAndView("boat/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDateDesc")
    public ModelAndView sortPastReservationsByDateDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByStartTimeDesc());

        return new ModelAndView("boat/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDurationAsc")
    public ModelAndView sortPastReservationsByDurationAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByDurationAsc());

        return new ModelAndView("boat/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDurationDesc")
    public ModelAndView sortPastReservationsByDurationDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByDurationDesc());

        return new ModelAndView("boat/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByPriceAsc")
    public ModelAndView sortPastReservationsByPriceAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByPriceAsc());

        return new ModelAndView("boat/reservationHistory");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByPriceDesc")
    public ModelAndView sortPastReservationsByPriceDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("reservations", this.reservationService.findByOrderByPriceDesc());

        return new ModelAndView("boat/reservationHistory");
    }

    @GetMapping("/allDiscounts/{id}")
    public ModelAndView getDiscountsByBoat(@PathVariable Long id, Model model) throws Exception {
        User user = this.userService.getUserFromPrincipal();
        model.addAttribute("principal", user);

        Boat boat = this.boatService.findById(id);
        model.addAttribute("boat", boat);

        if (boat == null) {
            throw new Exception("Boat with this id does not exist.");
        }
        model.addAttribute("boatReservations", reservationService.findDiscountsByBoat(id));
        model.addAttribute("services", this.boatService.findById(id).getAdditionalServices());

        return new ModelAndView("boat/allDiscounts");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/defineDiscount")
    public ModelAndView defineDiscount(@PathVariable Long id, Model model) throws Exception {
        User user = this.userService.getUserFromPrincipal();
        model.addAttribute("principal", user);

        model.addAttribute("boat", this.boatService.findById(id));
        BoatReservation boatReservation = new BoatReservation();

        model.addAttribute("boatReservation", boatReservation);
        model.addAttribute("services", this.boatService.findById(id).getAdditionalServices());
        model.addAttribute("sLength", this.boatService.findById(id).getAdditionalServices().size());

        Collection<BoatReservation> boatReservations = this.reservationService.findDiscountsByBoat(id);
        model.addAttribute("boatReservations", boatReservations);

        return new ModelAndView("boat/defineDiscount");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/defineDiscount/submit")
    public ModelAndView defineDiscount(@PathVariable Long id,
                                       @ModelAttribute BoatReservation boatReservation,
                                       Model model) throws Exception {
        Collection<BoatReservation> boatReservations = this.reservationService.findDiscountsByBoat(id);
        model.addAttribute("boatReservations", boatReservations);

        model.addAttribute("services", this.boatService.findById(id).getAdditionalServices());
        model.addAttribute("sLength", this.boatService.findById(id).getAdditionalServices().size());

        User user = this.userService.getUserFromPrincipal();
        model.addAttribute("principal", user);

        boatReservation.setBoatOwner((BoatOwner) this.userService.getUserFromPrincipal());
        boatReservation.setBoat(this.boatService.findById(id));
        boatReservation.setDiscount(true);
        this.reservationService.saveDiscount(boatReservation);

        Boat boat = boatService.findById(id);
        Set<Client> clients = boat.getSubscribers();
        for (Client c : clients) {
            if (boat.getSubscribers() != null && c.getBoatSubscriptions() != null) {
                emailSender.send(c.getEmail(), email(c.getFirstName(), "New discount for cottage ", boat.getBoatName(), " published."));
            }
        }
        return new ModelAndView("redirect:/boatReservations/allDiscounts/{id}/");
    }

    public String email(String name, String text1, String boatName, String text2) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">New discount:</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:10px;line-height:25px;color:#0b0c0c\"> <p>" + text1 + boatName + text2 + "</p> </p></blockquote>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    @GetMapping("/upcomingOwnersReservations/{id}")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showUpcomingReservations(Model model, @PathVariable Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        model.addAttribute("boatReservations", this.reservationService.getOwnersUpcomingReservations(id));

        return new ModelAndView("boat/upcomingReservations");
    }

    @GetMapping("/pastOwnersReservations/{id}")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showReservationHistory(Model model, String keyword,
                                               @PathVariable("id") Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        model.addAttribute("id", id);

        if (keyword != null) {
            model.addAttribute("boatReservations", this.reservationService.findClientForHistory(keyword, id));
        } else {
            model.addAttribute("boatReservations", this.reservationService.getOwnersPastReservations(id));
        }
        return new ModelAndView("boat/reservationHistory");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/writeReport/{oid}/{id}/{aid}")
    public ModelAndView reportForm(Model model, @PathVariable Long id,
                                   @PathVariable Long aid, @PathVariable Long oid) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        model.addAttribute("boatReservations", this.reservationService.getOwnersPastReservations(oid));
        Report report = new Report();
        model.addAttribute("report", report);
        report.setClient((Client) this.userService.findById(id));
        report.setAdmin((SystemAdministrator) this.userService.findById(aid));
        model.addAttribute("id", id);
        model.addAttribute("aid", aid);
        model.addAttribute("oid", oid);

        return new ModelAndView("boat/report");
    }


    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/writeReport/{oid}/{id}/{aid}/submit")
    public ModelAndView reportFormSubmit(Model model, @ModelAttribute Report report,
                                         @PathVariable Long id, @PathVariable Long aid,
                                         @PathVariable Long oid,
                                         Client client) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        model.addAttribute("boatReservations", this.reservationService.getOwnersPastReservations(oid));

        model.addAttribute("report", report);
        List<Report> reports = reportService.findBoatOwnersReports(boatOwner.getId());
        model.addAttribute("reports", reports);
        SystemAdministrator admin = (SystemAdministrator) this.userService.findById(aid);
        model.addAttribute("id", id);
        model.addAttribute("aid", aid);
        model.addAttribute("oid", oid);

        report.setBoatOwner(boatOwner);
        report.setClient((Client) this.userService.findById(id));
        report.setAdmin(admin);
        client.setPenalties(report.getClient().getPenalties());
        int penalties = client.getPenalties();

        if (report.getPenal() == report.getPenal().TRUE) {
            admin.getReports().add(report);
            report.setApproved(true);
            penalties += 1;
            report.getClient().setPenalties(penalties);
            client.setPenalties(report.getClient().getPenalties());
        }
        if (report.getDidAppear() == report.getDidAppear().FALSE) {
            client.getPenalties();
            penalties += 1;
            report.getClient().setPenalties(penalties);
            client.setPenalties(report.getClient().getPenalties());
        }

        client.setPenalties(report.getClient().getPenalties());
        reportService.save(report);
        return new ModelAndView("redirect:/boatReservations/pastOwnersReservations/{oid}");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/viewCalendar/{id}")
    public ModelAndView viewCalendar(Model model, @PathVariable Long id, String keyword) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);
        if (keyword != null) {
            model.addAttribute("boatReservations", this.reservationService.findClientForCalendar(keyword, id));
        } else {
            model.addAttribute("boatReservations", this.reservationService.getAllOwnersNowAndUpcomingReservations(id));
        }
        return new ModelAndView("boat/calendar");
    }

    @GetMapping("/{id}/chooseDate")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView chooseDate(Model model, @PathVariable Long id) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());

        return new ModelAndView("boat/reports/chooseDate2");
    }

    @PostMapping("/{id}/chooseDate2")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView chooseDate2(Model model, @PathVariable Long id,
                                    @RequestParam("startDate") String startDate,
                                    @RequestParam("endDate") String endDate) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {

            LocalDate ld1 = LocalDate.parse(startDate, formatter);
            LocalDate ld2 = LocalDate.parse(endDate, formatter);

            if (ld2.isBefore(ld1)) {
                return new ModelAndView("reservation/dateError");
            }

            model.addAttribute("startDate", ld1);
            model.addAttribute("endDate", ld2);

            return new ModelAndView("redirect:/boatReservations/{id}/incomes");
        } catch (Exception e) {
            return new ModelAndView("reservation/dateError");
        }
    }

    @GetMapping("/{id}/incomes")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView incomes(Model model, @RequestParam("startDate") String startDate,
                                @RequestParam("endDate") String endDate, @PathVariable Long id) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate ld1 = LocalDate.parse(startDate, formatter);
        LocalDate ld2 = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", ld1);
        model.addAttribute("endDate", ld2);

        Set<BoatReservation> reservations = this.reservationService.findByInterval(ld1, ld2, id);
        model.addAttribute("reservations", reservations);

        Double income = 0.0;
        for (BoatReservation br : reservations) {
            income += br.getPrice();
        }
        model.addAttribute("income", income);

        return new ModelAndView("boat/reports/incomes");
    }

    @GetMapping("/{id}/chooseDate3")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView chooseDate3(Model model, @PathVariable Long id) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());

        return new ModelAndView("boat/reports/chooseDate3");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @PostMapping("/{id}/chooseDate4")
    public ModelAndView chooseDate4(Model model, @PathVariable Long id,
                                    @RequestParam("startDate") String startDate,
                                    @RequestParam("endDate") String endDate) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate ld1 = LocalDate.parse(startDate, formatter);
            LocalDate ld2 = LocalDate.parse(endDate, formatter);

            if (ld2.isBefore(ld1)) {
                return new ModelAndView("reservation/dateError");
            }

            model.addAttribute("startDate", ld1);
            model.addAttribute("endDate", ld2);
            return new ModelAndView("redirect:/boatReservations/{id}/attendance");
        } catch (Exception e) {
            return new ModelAndView("reservation/dateError");
        }
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{id}/attendance")
    public ModelAndView reportOfAttendance(Model model, @PathVariable Long id,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         LocalDate ld1 = LocalDate.parse(startDate, formatter);
         LocalDate ld2 = LocalDate.parse(endDate, formatter);

         if (ld2.isBefore(ld1)) {
             return new ModelAndView("reservation/dateError");
         }

         model.addAttribute("startDate", ld1);
         model.addAttribute("endDate", ld2);

         Set<BoatReservation> reservations = this.reservationService.findByInterval2(ld1, ld2, id);
         model.addAttribute("reservations", reservations);
         Integer attendance = reservations.size();

         model.addAttribute("attendance", attendance);
         return new ModelAndView("boat/reports/attendance");
    }

    @GetMapping("/{oid}/makeReservationWithClient")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showAvailableClients(Model model, @PathVariable Long oid) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        model.addAttribute("clients", this.clientService.findAllAvailable_Boat(oid));

        return new ModelAndView("boat/makeReservation/showAvailableClients");
    }


    @GetMapping("/{oid}/{clid}/selectClient")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView selectClient(@PathVariable Long oid,
                                     @PathVariable Long clid,
                                     Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client", client);

        return new ModelAndView("redirect:/boatReservations/{oid}/{clid}/next");
    }

    @PreAuthorize("hasRole('BOAT_OWNER')")
    @GetMapping("/{oid}/{clid}/next")
    public ModelAndView chooseDate(@PathVariable Long oid, @PathVariable Long clid,
                                   Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client", client);

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());
        model.addAttribute("numPersons", 1);

        return new ModelAndView("boat/makeReservation/chooseDate");
    }

    @PostMapping("/{oid}/{clid}/chooseDate")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView chooseDate(Model model, @PathVariable Long oid,
                                   @PathVariable Long clid,
                                   @RequestParam("startDate") String startDate,
                                   @RequestParam("endDate") String endDate,
                                   @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client", client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate sd = LocalDate.parse(startDate, formatter);
            LocalDate ed = LocalDate.parse(endDate, formatter);

            if (ed.isBefore(sd) || sd.isBefore(LocalDate.now()) || ed.isBefore(LocalDate.now())) {
                return new ModelAndView("reservation/dateError");
            }

            model.addAttribute("startDate", sd);
            model.addAttribute("endDate", ed);
            model.addAttribute("numPersons", numPersons);

            return new ModelAndView("redirect:/boatReservations/{oid}/{clid}/showAvailableBoats");

        } catch (Exception e) {
            return new ModelAndView("reservation/dateError");
        }
    }


    @GetMapping("/{oid}/{clid}/showAvailableBoats")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showAvailableBoats(Model model, @PathVariable Long oid,
                                           @PathVariable Long clid,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate,
                                           @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);
        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("boats", this.boatService.findAllMyAvailable(sd, ed, numPersons, oid));

        return new ModelAndView("boat/makeReservation/showAvailableBoats");
    }

    @GetMapping("/{oid}/{clid}/showAvailableBoats/byPriceAsc")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showAvailableSortedByPriceAsc(Model model, @PathVariable Long clid, @PathVariable Long oid,
                                                      @RequestParam("startDate") String startDate,
                                                      @RequestParam("endDate") String endDate,
                                                      @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("boats", this.boatService.findAllMyAvailableSorted(oid, sd, ed, numPersons, true, true, false));

        return new ModelAndView("boat/makeReservation/showAvailableBoats");
    }

    @GetMapping("/{oid}/{clid}/showAvailableBoats/byPriceDesc")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showAvailableSortedByPriceDesc(Model model, @PathVariable Long oid,
                                                       @PathVariable Long clid,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("boats", this.boatService.findAllMyAvailableSorted(oid, sd, ed, numPersons, false, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/makeReservation/showAvailableBoats");
    }

    @GetMapping("/{oid}/{clid}/showAvailableBoats/byRatingAsc")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showAvailableSortedByRatingAsc(Model model, @PathVariable long oid,
                                                       @PathVariable Long clid,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("boats", this.boatService.findAllMyAvailableSorted(oid, sd, ed, numPersons, true, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("boat/makeReservation/showAvailableBoats");
    }

    @GetMapping("/{oid}/{clid}/showAvailableBoats/byRatingDesc")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView showAvailableSortedByRatingDesc(Model model, @PathVariable Long oid,
                                                        @PathVariable Long clid,
                                                        @RequestParam("startDate") String startDate,
                                                        @RequestParam("endDate") String endDate,
                                                        @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("boats", this.boatService.findAllMyAvailableSorted(oid, sd, ed, numPersons, false, false, true));

        return new ModelAndView("boat/makeReservation/showAvailableBoats");
    }

    @GetMapping("/{oid}/selectBoat/{clid}/{id}")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView selectBoat(@PathVariable Long id, @PathVariable Long clid,
                                   @PathVariable Long oid,
                                   Model model, @RequestParam("startDate") String startDate,
                                   @RequestParam("endDate") String endDate,
                                   @RequestParam("numPersons") Integer numPersons) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("client", client);
        model.addAttribute("clid", clid);

        model.addAttribute("services", this.boatService.findById(id).getAdditionalServices());
        model.addAttribute("boat_id", id);
        model.addAttribute("startDateString", startDate);
        model.addAttribute("endDateString", endDate);
        model.addAttribute("numPersons", numPersons);

        BoatReservation reservation = new BoatReservation();
        model.addAttribute("reservation", reservation);
        model.addAttribute("sLength", this.boatService.findById(id).getAdditionalServices().size());

        return new ModelAndView("boat/makeReservation/showAdditionalServices");
    }

    @PostMapping("/{oid}/reserve/{boatId}/{clid}")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView makeReservation(@PathVariable("boatId") Long boatId,
                                        @PathVariable Long clid,
                                        @PathVariable Long oid,
                                        Model model,
                                        @ModelAttribute("reservation") BoatReservation reservation) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        Boat boat = this.boatService.findById(boatId);
        BoatReservation res = this.reservationService.makeReservationWithClient(reservation, boat, clid);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("client", client);

        model.addAttribute("reservation", res);
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        emailSender.send(client.getEmail(), emailSuccess(
                client.getFirstName(),"You successfully made boat reservation. ",
                "Boat: ", boat.getBoatName(),
                "Boat owner: ", reservation.getBoatOwner().getFullName(),
                "Reservation start: ", reservation.getStartDate(),
                "Reservation end: ", reservation.getEndDate(),
                "Number of persons: ", reservation.getNumPersons(),
                "Price: ", reservation.getPrice()
        ));

        return new ModelAndView("redirect:/boatReservations/end");
    }

    @GetMapping("/end")
    @PreAuthorize("hasRole('BOAT_OWNER')")
    public ModelAndView confirmReservation(Model model) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        model.addAttribute("principal", boatOwner);

        return new ModelAndView("boat/makeReservation/success");
    }

    public String emailSuccess(String name, String text1, String text2, String boatName, String text3, String boatOwnerName,
                               String text4, LocalDate startDate, String text5, LocalDate endDate,
                               String text6, Integer numPersons, String text7, Double price) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Reservation</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:10px;line-height:25px;color:#0b0c0c\"> <p>" + text1
                + "</br>" + text2 + boatName + ", "
                + "</br>" + text3 + boatOwnerName + ", "
                + "</br>" + text4 + startDate + ", "
                + "</br>" + text5 + endDate + ", "
                + "</br>" + text6 + numPersons + ", "
                + "</br>" + text7 + price
                + "</p> </p></blockquote>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
