package com.example.ecommerce.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final boolean active;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id=user.getId();
        this.email       = user.getEmail();
        this.password    = user.getPassword();
        this.active     = Boolean.TRUE.equals(user.getActive());
        this.authorities  = Set.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()));
    }


    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return this.authorities; }
    @Override public String getPassword()                                     { return this.password;    }
    @Override public String getUsername()                                     { return this.email;      }
    @Override public boolean isAccountNonExpired()                            { return true;        }
    @Override public boolean isAccountNonLocked()                             { return true;        }
    @Override public boolean isCredentialsNonExpired()                        { return true;        }
    @Override public boolean isEnabled()                                      { return this.active;      }
}
