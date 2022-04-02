package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.WorksModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/arhouse.arh/authorization")
    public String getAuthorization() {
        return "authorization/authorization";
    }

    @GetMapping("/successauth")
    public String getSuccessAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        switch (((WorksModel)auth.getPrincipal()).getRoles().getRole()) {
            case "ROLE_Администратор":
                return "redirect:/arhouse.arh/admin";
            case "ROLE_Модельер":
                return "redirect:/arhouse.arh/modeler";
            case "ROLE_Дизайнер":
                return "redirect:/arhouse.arh/designer";
            default:
                return "redirect:/arhouse.arh/idnex";
        }
    }
}