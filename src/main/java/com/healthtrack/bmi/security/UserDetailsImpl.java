package com.healthtrack.bmi.security;

import com.healthtrack.bmi.entity.Doctor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Doctor doctor) {
        this.id = doctor.getId();
        this.email = doctor.getEmail();
        this.password = doctor.getPasswordHash();
        this.name = doctor.getName();
        // Since there is only one doctor in the system, we grant a default "ROLE_DOCTOR" authority
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Username is the email address
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
