package com.isa.cottages.Service.impl;

import com.isa.cottages.DTO.ChangePasswordAfterFirstLoginDTO;
import com.isa.cottages.DTO.ChangePasswordDTO;
import com.isa.cottages.Email.EmailSender;
import com.isa.cottages.Model.*;
import com.isa.cottages.Repository.UserRepository;
import com.isa.cottages.Service.ConfirmationTokenService;
import com.isa.cottages.Service.UserService;
import com.isa.cottages.authFasace.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationTokenService tokenService;
    private final EmailSender emailSender;
    private final AuthenticationFacade facade;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ConfirmationTokenService tokenService, EmailSender emailSender, AuthenticationFacade facade) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailSender = emailSender;
        this.facade = facade;
    }

    @Override
    public User changePasswordAfterFirstLogin(User user, ChangePasswordAfterFirstLoginDTO c) {
        user.setPassword(c.getNewPassword());
        this.userRepository.save(user);
        return user;
    }

    public User changePassword(User user, ChangePasswordDTO c) {
        user.setPassword(c.getNewPassword());
        this.userRepository.save(user);
        return user;
    }

    @Override
    public User findById(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Client saveClient(UserRequest userRequest) {
        // TODO: test loyalty program
        LoyaltyProgram loyaltyProgram = new LoyaltyProgram();

        Client cl = new Client();
        cl.setEmail(userRequest.getEmail());
        cl.setPassword(userRequest.getPassword());
        cl.setFirstName(userRequest.getFirstName());
        cl.setLastName(userRequest.getLastName());
        cl.setCity(userRequest.getCity());
        cl.setResidence(userRequest.getResidence());
        cl.setState(userRequest.getState());
        cl.setPhoneNumber(userRequest.getPhoneNumber());
        cl.setEnabled(false);
        cl.setLoyaltyProgram(loyaltyProgram);

        cl.setUserRole(UserRole.CLIENT);
        cl = this.userRepository.save(cl);
/*
        loyaltyProgram.setClient(cl);
        loyaltyProgram = this.loyaltyProgramService.save(loyaltyProgram);
*/
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                cl
        );
        tokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8081/auth/confirm?token=" + token;
        emailSender.send(userRequest.getEmail(),buildEmail(userRequest.getFirstName(),link));
        return cl;
    }

    @Override
    public User saveCottageOwner(UserRequest userRequest){

        CottageOwner co = new CottageOwner();
        co.setEnabled(true);
        co.setFirstName(userRequest.getFirstName());
        co.setLastName(userRequest.getLastName());
        co.setEmail(userRequest.getEmail());
        co.setPassword(userRequest.getPassword());
        co.setResidence(userRequest.getResidence());
        co.setCity(userRequest.getCity());
        co.setState(userRequest.getState());
        co.setPhoneNumber(userRequest.getPhoneNumber());
        co.setExplanationOfRegistration(userRequest.getExplanationOfRegistration());
        co.setRegistrationType(RegistrationType.COTTAGE_ADVERTISER);
        co.setUserRole(UserRole.COTTAGE_OWNER);

        this.userRepository.save(co);
        return co;
    }

    @Override
    public User saveInstructor(UserRequest userRequest) {

        Instructor i = new Instructor();
        i.setEnabled(true);
        i.setFirstName(userRequest.getFirstName());
        i.setLastName(userRequest.getLastName());
        i.setEmail(userRequest.getEmail());
        i.setPassword(userRequest.getPassword());
        i.setResidence(userRequest.getResidence());
        i.setCity(userRequest.getCity());
        i.setState(userRequest.getState());
        i.setPhoneNumber(userRequest.getPhoneNumber());

        i.setUserRole(UserRole.INSTRUCTOR);
        this.userRepository.save(i);

        return i;
    }

    @Override
    public User saveBoatOwner(UserRequest userRequest) {

        BoatOwner bo = new BoatOwner();
        bo.setEnabled(true);
        bo.setEmail(userRequest.getEmail());
        bo.setPassword(userRequest.getPassword());
        bo.setFirstName(userRequest.getFirstName());
        bo.setLastName(userRequest.getLastName());
        bo.setResidence(userRequest.getResidence());
        bo.setCity(userRequest.getCity());
        bo.setState(userRequest.getState());
        bo.setPhoneNumber(userRequest.getPhoneNumber());
        bo.setExplanationOfRegistration(userRequest.getExplanationOfRegistration());
        bo.setRegistrationType(RegistrationType.BOAT_ADVERTISER);
        bo.setUserRole(UserRole.BOAT_OWNER);

        this.userRepository.save(bo);
        return bo;
    }

    @Override
    public SystemAdministrator saveSystemAdmin(SystemAdministrator systemAdministrator){
        systemAdministrator.setEnabled(true);
        systemAdministrator.setPassword(systemAdministrator.getPassword());
        systemAdministrator.setUserRole(UserRole.SYS_ADMIN);

        this.userRepository.save(systemAdministrator);
        return systemAdministrator;
    }

    public String buildEmail(String name, String link) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
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
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
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

    @Override
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = tokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        tokenService.setConfirmedAt(token);
        userRepository.enableUser(
                confirmationToken.getUser().getEmail());
        return "Registration confirmed";
    }

    @Override
    public User getUserFromPrincipal() throws Exception {
        String principal = this.facade.getPrincipalEmail();
        return this.findByEmail(principal);
    }

    @Override
    public User findByEmailAndPassword(String email, String password){
        User user = this.userRepository.findByEmailAndPassword(email,password);

        return user;
    }
}
