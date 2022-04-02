package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.WorksModel;
import org.example.ArHouseProject.diploma.repository.*;
import org.example.ArHouseProject.diploma.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PersonnelController {
    private final RequestsRepository requestsRepository;
    private final ProjectsRepository projectsRepository;
    private final FurnituresShopRepository furnituresShopRepository;
    private final ShopsRepository shopsRepository;
    private final WorksRepository worksRepository;
    private final UserService userService;

    public PersonnelController(RequestsRepository requestsRepository, ProjectsRepository projectsRepository,
                               FurnituresShopRepository furnituresShopRepository, ShopsRepository shopsRepository,
                               WorksRepository worksRepository, UserService userService) {
        this.requestsRepository = requestsRepository;
        this.projectsRepository = projectsRepository;
        this.furnituresShopRepository = furnituresShopRepository;
        this.shopsRepository = shopsRepository;
        this.worksRepository = worksRepository;
        this.userService = userService;
    }

    String check_work_login = "";
    String check_work_pass = "";
    String check_work_session = "";

    @GetMapping("/arhouse.arh/admin")
    public String getAdmin(Model model) {
        model.addAttribute("admins_item", worksRepository.findAll());
        model.addAttribute("requests_item", requestsRepository.findAll());
        model.addAttribute("shopsfurnitures_item", furnituresShopRepository.findAll());
        model.addAttribute("shops_item", shopsRepository.findAll());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("admin_session", (WorksModel)auth.getPrincipal());
        model.addAttribute("reports_item", projectsRepository.findAll());

        check_work_session = ((WorksModel) auth.getPrincipal()).getUsername();

        return "admins/admin";
    }

    @PostMapping("/arhouse.arh/admin/editme/{id}")
    public String postEditMe(@PathVariable(value = "id") long id,
                             @ModelAttribute("edit_work") WorksModel worksModel) {
        String admin = "arhouse.arh/admin#view_window", editwork = "arhouse.arh/admin?#view_window_edit";

        if (check_work_session.equals(worksModel.getUsername())) {
            userService.saveUser(worksModel);
            return "redirect:/" + admin;
        } else {
            if (worksRepository.findByUsername(worksModel.getUsername()) != null) {
                System.out.println("Пользователь с таким именем уже существует");
                return "redirect:/" + editwork;
            } else {
                userService.saveUser(worksModel);
                return "redirect:/" + admin;
            }
        }
    }

    @PostMapping("/arhouse.arh/admin/delwork/{id}")
    public String postDelWork(@PathVariable(value = "id") long id) {
        String admin = "arhouse.arh/admin";
        WorksModel worksModel_del = worksRepository.findById(id);
        worksRepository.delete(worksModel_del);
        return "redirect:/" + admin;
    }

    @GetMapping("/arhouse.arh/admin/editwork/{id}")
    public String getEditWork(@PathVariable(value = "id") long id, Model model) {
        String admin = "arhouse.arh/admin#personnel";

        Optional<WorksModel> worksModelOptional = Optional.ofNullable(worksRepository.findById(id));

        if (worksModelOptional.isPresent()) {
            WorksModel worksModel_edit = worksRepository.findById(id);
            check_work_login = worksModel_edit.getUsername();
            check_work_pass = worksModel_edit.getPassword();

            if (worksModel_edit.getPassword() != null)
                worksModel_edit.setPassword(null);

            model.addAttribute("edit_work", worksModel_edit);
            return "admins/personnel/editwork";
        }
        else
            return "redirect:/" + admin;
    }

    @PostMapping("/arhouse.arh/admin/editwork/{id}")
    public String postEditWork(@PathVariable(value = "id") long id,
                               @ModelAttribute("edit_work") @Valid WorksModel worksModel,
                               BindingResult bindingResult) {
        String admin = "arhouse.arh/admin#personnel", editwork = "arhouse.arh/admin/editwork/{id}?error";

        if (bindingResult.hasErrors())
            return "admins/personnel/editwork";
        else {
            if (check_work_login.equals(worksModel.getUsername())) {
                userService.saveEditUser(worksModel, check_work_pass);
                return "redirect:/" + admin;
            } else {
                if (worksRepository.findByUsername(worksModel.getUsername()) != null)
                    return "redirect:/" + editwork;
                else {
                    userService.saveEditUser(worksModel, check_work_pass);
                    return "redirect:/" + admin;
                }
            }
        }
    }

    @GetMapping("/arhouse.arh/admin/addwork")
    public String getAddWork(Model model) {
        model.addAttribute("add_work", new WorksModel());
        return "admins/personnel/addwork";
    }

    @PostMapping("/arhouse.arh/admin/addwork")
    public String postAddWork(@ModelAttribute("add_work") @Valid WorksModel worksModel,
                              BindingResult bindingResult) {
        String admin = "arhouse.arh/admin#personnel";

        if (bindingResult.hasErrors())
            return "admins/personnel/addwork";
        else {
            if (worksRepository.findByUsername(worksModel.getUsername()) != null) {
                System.out.println("Пользователь с таким именем уже существует");
                return "admins/personnel/addwork";
            } else {
                userService.saveUser(worksModel);
                return "redirect:/" + admin;
            }
        }
    }
}