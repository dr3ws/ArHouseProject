package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
public class WorksModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min=2, message = "Не меньше 2 знаков")
    private String firstName;
    @Size(min=2, message = "Не меньше 2 знаков")
    private String secondName;
    @Size(min=5, message = "Не меньше 5 знаков")
    private String username;
//    @Size(min=8, message = "Не меньше 8 знаков")
    //@Transient
    private String password;

    @OneToOne(fetch = FetchType.EAGER)
    private RolesModel roles;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> c = Collections.singleton(getRoles());
        return c;
    }
}