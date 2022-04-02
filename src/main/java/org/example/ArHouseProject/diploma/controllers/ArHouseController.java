package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.dto.SendMessage;
import org.example.ArHouseProject.diploma.models.RequestsModel;
import org.example.ArHouseProject.diploma.repository.ProjectsRepository;
import org.example.ArHouseProject.diploma.repository.RequestsRepository;
import org.example.ArHouseProject.diploma.repository.StatusRepository;
import org.example.ArHouseProject.diploma.service.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Controller
public class ArHouseController {
    private final RequestsRepository requestsRepository;
    private final StatusRepository statusRepository;
    private final ProjectsRepository projectsRepository;
    private final MailSender mailSender;

    public ArHouseController(RequestsRepository requestsRepository, StatusRepository statusRepository,
                             ProjectsRepository projectsRepository, MailSender mailSender) {
        this.requestsRepository = requestsRepository;
        this.statusRepository = statusRepository;
        this.projectsRepository = projectsRepository;
        this.mailSender = mailSender;
    }

    @Value("${uploadFilesPlane.path}")
    private String uploadFilesPlane; // Путь расположения
    @Value("${PathFilesPlane.path}")
    private String PathFilesPlane; // Путь в бд

    /** ГЛАВНАЯ СТРАНИЦА */

    @GetMapping("/arhouse.arh")
    public String getIndex() {
        return "/site/index";
    }

    /** СТРАНИЦА КАЛЬКУЛЯТОРА */

    @GetMapping("/arhouse.arh/calculator")
    public String getCalculator() {
        return "/site/calculator";
    }

    /** СТРАНИЦА ЗАЯВКИ */

    @GetMapping("/arhouse.arh/request")
    public String getRequest(Model model) {
        model.addAttribute("request_new", new RequestsModel());
        return "/site/request";
    }

    @PostMapping("/arhouse.arh/request")
    public String postRequest(@RequestParam("file") MultipartFile multipartFile,
                              @ModelAttribute("request_new") @Valid RequestsModel requestsModel,
                              BindingResult bindingResult) throws IOException {
        String requestsuccess = "arhouse.arh/request?success", requestnullfile = "arhouse.arh/request?nullfile";

        if (bindingResult.hasErrors())
            return "site/request";
        else {
            if (Objects.equals(multipartFile.getOriginalFilename(), ""))
                return "redirect:/" + requestnullfile;
            else {
                File uploadDir = new File(uploadFilesPlane);
                boolean wasSuccessful = uploadDir.mkdirs();
                if (wasSuccessful)
                    System.out.println("Create path");

                requestsRepository.save(requestsModel);

                multipartFile.transferTo(new File(uploadFilesPlane + "/" + requestsModel.getNameProject() + "-" +
                        requestsModel.getId() + ".png"));
                requestsModel.setPlaneProject(PathFilesPlane + "/" + requestsModel.getNameProject() + "-" +
                        requestsModel.getId() + ".png");

                requestsModel.setStatusModel(statusRepository.findById(0));
                requestsModel.setTechTask(false);
                requestsRepository.save(requestsModel);

                String message = String.format(
                        "Здравствйте, %s %s.\n" +
                                "Ваша заявка успешно зарегистрирована!\n" +
                                "После предоставления информации о помещении, она должна быть обработана в течение некоторого времени.\n" +
                                "Мы уведомим Вас когда заявка будет одобрена.\n" +
                                "Спасибо вам за использование услуг ArHouse!",
                        requestsModel.getFirstName(),
                        requestsModel.getSecondName()
                );
                mailSender.send(requestsModel.getEmail(), "ArHouse", message);

                return "redirect:/" + requestsuccess;
            }
        }
    }

    /** СТРАНИЦА ПРОСМОТРА */

    @GetMapping("/arhouse.arh/preview")
    public String getPreview(@ModelAttribute("preview") RequestsModel requestsModel, Model model) {
        model.addAttribute("request", requestsRepository.findAll());
        model.addAttribute("request1", new RequestsModel());
        return "/site/preview";
    }

    @PostMapping("/arhouse.arh/preview")
    public String postPreview(@ModelAttribute("request") RequestsModel requestsModel) {
        String previewerror = "arhouse.arh/preview?error";
        Iterable<RequestsModel> requestsModelIterable = requestsRepository.findAll();

        for(RequestsModel request_item : requestsModelIterable)
        {
            if (request_item.getAuthUsers(requestsModel.getId(), requestsModel.getPasswordProject()))
                return "redirect:/arhouse.arh/previews/" + request_item.getToken();
        }
        return "redirect:/" + previewerror;
    }

    @GetMapping("/arhouse.arh/previews/{token}")
    public String getPreviews(@PathVariable(value = "token") String token, Model model) {
        RequestsModel requestsModel = requestsRepository.findByToken(token);
        model.addAttribute("requests_item", projectsRepository.findProjectsModelsByRequestsModel(requestsModel));
        return "/site/previews";
    }

    @GetMapping("/arhouse.arh/previewsvr/{token}")
    public String getPreviewsVR(@PathVariable(value = "token") String token, Model model) {
        model.addAttribute("requests_item_vr", requestsRepository.findByToken(token));
        return "/site/previewsvr";
    }

    /** СТРАНИЦА СПИСКА ПРОЕКТОВ */

    @GetMapping("/arhouse.arh/projects")
    public String getProjects(Model model) {
        model.addAttribute("projects_item", requestsRepository.findAll());
        return "/site/projects";
    }

    /** СТРАНИЦА ПОЛЬЗОВАТЕЛЛЬСКОГО СОШЛАШЕНИЯ */

    @GetMapping("/arhouse.arh/rules")
    public String getRules() {
        return "/site/rules";
    }

    /** СТРАНИЦА О НАС */

    @GetMapping("/arhouse.arh/contact")
    public String getContact(Model model) {
        model.addAttribute("messge_new", new SendMessage());
        return "/site/contact";
    }

    @PostMapping("/arhouse.arh/contact")
    public String postContact(@ModelAttribute("message_new") SendMessage sendMessage) {
        String contactsuccess = "arhouse.arh/contact?success";

        String subject = sendMessage.getFirstName() + " " + sendMessage.getSecondName();
        mailSender.sendMe(sendMessage.getEmail(), "andrei.kalinin.99@yandex.ru", subject, sendMessage.getMessageUser());
        return "redirect:/" + contactsuccess;

    }

    /** СТРАНИЦА ОШИБКИ */

    @GetMapping("/404")
    public String getErrorPage() {
        return "/site/404";
    }
}