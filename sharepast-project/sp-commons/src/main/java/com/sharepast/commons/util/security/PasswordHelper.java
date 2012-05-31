package com.sharepast.commons.util.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PasswordHelper {

    private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();
    private SaltSource saltSource;

    public PasswordHelper(PasswordEncoder passwordEncoder, SaltSource saltSource) {
        this.passwordEncoder = passwordEncoder;
        this.saltSource = saltSource;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }

    public String encode(String toEncode) {
        if (SecurityContextHolder.getContext().getAuthentication()==null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("No Authentication object found in context " +
                    "for current user.");
        }
        UserDetails user = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object salt = this.saltSource.getSalt(user);
        return this.passwordEncoder.encodePassword(toEncode, salt);
    }

    public String encode(String toEncode, UserDetails user) {
        Object salt = this.saltSource.getSalt(user);
        return this.passwordEncoder.encodePassword(toEncode, salt);
    }
}
