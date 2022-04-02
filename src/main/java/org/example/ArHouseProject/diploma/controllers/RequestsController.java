package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.ProjectsModel;
import org.example.ArHouseProject.diploma.models.RequestsModel;
import org.example.ArHouseProject.diploma.repository.ProjectsRepository;
import org.example.ArHouseProject.diploma.repository.RequestsRepository;
import org.example.ArHouseProject.diploma.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
public class RequestsController {
    private final ProjectsRepository projectsRepository;
    private final RequestsRepository requestsRepository;
    private final StatusRepository statusRepository;

    public RequestsController(ProjectsRepository projectsRepository, RequestsRepository requestsRepository,
                              StatusRepository statusRepository) {
        this.projectsRepository = projectsRepository;
        this.requestsRepository = requestsRepository;
        this.statusRepository = statusRepository;
    }

    @Value("${uploadModelRequest.path}")
    private String uploadModelRequest; // Путь расположения

    @Value("${uploadFilesPlane.path}")
    private String uploadFilesPlane; // Путь расположения
    @Value("${PathFilesPlane.path}")
    private String PathFilesPlane; // Путь в бд

    String check_request_pass = "";
    String request_plane = "";
    String request_nameObject = "";

    @GetMapping("/arhouse.arh/admin/editrequest/{id}")
    public String getEditRequest(@PathVariable(value = "id") long id, Model model) {
        String admin = "arhouse.arh/admin#requests";
        Optional<RequestsModel> requestsModelOptional = Optional.ofNullable(requestsRepository.findById(id));

        if (requestsModelOptional.isPresent()) {
            RequestsModel requestsModel_edit = requestsRepository.findById(id);

            check_request_pass = requestsModel_edit.getPasswordProject();
            request_plane = requestsModel_edit.getPlaneProject();
            request_nameObject = requestsModel_edit.getNameProject();
            
            if (requestsModel_edit.getPasswordProject() != null)
                requestsModel_edit.setPasswordProject(null);

            model.addAttribute("edit_request", requestsModel_edit);
            return "admins/requests/editrequest";
        }
        else
            return "redirect:/" + admin;
    }

    @PostMapping("/arhouse.arh/admin/editrequest/{id}")
    public String postEditRequest(@PathVariable(value = "id") long id,
                                  @RequestParam("file") MultipartFile multipartFile,
                                  @ModelAttribute("edit_request") @Valid RequestsModel requestsModel,
                                  BindingResult bindingResult) throws IOException {
        String admin = "arhouse.arh/admin#requests";
        boolean check = false;

        RequestsModel request = requestsRepository.findById(id);
        ProjectsModel project = projectsRepository.findProjectsModelsByRequestsModel(request);

        if (bindingResult.hasErrors())
            return "admins/requests/editrequest";
        else {
            if (!request_nameObject.equals(requestsModel.getNameProject())) {
                File uploadDirMove = new File(uploadFilesPlane + "/" + request_nameObject + "-" +
                        requestsModel.getId() + ".png");
                boolean wasSuccessful = uploadDirMove.delete();
                if (wasSuccessful)
                    System.out.println("File moved");
            }

            if (Objects.equals(multipartFile.getOriginalFilename(), ""))
                requestsModel.setPlaneProject(request_plane);
            else {
                File uploadDir = new File(uploadFilesPlane);
                boolean wasSuccessful = uploadDir.mkdirs();
                if (wasSuccessful)
                    System.out.println("Create path");

                multipartFile.transferTo(new File(uploadFilesPlane + "/" + requestsModel.getNameProject() + "-" +
                        requestsModel.getId() + ".png"));
                requestsModel.setPlaneProject(PathFilesPlane + "/" + requestsModel.getNameProject() + "-" +
                        requestsModel.getId() + ".png");

                requestsModel.setStatusModel(statusRepository.findById(0));
            }

            if (Objects.equals(requestsModel.getPasswordProject(), ""))
                requestsModel.setPasswordProject(check_request_pass);
            else
                requestsModel.setPasswordProject(requestsModel.getPasswordProject());

            List<ProjectsModel> projectsModelList = projectsRepository.findAllByRequestsModel(requestsModel);
            for (ProjectsModel item : projectsModelList) {
                if (item.getRequestsModel().getId().equals(requestsModel.getId())) {
                    projectsRepository.delete(project);
                    check = true;
                    break;
                }
            }

            if (!check)
                requestsModel.setStatusModel(statusRepository.findById(0));

            requestsRepository.save(requestsModel);

            if (check) {
                System.out.println("123");
                if (requestsModel.getStatusModel().getId() == 0) {
//                List<ProjectsModel> projectsModelList = projectsRepository.findAllByRequestsModel(requestsModel);
//
//                for (ProjectsModel item : projectsModelList) {
//                    if (item.getRequestsModel().getId().equals(requestsModel.getId())) {
//                        projectsRepository.delete(project);
//                        break;
//                    }
//                }
                        projectsRepository.delete(project);
                }

                if (requestsModel.getStatusModel().getId() == 1) {
                    if (project.getStatusModel().getId() == null || project.getStatusModel().getId() == 0)
                        project.setStatusModel(statusRepository.findById(3));

                    if (project.getStatusModel().getId() == 6)
                        project.setStatusModel(statusRepository.findById(5));

                    projectsRepository.save(project);
                }

                if (requestsModel.getStatusModel().getId() == 2) {
                    project.setStatusModel(statusRepository.findById(6));
                    projectsRepository.save(project);
                }
            }
            return "redirect:/" + admin;
        }
    }


    @GetMapping("/arhouse.arh/admin/editrequest/{id}/editmodel")
    public String getEditModel(@PathVariable(value = "id") long id, Model model) {
        RequestsModel requestsModel = requestsRepository.findById(id);
        model.addAttribute("edit_model", projectsRepository.findProjectsModelsByRequestsModel(requestsModel));
        return "admins/requests/editmodel";
    }

    @PostMapping("/arhouse.arh/admin/editrequest/{id}/editmodel")
    public String postEditModel(@ModelAttribute("edit_model") ProjectsModel projectsModel,
                                @RequestParam("file") MultipartFile multipartFile,
                                @PathVariable(value = "id") long id)throws IOException {
        String admin = "arhouse.arh/admin/editrequest/{id}";

        RequestsModel requestsModel = requestsRepository.findById(id);
        ProjectsModel projectsModelEdit = projectsRepository.findProjectsModelsByRequestsModel(requestsModel);

        if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
            projectsModelEdit.setStatusModel(projectsModel.getStatusModel());
            projectsRepository.save(projectsModelEdit);
        } else {
            File uploadDir = new File(uploadModelRequest);
            boolean wasSuccessful = uploadDir.mkdirs();
            if (wasSuccessful)
                System.out.println("Create path");

            projectsModelEdit.setStatusModel(projectsModel.getStatusModel());
            projectsRepository.save(projectsModelEdit);

            multipartFile.transferTo(new File(uploadModelRequest + "/" + requestsModel.getId() + "/" +
                    (requestsModel.getNameProject() + ".blend")));
            projectsModel.setPathToModel(uploadModelRequest + "/" + requestsModel.getId() + "/" +
                    (requestsModel.getNameProject() + ".blend"));
        }
        return "redirect:/" + admin;
    }

    @PostMapping("/arhouse.arh/admin/updtechtask/{id}")
    public String postUpdTechTask(@PathVariable(value = "id") long id) {
        String admin = "arhouse.arh/admin/editrequest/{id}";

        System.out.println("123");
        RequestsModel requestsModel_updtechtask = requestsRepository.findById(id);
        requestsModel_updtechtask.setTechTask(true);
        requestsModel_updtechtask.setToken(UUID.randomUUID().toString());
        requestsRepository.save(requestsModel_updtechtask);
        return "redirect:/" + admin;
    }

    @PostMapping("/arhouse.arh/admin/delrequest/{id}")
    public String postDelRequest(@PathVariable(value = "id") long id) {
        String admin = "arhouse.arh/admin#requests";
        RequestsModel requestsModel_del = requestsRepository.findById(id);
        requestsRepository.delete(requestsModel_del);
        return "redirect:/" + admin;
    }
}