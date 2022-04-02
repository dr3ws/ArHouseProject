package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.example.ArHouseProject.diploma.models.FurnituresShopModel;
import org.example.ArHouseProject.diploma.models.ShopsModel;
import org.example.ArHouseProject.diploma.repository.FurnituresRepository;
import org.example.ArHouseProject.diploma.repository.FurnituresShopRepository;
import org.example.ArHouseProject.diploma.repository.OrganizationsRepository;
import org.example.ArHouseProject.diploma.repository.ShopsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ShopsController {
    private final FurnituresRepository furnituresRepository;
    private final FurnituresShopRepository furnituresShopRepository;
    private final OrganizationsRepository organizationsRepository;
    private final ShopsRepository shopsRepository;

    public ShopsController(FurnituresRepository furnituresRepository, ShopsRepository shopsRepository,
                           OrganizationsRepository organizationsRepository, FurnituresShopRepository furnituresShopRepository) {
        this.furnituresRepository = furnituresRepository;
        this.furnituresShopRepository = furnituresShopRepository;
        this.organizationsRepository = organizationsRepository;
        this.shopsRepository = shopsRepository;
    }

    String check_shop = "";

    @GetMapping("/arhouse.arh/admin/addshop")
    public String getAddShop(Model model) {
        model.addAttribute("add_shop", new ShopsModel());
        model.addAttribute("organizations_item", organizationsRepository.findAll());
        return "admins/shops/addshop";
    }

    @PostMapping("/arhouse.arh/admin/addshop")
    public String postAddShop(@ModelAttribute("add_shop") @Valid ShopsModel shopsModel,
                              BindingResult bindingResult) {
        String admin = "arhouse.arh/admin#shops", addshop = "arhouse.arh/admin/addshop";

        if (bindingResult.hasErrors())
            return "admins/shops/addshop";
        else {
            if (shopsRepository.findShopsModelByNameShop(shopsModel.getNameShop()) != null) {
                System.out.println("Такой магазин уже существует");
                return "redirect:/" + addshop;
            } else {
                shopsRepository.save(shopsModel);
                return "redirect:/" + admin;
            }
        }
    }

    @GetMapping("/arhouse.arh/admin/editshop/{id}")
    public String getEditShop(@PathVariable(value = "id") long id, Model model) {
        String admin = "arhouse.arh/admin#shops";

        Optional<ShopsModel> shopsModelOptional = Optional.ofNullable(shopsRepository.findById(id));

        if (shopsModelOptional.isPresent()) {
            model.addAttribute("organizations_item", organizationsRepository.findAll());
            ShopsModel shopsModel_edit = shopsRepository.findById(id);
            model.addAttribute("edit_shop", shopsModel_edit);
            check_shop = shopsModel_edit.getNameShop();
            return "admins/shops/editshop";
        }
        else
            return "redirect:/" + admin;
    }

    @PostMapping("/arhouse.arh/admin/editshop/{id}")
    public String postEditShop(@PathVariable(value = "id") long id,
                               @ModelAttribute("edit_shop") @Valid ShopsModel shopsModel,
                               BindingResult bindingResult) {
        String admin = "arhouse.arh/admin#shops", editshop = "arhouse.arh/admin/editshop/{id}";

        if (bindingResult.hasErrors())
            return "admins/shops/editshop";
        else {
            if (check_shop.equals(shopsModel.getNameShop())) {
                shopsRepository.save(shopsModel);
                return "redirect:/" + admin;
            } else {
                if (shopsRepository.findShopsModelByNameShop(shopsModel.getNameShop()) != null) {
                    System.out.println("Такой магазин уже существует");
                    return "redirect:/" + editshop;
                } else {
                    shopsRepository.save(shopsModel);
                    return "redirect:/" + admin;
                }
            }
        }
    }

    @PostMapping("/arhouse.arh/admin/delshop/{id}")
    public String postDelShop(@PathVariable(value = "id") long id) {
        String admin = "arhouse.arh/admin#shops";

        List<Integer> furnId = new ArrayList<>();

        ShopsModel shopsModel_del = shopsRepository.findById(id);
        List<FurnituresShopModel> furnituresShopModel = furnituresShopRepository.findAllByShopsModel(shopsModel_del);

        for (FurnituresShopModel item : furnituresShopModel) {
            if (item.getShopsModel().getId().equals(shopsModel_del.getId())) {
                furnId.add(Math.toIntExact(item.getFurnituresModel().getId()));
                furnituresShopRepository.delete(item);
            }
        }

        for (Integer item : furnId) {
            FurnituresModel furnituresModel = furnituresRepository.findById(item.longValue());
            if (furnituresModel.getPathToFurniture() != null) {
                File uploadDir = new File(furnituresModel.getPathToFurniture());
                boolean wasSuccessful = uploadDir.delete();
                if (!wasSuccessful)
                    System.out.println("File not deleted");
            }
            furnituresRepository.delete(furnituresModel);
        }

        shopsRepository.delete(shopsModel_del);
        return "redirect:/" + admin;
    }
}