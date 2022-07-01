package com.isa.cottages.Controller;

import com.isa.cottages.Email.EmailSender;
import com.isa.cottages.Model.*;
import com.isa.cottages.Service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/cottageReservations")
public class CottageReservationController {

    private CottageReservationServiceImpl reservationService;
    private UserServiceImpl userService;
    private CottageServiceImpl cottageService;
    private ReportServiceImpl reportService;
    private EmailSender emailSender;
    private ClientServiceImpl clientService;

    @Autowired
    public CottageReservationController(CottageReservationServiceImpl reservationService,
                                        UserServiceImpl userService,
                                        CottageServiceImpl cottageService,
                                        ReportServiceImpl reportService, EmailSender emailSender,
                                        ClientServiceImpl clientService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.cottageService = cottageService;
        this.reportService = reportService;
        this.emailSender = emailSender;
        this.clientService = clientService;
    }

    @GetMapping("/upcomingOwnersReservations/{id}")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView showUpcomingReservations(Model model, @PathVariable Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        model.addAttribute("cottageReservations", this.reservationService.getOwnersUpcomingReservations(id));

        return new ModelAndView("cottage/upcomingReservations");
    }

    @GetMapping("/pastOwnersReservations/{id}")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView showReservationHistory(Model model, String keyword, @PathVariable("id") Long id, String email) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
//        Client client = this.clientService.findByEmail(email);
//        model.addAttribute("client", client);

        if (keyword != null) {
            model.addAttribute("cottageReservations", this.reservationService.findClientForHistory(keyword, id));
        } else {
            model.addAttribute("cottageReservations", this.reservationService.getOwnersPastReservations(id));
        }
        return new ModelAndView("cottage/pastReservations");
    }

    @GetMapping("/allDiscounts/{id}")
    public ModelAndView getDiscountsByCottage(@PathVariable Long id, Model model) throws Exception {
        User user = this.userService.getUserFromPrincipal();
        model.addAttribute("principal", user);

        Cottage cottage = this.cottageService.findById(id);
        model.addAttribute("cottage", cottage);

        if(cottage == null) {
            throw new Exception("Cottage with this id does not exist.");
        }
        model.addAttribute("cottageReservations", reservationService.findDiscountsByCottage(id));
        model.addAttribute("services", this.cottageService.findById(id).getAdditionalServices());
        model.addAttribute("sLength", this.cottageService.findById(id).getAdditionalServices().size());

        return new ModelAndView("cottage/allDiscounts");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/defineDiscount")
    public ModelAndView defineDiscount(@PathVariable Long id, Model model) throws Exception {
        User user = this.userService.getUserFromPrincipal();
        model.addAttribute("principal", user);

        model.addAttribute("cottage", this.cottageService.findById(id));
        CottageReservation cottageReservation = new CottageReservation();

        model.addAttribute("cottageReservation", cottageReservation);
        model.addAttribute("services", this.cottageService.findById(id).getAdditionalServices());
        model.addAttribute("sLength", this.cottageService.findById(id).getAdditionalServices().size());

        Collection<CottageReservation> cottageReservations = this.reservationService.findDiscountsByCottage(id);
        model.addAttribute("cottageReservations", cottageReservations);

        return new ModelAndView("cottage/defineDiscount");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/{id}/defineDiscount/submit")
    public ModelAndView defineDiscount(@PathVariable Long id, @ModelAttribute CottageReservation cottageReservation,
                                       Model model) throws Exception {
        Collection<CottageReservation> cottageReservations = this.reservationService.findDiscountsByCottage(id);
        model.addAttribute("cottageReservations", cottageReservations);

        model.addAttribute("services", this.cottageService.findById(id).getAdditionalServices());
        model.addAttribute("sLength", this.cottageService.findById(id).getAdditionalServices().size());

        User user = this.userService.getUserFromPrincipal();
        model.addAttribute("principal", user);

        cottageReservation.setCottageOwner((CottageOwner) this.userService.getUserFromPrincipal());
        cottageReservation.setCottage(this.cottageService.findById(id));
        cottageReservation.setDiscount(true);
        this.reservationService.saveDiscount(cottageReservation);

        Cottage cottage = cottageService.findById(id);
        Set<Client> clients = cottage.getSubscribers();
        for (Client c:clients) {
            if (cottage.getSubscribers() != null && c.getBoatSubscriptions() != null) {
                emailSender.send(c.getEmail(), email(c.getFirstName(), "New discount for cottage ", cottage.getName(), " published."));
            }
        }
        return new ModelAndView("redirect:/cottageReservations/allDiscounts/{id}/");
    }

    public String email(String name, String text1, String cottageName, String text2) {
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
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:10px;line-height:25px;color:#0b0c0c\"> <p>" + text1 + cottageName + text2 +"</p> </p></blockquote>\n" +
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

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/writeReport/{oid}/{id}/{aid}")
    public ModelAndView reportForm(Model model, @PathVariable Long id,
                                   @PathVariable Long aid, @PathVariable Long oid) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        model.addAttribute("cottageReservations", this.reservationService.getOwnersPastReservations(oid));
        Report report = new Report();
        model.addAttribute("report", report);
        report.setClient((Client) this.userService.findById(id));
        report.setAdmin((SystemAdministrator) this.userService.findById(aid));
        model.addAttribute("id", id);
        model.addAttribute("aid", aid);
        model.addAttribute("oid", oid);

        return new ModelAndView("cottage/report");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @PostMapping("/writeReport/{oid}/{id}/{aid}/submit")
    public ModelAndView reportFormSubmit(Model model, @ModelAttribute Report report,
                                         @PathVariable Long id, @PathVariable Long aid,
                                         @PathVariable Long oid,
                                         Client client) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        model.addAttribute("cottageReservations", this.reservationService.getOwnersPastReservations(oid));

        model.addAttribute("report", report);
        List<Report> reports = reportService.findCottageOwnersReports(cottageOwner.getId());
        model.addAttribute("reports", reports);
        SystemAdministrator admin = (SystemAdministrator) this.userService.findById(aid);
        model.addAttribute("id", id);
        model.addAttribute("aid", aid);
        model.addAttribute("oid", oid);

        report.setCottageOwner(cottageOwner);
        report.setClient((Client) this.userService.findById(id));
        report.setAdmin(admin);
        client.setPenalties(report.getClient().getPenalties());
        int penalties = client.getPenalties();

        if(report.getPenal() == report.getPenal().TRUE) {
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
        return new ModelAndView("redirect:/cottageReservations/pastOwnersReservations/{oid}");
    }

    @GetMapping("/chooseTime")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView chooseDate(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());
        model.addAttribute("numPersons", 1);
        model.addAttribute("res_type", "cottage");

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

        return new ModelAndView("redirect:/cottageReservations/available");
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

        model.addAttribute("cottages", this.cottageService.findAllAvailable(sd, ed, numPersons));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("cottage/available");
    }

    @GetMapping("/select/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView selectEntity(@PathVariable Long id, Model model, @RequestParam("startDate") String startDate,
                                     @RequestParam("endDate") String endDate, @RequestParam("numPersons") Integer numPersons) throws Exception {

        Client client = (Client) this.userService.getUserFromPrincipal();

        model.addAttribute("principal", client);
        model.addAttribute("services", this.cottageService.findById(id).getAdditionalServices());
        model.addAttribute("entity_id", id);
        model.addAttribute("startDateString", startDate);
        model.addAttribute("endDateString", endDate);
        model.addAttribute("numPersons", numPersons);

        CottageReservation reservation = new CottageReservation();
        model.addAttribute("reservation", reservation);
        model.addAttribute("res_type", "cottage");
        model.addAttribute("sLength", this.cottageService.findById(id).getAdditionalServices().size());

        return new ModelAndView("additionalServices");
    }

    @PostMapping("/done/{cottageId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView makeReservation(@PathVariable("cottageId") Long cottageId, Model model, @ModelAttribute("reservation") CottageReservation reservation) throws Exception {

        Cottage cottage = this.cottageService.findById(cottageId);
        CottageReservation res = this.reservationService.makeReservation(reservation, cottage);

        model.addAttribute("reservation", res);
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("redirect:/cottageReservations/success");
    }

    @GetMapping("/success")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView reservationConfirmation(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("reservation/success");
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

        model.addAttribute("cottages", this.cottageService.findAllAvailableSorted(sd, ed, numPersons, true, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/available");
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

        model.addAttribute("cottages", this.cottageService.findAllAvailableSorted(sd, ed, numPersons, false, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/available");
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

        model.addAttribute("cottages", this.cottageService.findAllAvailableSorted(sd, ed, numPersons, true, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/available");
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

        model.addAttribute("cottages", this.cottageService.findAllAvailableSorted(sd, ed, numPersons, false, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/available");
    }

    @GetMapping("/onDiscount/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showOffersOnDiscount(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("reservations", this.reservationService.getAllWithDiscount(id));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        return new ModelAndView("cottage/resOnDiscount");
    }

    @PostMapping("/onDiscount/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView selectOffersOnDiscount(@PathVariable("id") Long id, Model model) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        CottageReservation reservation = this.reservationService.makeReservationOnDiscount(id);

        model.addAttribute("principal", client);
        return new ModelAndView("redirect:/cottageReservations/success");
    }

    @RequestMapping("/cancel/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView cancelReservation(@PathVariable Long id, Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (this.reservationService.canCancel(id)) {
            this.reservationService.cancel(id);
            return new ModelAndView("redirect:/cottageReservations/upcoming");
        }

        return new ModelAndView("reservation/cancellationError");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/viewCalendar/{id}")
    public ModelAndView viewCalendar (Model model, @PathVariable Long id, String keyword) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);
        if (keyword != null) {
            model.addAttribute("cottageReservations", this.reservationService.findClientForCalendar(keyword, id));
        } else {
            model.addAttribute("cottageReservations", this.reservationService.getAllOwnersNowAndUpcomingReservations(id));
        }
        return new ModelAndView("cottage/calendar");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/makeReservationWithClient")
    public ModelAndView makeReservationWithClient(Model model) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        model.addAttribute("principal", cottageOwner);

        return new ModelAndView("cottage/makeReservationWithClient");
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showReservationHistory(Model model, String keyword) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (keyword != null) {
            // TODO: Dodaj pretragu
            //model.addAttribute("reservations", this.reservationService.findByKeyword(keyword));
        } else {
            model.addAttribute("cottageReservations", this.reservationService.getPastReservations());
        }
        // TODO: dodaj stranicu
        return new ModelAndView("cottage/pastReservations");
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showUpcomingReservations(Model model, String keyword) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        if (keyword != null) {
            // TODO: Dodaj pretragu
            //model.addAttribute("reservations", this.reservationService.findByKeyword(keyword));
        } else {
            model.addAttribute("cottageReservations", this.reservationService.getUpcomingReservations());
        }
        // TODO: Dopuni stranicu
        return new ModelAndView("cottage/upcomingReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDateAsc")
    public ModelAndView sortPastReservationsByDateAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("cottageReservations", this.reservationService.findByOrderByStartTimeAsc());

        return new ModelAndView("cottage/pastReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDateDesc")
    public ModelAndView sortPastReservationsByDateDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("cottageReservations", this.reservationService.findByOrderByStartTimeDesc());

        return new ModelAndView("cottage/pastReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDurationAsc")
    public ModelAndView sortPastReservationsByDurationAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("cottageReservations", this.reservationService.findByOrderByDurationAsc());

        return new ModelAndView("cottage/pastReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByDurationDesc")
    public ModelAndView sortPastReservationsByDurationDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("cottageReservations", this.reservationService.findByOrderByDurationDesc());

        return new ModelAndView("cottage/pastReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByPriceAsc")
    public ModelAndView sortPastReservationsByPriceAsc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("cottageReservations", this.reservationService.findByOrderByPriceAsc());

        return new ModelAndView("cottage/pastReservations");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/history/sortByPriceDesc")
    public ModelAndView sortPastReservationsByPriceDesc(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());
        model.addAttribute("cottageReservations", this.reservationService.findByOrderByPriceDesc());

        return new ModelAndView("cottage/pastReservations");
    }

    @GetMapping("/{id}/chooseDate")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView chooseDate(Model model, @PathVariable Long id) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());

        return new ModelAndView("cottage/reports/chooseDate2");
    }

    @PostMapping("/{id}/chooseDate2")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
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

            return new ModelAndView("redirect:/cottageReservations/{id}/incomes");
        } catch (Exception e) {
            return new ModelAndView("reservation/dateError");
        }
    }

    @GetMapping("/{id}/incomes")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView incomes (Model model, @RequestParam("startDate") String startDate,
                                 @RequestParam("endDate") String endDate, @PathVariable Long id) throws Exception{
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld1 = LocalDate.parse(startDate, formatter);
        LocalDate ld2 = LocalDate.parse(endDate, formatter);
        model.addAttribute("startDate", ld1);
        model.addAttribute("endDate", ld2);

        Set<CottageReservation> reservations = this.reservationService.findByInterval(ld1, ld2, id);
        model.addAttribute("reservations", reservations);

        Double income = 0.0;
        for(CottageReservation cr: reservations) {
            income += cr.getPrice();
        }
        model.addAttribute("income", income);

        return new ModelAndView("cottage/reports/incomes");
    }

    @GetMapping("/{id}/chooseDate3")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView chooseDate3(Model model, @PathVariable Long id) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());

        return new ModelAndView("cottage/reports/chooseDate3");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
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

            return new ModelAndView("redirect:/cottageReservations/{id}/attendance");
        } catch (Exception e) {
            return new ModelAndView("reservation/dateError");
        }
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{id}/attendance")
    public ModelAndView reportOfAttendance(Model model, @PathVariable Long id,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld1 = LocalDate.parse(startDate, formatter);
        LocalDate ld2 = LocalDate.parse(endDate, formatter);
        model.addAttribute("startDate", ld1);
        model.addAttribute("endDate", ld2);

        Set<CottageReservation> reservations = this.reservationService.findByInterval2(ld1, ld2, id);
        model.addAttribute("reservations", reservations);
        Integer attendance = reservations.size();

        model.addAttribute("attendance", attendance);


        return new ModelAndView("cottage/reports/attendance");
    }

    @GetMapping("/{oid}/makeReservationWithClient")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView showAvailableClients(Model model, @PathVariable Long oid) throws Exception {

        model.addAttribute("clients", this.clientService.findAllAvailable_Cottage(oid));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/makeReservation/showAvailableClients");
    }


    @GetMapping("/{oid}/{clid}/selectClient")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView selectClient(@PathVariable Long oid,
                                     @PathVariable Long clid,
                                     Model model) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        return new ModelAndView("redirect:/cottageReservations/{oid}/{clid}/next");
    }

    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    @GetMapping("/{oid}/{clid}/next")
    public ModelAndView chooseDate(@PathVariable Long oid, @PathVariable Long clid,
                                   Model model) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        model.addAttribute("startDate", LocalDate.now());
        model.addAttribute("endDate", LocalDate.now());
        model.addAttribute("numPersons", 1);

        return new ModelAndView("cottage/makeReservation/chooseDate");
    }

    @PostMapping("/{oid}/{clid}/chooseDate")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView chooseDate(Model model, @PathVariable Long oid,
                                   @PathVariable Long clid,
                                   @RequestParam("startDate") String startDate,
                                   @RequestParam("endDate") String endDate,
                                   @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

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

        return new ModelAndView("redirect:/cottageReservations/{oid}/{clid}/showAvailableCottages");
    } catch (Exception e) {
        return new ModelAndView("reservation/dateError");
        }
    }

    @GetMapping("/{oid}/{clid}/showAvailableCottages")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView showAvailableCottages(Model model, @PathVariable Long oid,
                                           @PathVariable Long clid,
                                           @RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate,
                                           @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);
        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("cottages", this.cottageService.findAllMyAvailable(sd, ed, numPersons, oid));

        return new ModelAndView("cottage/makeReservation/showAvailableCottages");
    }

    @GetMapping("/{oid}/{clid}/showAvailableCottages/byPriceAsc")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView showAvailableSortedByPriceAsc(Model model, @PathVariable Long clid, @PathVariable Long oid,
                                                      @RequestParam("startDate") String startDate,
                                                      @RequestParam("endDate") String endDate,
                                                      @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());
        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("cottages", this.cottageService.findAllMyAvailableSorted(oid, sd, ed, numPersons, true, true, false));

        return new ModelAndView("cottage/makeReservation/showAvailableCottages");
    }

    @GetMapping("/{oid}/{clid}/showAvailableCottages/byPriceDesc")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailableSortedByPriceDesc(Model model, @PathVariable Long oid,
                                                       @PathVariable Long clid,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());
        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("cottages", this.cottageService.findAllMyAvailableSorted(oid, sd, ed, numPersons, false, true, false));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/makeReservation/showAvailableCottages");
    }

    @GetMapping("/{oid}/{clid}/showAvailableCottages/byRatingAsc")
    @PreAuthorize("hasRole('CLIENT')")
    public ModelAndView showAvailableSortedByRatingAsc(Model model, @PathVariable long oid,
                                                       @PathVariable Long clid,
                                                       @RequestParam("startDate") String startDate,
                                                       @RequestParam("endDate") String endDate,
                                                       @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());
        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("cottages", this.cottageService.findAllMyAvailableSorted(oid, sd, ed, numPersons, true, false, true));
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/makeReservation/showAvailableCottages");
    }

    @GetMapping("/{oid}/{clid}/showAvailableCottages/byRatingDesc")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView showAvailableSortedByRatingDesc(Model model, @PathVariable Long oid,
                                                        @PathVariable Long clid,
                                                        @RequestParam("startDate") String startDate,
                                                        @RequestParam("endDate") String endDate,
                                                        @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", userService.getUserFromPrincipal());
        Client client = (Client) userService.findById(clid);
        model.addAttribute("clid", clid);
        model.addAttribute("client",client);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(startDate, formatter);
        LocalDate ed = LocalDate.parse(endDate, formatter);

        model.addAttribute("startDate", sd);
        model.addAttribute("endDate", ed);
        model.addAttribute("numPersons", numPersons);

        model.addAttribute("cottages", this.cottageService.findAllMyAvailableSorted(oid, sd, ed, numPersons, false, false, true));

        return new ModelAndView("cottage/makeReservation/showAvailableCottages");
    }

    @GetMapping("/{oid}/selectCottage/{clid}/{id}")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView selectCottage(@PathVariable Long id, @PathVariable Long clid,
                                   @PathVariable Long oid,
                                   Model model, @RequestParam("startDate") String startDate,
                                   @RequestParam("endDate") String endDate,
                                   @RequestParam("numPersons") Integer numPersons) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        Client client = (Client) userService.findById(clid);
        model.addAttribute("client", client);
        model.addAttribute("clid", clid);

        model.addAttribute("services", this.cottageService.findById(id).getAdditionalServices());
        model.addAttribute("cottage_id", id);
        model.addAttribute("startDateString", startDate);
        model.addAttribute("endDateString", endDate);
        model.addAttribute("numPersons", numPersons);

        CottageReservation reservation = new CottageReservation();
        model.addAttribute("reservation", reservation);
        model.addAttribute("sLength", this.cottageService.findById(id).getAdditionalServices().size());

        return new ModelAndView("cottage/makeReservation/showAdditionalServices");
    }

    @PostMapping("/{oid}/reserve/{cottageId}/{clid}")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView makeReservation(@PathVariable("cottageId") Long cottageId,
                                        @PathVariable Long clid,
                                        @PathVariable Long oid,
                                        Model model,
                                        @ModelAttribute("reservation") CottageReservation reservation) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        Cottage cottage = this.cottageService.findById(cottageId);
        CottageReservation res = this.reservationService.makeReservationWithClient(reservation, cottage, clid);

        Client client = (Client) userService.findById(clid);
        model.addAttribute("client", client);

        model.addAttribute("reservation", res);
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        emailSender.send(client.getEmail(), emailSuccess(
                client.getFirstName(),"You successfully made cottage reservation. ",
                "Cottage: ", cottage.getName(),
                "Cottage owner: ", reservation.getCottageOwner().getFullName(),
                "Reservation start: ", reservation.getStartDate(),
                "Reservation end: ", reservation.getEndDate(),
                "Number of persons: ", reservation.getNumPersons(),
                "Price: ", reservation.getPrice()
        ));

        return new ModelAndView("redirect:/cottageReservations/end");
    }

    @GetMapping("/end")
    @PreAuthorize("hasRole('COTTAGE_OWNER')")
    public ModelAndView confirmReservation(Model model) throws Exception {
        model.addAttribute("principal", this.userService.getUserFromPrincipal());

        return new ModelAndView("cottage/makeReservation/success");
    }

    public String emailSuccess(String name, String text1, String text2, String cottageName, String text3,
                               String cottageOwnerName,
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
                + "</br>" + text2 + cottageName + ", "
                + "</br>" + text3 + cottageOwnerName + ", "
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
