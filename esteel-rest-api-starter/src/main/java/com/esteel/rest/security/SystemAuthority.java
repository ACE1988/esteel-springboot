package com.esteel.rest.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface SystemAuthority {
public static final SimpleGrantedAuthority AUTHORITY_ESTEEL = new SimpleGrantedAuthority("ESTEEL");
}
