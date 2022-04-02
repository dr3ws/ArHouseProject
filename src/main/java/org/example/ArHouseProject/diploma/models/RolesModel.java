package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;

@Entity
@Data
public class RolesModel implements GrantedAuthority {
    @Id
    private Long id;
    private String role;

    @Override
    public String getAuthority() {
        return getRole();
    }
}