package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.ProjectsModel;
import org.example.ArHouseProject.diploma.models.RequestsModel;
import org.example.ArHouseProject.diploma.models.WorksModel;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ModelersController {
    private final ProjectsRepository projectsRepository;
    private final RequestsRepository requestsRepository;
    private final StatusRepository statusRepository;
    private final WorksRepository worksRepository;
    private final UserService userService;
    private final MailSender mailSender;

    public ModelersController(ProjectsRepository projectsRepository, RequestsRepository requestsRepository,
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

    @GetMapping("/arhouse.arh/modeler")
    public String getModeller(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("modeler_session", (WorksModel)auth.getPrincipal());
        model.addAttribute("requests", requestsRepository.findAll());

        check_work_session = ((WorksModel) auth.getPrincipal()).getUsername();

        List<ProjectsModel> models3mod = projectsRepository.findAllByStatusModel(statusRepository.findById(3));
        List<RequestsModel> request3 = new ArrayList<>();
        List<ProjectsModel> models4mod = projectsRepository.findAllByStatusModel(statusRepository.findById(4));
        List<RequestsModel> request4 = new ArrayList<>();

        for (ProjectsModel i: models3mod) {
            request3.add(i.getRequestsModel());
        }
        model.addAttribute("models3mod", request3);

        for (ProjectsModel i: models4mod) {
            request4.add(i.getRequestsModel());
        }
        model.addAttribute("models4mod", request4);

        model.addAttribute("request_post_mod", new RequestsModel());
        return "works/modeler";
    }

    @PostMapping("/arhouse.arh/modeler/{id}")
    public String postModeller(@PathVariable(value = "id") long id) {
        String modeler = "arhouse.arh/modeler";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        RequestsModel requestsModel_modelers = requestsRepository.findById(id);

        if (requestsModel_modelers.getStatusModel().getId().equals((long)0)) // если статус ожидание
        {
            ProjectsModel projectsModel = new ProjectsModel();
            projectsModel.setRequestsModel(requestsModel_modelers);

            SecureRandom random = new SecureRandom();
            String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < 15; i++) {
                int index = random.nextInt(ALPHA_CAPS.length());
                result.append(ALPHA_CAPS.charAt(index));
            }

            File myDir = new File(uploadModelRequest + "/" + requestsModel_modelers.getId().toString());
            boolean wasSuccessful = myDir.mkdirs();
            if (!wasSuccessful)
                System.out.println("Path failed.");

            if (requestsModel_modelers.getPasswordProject() == null)
                requestsModel_modelers.setPasswordProject(result.toString());

            requestsModel_modelers.setWorksModel(((WorksModel) auth.getPrincipal()));
            requestsModel_modelers.setStatusModel(statusRepository.findById(1)); // статус процесс
            projectsModel.setApprovedReport(false);
            projectsModel.setStatusModel(statusRepository.findById(3)); // статус моделирование
            requestsRepository.save(requestsModel_modelers);
            projectsRepository.save(projectsModel);

            String message = String.format(
                    "Здравствйте, %s %s.\n" +
                            "Ваша заявка успешно одобрена!\n" +
                            "Теперь у Вас есть возможность следить за процессом работы над проектом, не выходя из дома.\n" +
                            "Для этого вам необходимо авторизироваться здесь: http://localhost:10015/preview\n" +
                            "Данные для авторизации:\n" +
                            "\t Идентификатор: %s\n" +
                            "\t Пароль: %s\n" +

                            "Спасибо вам за использование услуг ArHouse!",
                    requestsModel_modelers.getFirstName(),
                    requestsModel_modelers.getSecondName(),
                    requestsModel_modelers.getId(),
                    requestsModel_modelers.getPasswordProject()
            );

            mailSender.send(requestsModel_modelers.getEmail(), "ArHouse", message);


            return "redirect:/" + modeler;
        }

        ProjectsModel project = projectsRepository.findProjectsModelsByRequestsModel(requestsModel_modelers);
        if (project.getStatusModel().getId() == 3) // если статус моделирование
        {
            project.setStatusModel(statusRepository.findById(4)); // статус моделированиеЗавершено
            projectsRepository.save(project);

            String message = String.format(
                    "Здравствйте, %s %s.\n" +
                            "Статус работы именился!\n" +
                            "Проверьте стадию работы здесь: http://localhost:10015/preview\n" +
                            "Данные для авторизации:\n" +
                            "\t Идентификатор: %s\n" +
                            "\t Пароль: %s\n" +

                            "Спасибо вам за использование услуг ArHouse!",
                    requestsModel_modelers.getFirstName(),
                    requestsModel_modelers.getSecondName(),
                    requestsModel_modelers.getId(),
                    requestsModel_modelers.getPasswordProject()
            );
            mailSender.send(requestsModel_modelers.getEmail(), "ArHouse", message);
        }
        return "redirect:/" + modeler;
    }

    @PostMapping("/arhouse.arh/modeler/editme/{id}")
    public String postEditMe(@ModelAttribute("edit_work") WorksModel worksModel,
                             @PathVariable(value = "id") long id) {
        String modeler = "arhouse.arh/modeler?#view_window", editwork = "arhouse.arh/modeler?#view_window_edit";

        if (check_work_session.equals(worksModel.getUsername())) {
            userService.saveUser(worksModel);
            return "redirect:/" + modeler;
        } else {
            if (worksRepository.findByUsername(worksModel.getUsername()) != null) {
                System.out.println("Пользователь с таким именем уже существует");
                return "redirect:/" + editwork;
            } else {
                userService.saveUser(worksModel);
                return "redirect:/" + modeler;
            }
        }
    }

    @GetMapping("/arhouse.arh/modeler/uploadrequest/{id}")
    public String getUploadRequest(@PathVariable(value = "id") long id, Model model) {
        String modeler = "arhouse.arh/modeler#process_requests";
        Optional<RequestsModel> RequestsModelOptional = Optional.ofNullable(requestsRepository.findById(id));

        if (RequestsModelOptional.isPresent()) {
            RequestsModel RequestsModel = requestsRepository.findById(id);
            ProjectsModel projectsModel = projectsRepository.findProjectsModelsByRequestsModel(RequestsModel);
            model.addAttribute("upd_request", projectsModel);
            return "works/uploadrequest";
        }
        else
            return "redirect:/" + modeler;
    }

    @PostMapping("/arhouse.arh/modeler/uploadrequest/{id}")
    public String postUploadRequest(@RequestParam("file") MultipartFile multipartFile,
                                    @PathVariable(value = "id") long id) throws IOException {
        String modeler = "arhouse.arh/modeler#process_requests", uploadrequest = "arhouse.arh/modeler/uploadrequest/{id}";

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
            return "redirect:/" + modeler;
        }
    }
}