package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.ParsingTXT;
import org.example.ArHouseProject.diploma.models.*;
import org.example.ArHouseProject.diploma.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class ProjectsController {
    private final FurnituresProjectRepository furnituresProjectRepository;
    private final FurnituresRepository furnituresRepository;
    private final ProjectsRepository projectsRepository;
    private final RequestsRepository requestsRepository;
    private final FurnituresShopRepository furnituresShopRepository;

    public ProjectsController(FurnituresProjectRepository furnituresProjectRepository, FurnituresRepository furnituresRepository,
                              ProjectsRepository projectsRepository, RequestsRepository requestsRepository,
                              FurnituresShopRepository furnituresShopRepository) {
        this.furnituresProjectRepository = furnituresProjectRepository;
        this.furnituresRepository = furnituresRepository;
        this.projectsRepository = projectsRepository;
        this.requestsRepository = requestsRepository;
        this.furnituresShopRepository = furnituresShopRepository;
    }

    @GetMapping("/arhouse.arh/admin/createreports/{id}")
    public String getCreateReports(@PathVariable(value = "id") long id, Model model) {
        String report = "arhouse.arh/admin/readyreport/{id}";

        RequestsModel requestsModel = requestsRepository.findById(id);
        ProjectsModel projectsModel = projectsRepository.findProjectsModelsByRequestsModel(requestsModel);

        if (projectsModel.getApprovedReport())
            return "redirect:/" + report;
        else {
            model.addAttribute("create_report", projectsModel);
            return "admins/reports/createreport";
        }
    }

    @PostMapping("/arhouse.arh/admin/createreports/{id}")
    public String postCreateReports(@PathVariable(value = "id") long id,
                                    @ModelAttribute("create_report") ProjectsModel projectsModel,
                                    @RequestParam("file") MultipartFile multipartFile) throws IOException {
        String report = "arhouse.arh/admin/readyreport/{id}", createreports = "arhouse.arh/admin/createreports/{id}";

        if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
            System.out.println("Нет файла");
            return "redirect:/" + createreports;
        } else {
            ParsingTXT parsingTXT = new ParsingTXT(furnituresProjectRepository, furnituresRepository, projectsRepository, requestsRepository);
            parsingTXT.myListTXT(multipartFile.getInputStream());
            return "redirect:/" + report;
        }
    }

    @GetMapping("/arhouse.arh/admin/readyreport/{id}")
    public String getReadyReport(@PathVariable(value = "id") long id, Model model) {
        List<SecondaryModel> secondaryModels = new ArrayList<>();
        RequestsModel requestsModel = requestsRepository.findById(id);
        ProjectsModel projectsModel = projectsRepository.findProjectsModelsByRequestsModel(requestsModel);
        List<FurnituresProjectModel> furnitureProjectModels = furnituresProjectRepository.findAllByProjectsModel(projectsModel);

        List<FurnituresModel> furnituresModels = new ArrayList<>();
        for (FurnituresProjectModel tmp : furnitureProjectModels) {
            furnituresModels.add(tmp.getFurnituresModel());
        }

        List<FurnituresShopModel> furnituresShopModelList = new ArrayList<>();
        for (FurnituresModel furnituresModel : furnituresModels){
            List<FurnituresShopModel> furnituresShopModelList1 = furnituresShopRepository.findAllByFurnituresModel(furnituresModel);
            furnituresShopModelList.addAll(furnituresShopModelList1);
        }

        for (FurnituresShopModel fsm : furnituresShopModelList) {
            SecondaryModel secondaryModel = new SecondaryModel();
            secondaryModel.setNameFurniture(fsm.getFurnituresModel().getNameFurniture());
            secondaryModel.setPriceFurniture(fsm.getPrice());
            secondaryModel.setCategories(fsm.getFurnituresModel().getCategoriesModel().getCategories());
            secondaryModel.setNameShop(fsm.getShopsModel().getNameShop());
            secondaryModel.setCountShop(fsm.getCount());
            secondaryModel.setCountProject(furnituresProjectRepository.findByFurnituresModelAndProjectsModel(fsm.getFurnituresModel(), projectsModel).getCount());
            secondaryModels.add(secondaryModel);
        }
        model.addAttribute("ready_report", secondaryModels);
        return "admins/reports/readyreport";
    }
}