package com.isa.cottages.authFasace;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFasace {

    Authentication getAuthentication();

    String getPrincipalEmail();
}
