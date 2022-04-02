package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.OrganizationsModel;
import org.example.ArHouseProject.diploma.repository.OrganizationsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class OrganizationController {
    private final OrganizationsRepository organizationsRepository;

    public OrganizationController(OrganizationsRepository organizationsRepository) {
        this.organizationsRepository = organizationsRepository;
    }

    @GetMapping("/arhouse.arh/admin/addorganization")
    public String getAddOrganization(Model model) {
        model.addAttribute("add_organization", new OrganizationsModel());
        return "admins/shops/addorganization";
    }

    @PostMapping("/arhouse.arh/admin/addorganization")
    public String postAddOrganization(@ModelAttribute("add_organization") @Valid OrganizationsModel organizationsModel,
                                      BindingResult bindingResult) {
        String admin = "arhouse.arh/admin#shops", addorganization = "arhouse.arh/admin/addorganization";

        if (bindingResult.hasErrors())
            return "admins/shops/addorganization";
        else {
            if (organizationsRepository.findOrganizationsModelByNameOrganizationAndInnAndOgrn(organizationsModel.getNameOrganization(),
                    organizationsModel.getInn(), organizationsModel.getOgrn()) != null) {
                System.out.println("Такая организация уже существует");
                return "redirect:/" + addorganization;
            } else {
                organizationsRepository.save(organizationsModel);
                return "redirect:/" + admin;
            }
        }
    }
}
//    @GetMapping("/admin/editorganization/{id}")
//    public String getEditOrganization(@PathVariable(value = "id") long id, Model model) {
//        String admin = "admin#shops";
//
//        Optional<ShopsModel> shopsModelOptional = Optional.ofNullable(shopsRepository.findById(id));
//
//        if (shopsModelOptional.isPresent()) {
//            ShopsModel shopsModel_edit = shopsRepository.findById(id);
//            model.addAttribute("edit_shop", shopsModel_edit);
//            check_shop = shopsModel_edit.getNameShop();
//            return "admins/shops/editshop";
//        }
//        else
//            return "redirect:/" + admin;
//    }
//
//    @PostMapping("/admin/editorganization/{id}")
//    public String postEditOrganization(@ModelAttribute("edit_shop") ShopsModel shopsModel,
//                               @PathVariable(value = "id") long id) {
//        String admin = "admin#shops", editshop = "admin/editshop/{id}";
//
//        if (check_shop.equals(shopsModel.getNameShop())) {
//            shopsRepository.save(shopsModel);
//            return "redirect:/" + admin;
//        } else {
//            if (shopsRepository.findShopsModelByNameShop(shopsModel.getNameShop()) != null) {
//                System.out.println("Такой магазин уже существует");
//                return "redirect:/" + editshop;
//            } else {
//                shopsRepository.save(shopsModel);
//                return "redirect:/" + admin;
//            }
//        }
//    }

//    @PostMapping("/admin/delorganization/{id}")
//    public String postDelOrganization(@PathVariable(value = "id") long id) {
//        String admin = "admin#shops";
//        OrganizationsModel organizationsModel_del = organizationsRepository.findById(id);
//        organizationsRepository.delete(organizationsModel_del);
//        return "redirect:/" + admin;
//    }