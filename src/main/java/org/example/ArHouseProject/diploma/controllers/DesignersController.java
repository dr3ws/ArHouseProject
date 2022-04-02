package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.*;
import org.example.ArHouseProject.diploma.repository.*;
import org.example.ArHouseProject.diploma.service.MailSender;
import org.example.ArHouseProject.diploma.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class DesignersController {
    private final ProjectsRepository projectsRepository;
    private final RequestsRepository requestsRepository;
    private final StatusRepository statusRepository;
    private final WorksRepository worksRepository;
    private final UserService userService;
    private final MailSender mailSender;

    public DesignersController(ProjectsRepository projectsRepository, RequestsRepository requestsRepository,
                               StatusRepository statusRepository, WorksRepository worksRepository,
                               UserService userService, MailSender mailSender) {
        this.projectsRepository = projectsRepository;
        this.requestsRepository = requestsRepository;
        this.statusRepository = statusRepository;
        this.worksRepository = worksRepository;
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Value("${uploadModelRequest.path}")
    private String uploadModelRequest; // Путь расположения
//    @Value("${PathModelRequest.path}")
//    private String PathModelRequest; // Путь в бд

    String check_work_session = "";

    @GetMapping("/arhouse.arh/designer")
    public String getDesigners(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("designer_session", (WorksModel)auth.getPrincipal());
        model.addAttribute("requests", requestsRepository.findAll());

        check_work_session = ((WorksModel) auth.getPrincipal()).getUsername();

        List<ProjectsModel> models4des = projectsRepository.findAllByStatusModel(statusRepository.findById(4));
        List<RequestsModel> request4 = new ArrayList<>();
        List<ProjectsModel> models5des = projectsRepository.findAllByStatusModel(statusRepository.findById(5));
        List<RequestsModel> request5 = new ArrayList<>();
        List<ProjectsModel> models6des = projectsRepository.findAllByStatusModel(statusRepository.findById(6));
        List<RequestsModel> request6 = new ArrayList<>();

        for (ProjectsModel i: models4des) {
            request4.add(i.getRequestsModel());
        }
        model.addAttribute("models4des", request4);

        for (ProjectsModel i: models5des) {
            request5.add(i.getRequestsModel());
        }
        model.addAttribute("models5des", request5);

        for (ProjectsModel i: models6des) {
            request6.add(i.getRequestsModel());
        }
        model.addAttribute("models6des", request6);

        model.addAttribute("request_post_des", new RequestsModel());
        return "works/designer";
    }

    @PostMapping("/arhouse.arh/designer/{id}")
    public String postDesigners(@PathVariable(value = "id") long id) {
        String designer = "arhouse.arh/designer";
        RequestsModel requestsModel_designers = requestsRepository.findById(id);
        ProjectsModel project = projectsRepository.findProjectsModelsByRequestsModel(requestsModel_designers);

        if (project.getStatusModel().getId().equals((long)4)) // если статус моделированиеЗавершено
        {
            project.setStatusModel(statusRepository.findById(5)); // статус дизайн
            projectsRepository.save(project);
        }
        else if (project.getStatusModel().getId() == 5) // если статус дизайн
        {
            requestsModel_designers.setStatusModel(statusRepository.findById(2)); // статус завершено
            project.setStatusModel(statusRepository.findById(6)); // статус дизайнЗавершено
            requestsRepository.save(requestsModel_designers);
            projectsRepository.save(project);

            String message = String.format(
                    "Здравствйте, %s %s.\n" +
                            "Работа над вашим проектом завершена!\n" +
                            "Проверьте результат работы здесь: http://localhost:10015/preview\n" +
                            "Данные для авторизации:\n" +
                            "\t Идентификатор: %s\n" +
                            "\t Пароль: %s\n" +

                            "Спасибо вам за использование услуг ArHouse!",
                    requestsModel_designers.getFirstName(),
                    requestsModel_designers.getSecondName(),
                    requestsModel_designers.getId(),
                    requestsModel_designers.getPasswordProject()
            );

            mailSender.send(requestsModel_designers.getEmail(), "ArHouse", message);
        }
        return "redirect:/" + designer;
    }

    @PostMapping("/arhouse.arh/designer/editme/{id}")
    public String postEditMe(@ModelAttribute("edit_work") WorksModel worksModel,
                             @PathVariable(value = "id") long id) {
        String designer = "arhouse.arh/designer?#view_window", editwork = "arhouse.arh/designer?#view_window_edit";

        if (check_work_session.equals(worksModel.getUsername())) {
            userService.saveUser(worksModel);
            return "redirect:/" + designer;
        } else {
            if (worksRepository.findByUsername(worksModel.getUsername()) != null) {
                System.out.println("Пользователь с таким именем уже существует");
                return "redirect:/" + editwork;
            } else {
                userService.saveUser(worksModel);
                return "redirect:/" + designer;
            }
        }
    }

    @GetMapping("/arhouse.arh/designer/uploadrequest/{id}")
    public String getUploadRequest(@PathVariable(value = "id") long id, Model model) {
        String designer = "arhouse.arh/designer#process_requests";
        Optional<RequestsModel> requestModelOptional = Optional.ofNullable(requestsRepository.findById(id));

        if (requestModelOptional.isPresent()) {
            RequestsModel requestsModel = requestsRepository.findById(id);
            ProjectsModel projectsModel = projectsRepository.findProjectsModelsByRequestsModel(requestsModel);
            model.addAttribute("upd_request", projectsModel);
            return "works/uploadrequest";
        }
        else
            return "redirect:/" + designer;
    }

    @PostMapping("/arhouse.arh/designer/uploadrequest/{id}")
    public String postUploadRequest(@RequestParam("file") MultipartFile multipartFile,
                                  @PathVariable(value = "id") long id) throws IOException {
        String designer = "arhouse.arh/designer#process_requests", uploadrequest = "arhouse.arh/designer/uploadrequest/{id}";

        if (Objects.equals(multipartFile.getOriginalFilename(), ""))
            return "redirect:/" + uploadrequest;
        else {
            File uploadDir = new File(uploadModelRequest);
            boolean wasSuccessful = uploadDir.mkdirs();
            if (wasSuccessful)
                System.out.println("Create path");

            RequestsModel RequestsModel = requestsRepository.findById(id);
            ProjectsModel projectsModel = projectsRepository.findProjectsModelsByRequestsModel(RequestsModel);

            multipartFile.transferTo(new File(uploadModelRequest + "/" + projectsModel.getRequestsModel().getId() + "/" +
                    (projectsModel.getRequestsModel().getNameProject() + ".blend")));
            projectsModel.setPathToModel(uploadModelRequest + "/" + projectsModel.getRequestsModel().getId() + "/" +
                    (projectsModel.getRequestsModel().getNameProject() + ".blend"));

            projectsRepository.save(projectsModel);
            return "redirect:/" + designer;
        }
    }
}