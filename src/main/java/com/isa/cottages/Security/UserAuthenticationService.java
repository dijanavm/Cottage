package com.isa.cottages.Security;

import com.isa.cottages.Model.User;
import com.isa.cottages.Service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import static com.isa.cottages.Model.UserRole.*;


import java.util.ArrayList;
import java.util.List;

@Service
public class UserAuthenticationService implements AuthenticationProvider {

    private UserServiceImpl userService;

    @Autowired
    public UserAuthenticationService(UserServiceImpl userService) {
        this.userService = userService;
    }


    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        System.out.println("test");
        Authentication retVal = null;
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        User user = this.userService.findByEmailAndPassword(auth.getName(),auth.getCredentials().toString());

        if (auth != null && user != null) {
            String email = auth.getName();
            String password = auth.getCredentials().toString();
            System.out.println("email: " + email);
            System.out.println("password: " + password);

                if (user.getUserRole() == SYS_ADMIN) {
                    grantedAuths.add(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"));

                    retVal = new UsernamePasswordAuthenticationToken(
                            email, password, grantedAuths
                    );
                }
                else if (user.getUserRole() == COTTAGE_OWNER) {
                    grantedAuths.add(new SimpleGrantedAuthority("ROLE_COTTAGE_OWNER"));

                    retVal = new UsernamePasswordAuthenticationToken(
                            email, password, grantedAuths
                    );
                } else if (user.getUserRole() == BOAT_OWNER) {
                    grantedAuths.add(new SimpleGrantedAuthority("ROLE_BOAT_OWNER"));

                    retVal = new UsernamePasswordAuthenticationToken(
                            email, password, grantedAuths
                    );
                } else if (user.getUserRole() == CLIENT) {
                    grantedAuths.add(new SimpleGrantedAuthority("ROLE_CLIENT"));

                    retVal = new UsernamePasswordAuthenticationToken(
                            email, password, grantedAuths
                    );
                }
            } else {
        System.out.println("invalid login");
        retVal = new UsernamePasswordAuthenticationToken(
                null, null, grantedAuths
        );
        System.out.println("bad Login");

    }
    System.out.println("return login info");
    return retVal;
}

    @Override
    public boolean supports(Class<?> tokenType) {

        return tokenType.equals(UsernamePasswordAuthenticationToken.class);
    }
}
