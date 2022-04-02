package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.example.ArHouseProject.diploma.models.FurnituresShopModel;
import org.example.ArHouseProject.diploma.repository.CatigoriesRepository;
import org.example.ArHouseProject.diploma.repository.FurnituresRepository;
import org.example.ArHouseProject.diploma.repository.FurnituresShopRepository;
import org.example.ArHouseProject.diploma.repository.ShopsRepository;
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

@Controller
public class FurnituresController {
    private final CatigoriesRepository catigoriesRepository;
    private final FurnituresRepository furnituresRepository;
    private final FurnituresShopRepository furnituresShopRepository;
    private final ShopsRepository shopsRepository;

    public FurnituresController(CatigoriesRepository catigoriesRepository, FurnituresRepository furnituresRepository,
                                ShopsRepository shopsRepository, FurnituresShopRepository furnituresShopRepository) {
        this.catigoriesRepository = catigoriesRepository;
        this.furnituresRepository = furnituresRepository;
        this.furnituresShopRepository = furnituresShopRepository;
        this.shopsRepository = shopsRepository;
    }

    @Value("${uploadModelFurniture.path}")
    private String uploadModelFurniture; // Путь расположения
//    @Value("${PathModelFurniture.path}")
//    private String PathModelFurniture; // Путь в бд

    String check_furniture = "";
    String check_path = "";

    @GetMapping("/arhouse.arh/admin/addfurniture")
    public String getAddFurniture(Model model) {
        model.addAttribute("shops_item", shopsRepository.findAll());
        model.addAttribute("catigories_item", catigoriesRepository.findAll());
        model.addAttribute("add_furniture", new FurnituresShopModel());
        return "admins/shops/addfurniture";
    }

    @PostMapping("/arhouse.arh/admin/addfurniture")
    public String postAddFurniture(@RequestParam("file") MultipartFile multipartFile,
                                   @ModelAttribute("add_furniture") @Valid FurnituresShopModel furnituresShopModel,
                                   BindingResult bindingResult) throws IOException {
        String admin = "arhouse.arh/admin#shops/furniture";

        boolean checkShop = false;
        int checkId = 0;

        FurnituresModel checkAddFurniture = furnituresRepository.findFurnitureModelByArticul(furnituresShopModel.getFurnituresModel().getArticul());

        if (bindingResult.hasErrors())
            return "admins/shops/addfurniture";
        else {
            if (checkAddFurniture != null) {
                List<FurnituresShopModel> furnituresShopModelList = furnituresShopRepository.findAllByFurnituresModel(checkAddFurniture);

                for (FurnituresShopModel item : furnituresShopModelList) {
                    if (item.getShopsModel() == furnituresShopModel.getShopsModel()) {
                        checkId = Math.toIntExact(item.getId());
                        checkShop = true;
                        break;
                    }
                }

                if (checkShop) {
                    FurnituresShopModel currentFurniture = furnituresShopRepository.findById(checkId); // текущая мебель

                    int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();

                    currentFurniture.setPrice(furnituresShopModel.getPrice()); // перезапись цены
                    currentFurniture.setCount(furnituresShopModel.getCount()); // перезапись количества
                    currentFurniture.setTotalPrice((long) totalprice); // перезапись суммы

                    furnituresShopRepository.save(currentFurniture);
                } else {
                    if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
                        int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                        furnituresShopModel.setTotalPrice((long) totalprice);
                    } else {
                        File uploadDir = new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories());
                        boolean wasSuccessful = uploadDir.mkdirs();
                        if (wasSuccessful)
                            System.out.println("Create path");

                        furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                        furnituresShopRepository.save(furnituresShopModel);

                        multipartFile.transferTo(new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend"));
                        furnituresShopModel.getFurnituresModel().setPathToFurniture(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend");

                        int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                        furnituresShopModel.setTotalPrice((long) totalprice);
                    }
                    furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                    furnituresShopRepository.save(furnituresShopModel);
                }
            } else {
                if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
                    int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                    furnituresShopModel.setTotalPrice((long) totalprice);
                } else {
                    File uploadDir = new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories());
                    boolean wasSuccessful = uploadDir.mkdirs();
                    if (wasSuccessful)
                        System.out.println("Create path");

                    furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                    furnituresShopRepository.save(furnituresShopModel);

                    multipartFile.transferTo(new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend"));
                    furnituresShopModel.getFurnituresModel().setPathToFurniture(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend");

                    int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                    furnituresShopModel.setTotalPrice((long) totalprice);
                }
                furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                furnituresShopRepository.save(furnituresShopModel);
            }
            return "redirect:/" + admin;
        }
    }

    @GetMapping("/arhouse.arh/admin/editfurniture/{id}")
    public String getEditFurniture(@PathVariable(value = "id") long id, Model model) {
        String admin = "arhouse.arh/admin#shops";

        Optional<FurnituresShopModel> sfModelOptional = Optional.ofNullable(furnituresShopRepository.findById(id));

        if (sfModelOptional.isPresent()) {
            model.addAttribute("shops_item", shopsRepository.findAll());
            model.addAttribute("catigories_item", catigoriesRepository.findAll());
            FurnituresShopModel furnituresShopModel_edit = furnituresShopRepository.findById(id);
            model.addAttribute("edit_furniture", furnituresShopModel_edit);
            check_furniture = furnituresShopModel_edit.getFurnituresModel().getNameFurniture();
            check_path = furnituresShopModel_edit.getFurnituresModel().getPathToFurniture();
            return "admins/shops/editfurniture";
        }
        else
            return "redirect:/" + admin;
    }

    @PostMapping("/arhouse.arh/admin/editfurniture/{id}")
    public String postEditFurniture(@RequestParam("file") MultipartFile multipartFile,
                                    @PathVariable(value = "id") long id,
                                    @ModelAttribute("edit_furniture") FurnituresShopModel furnituresShopModel,
                                    BindingResult bindingResult) throws IOException {
        String admin = "arhouse.arh/admin#shopsfurniture";

        boolean checkShop = false;
        int checkId = 0;

        if (bindingResult.hasErrors())
            return "admins/shops/editfurniture";
        else {

            if (check_furniture.equals(furnituresShopModel.getFurnituresModel().getNameFurniture())) {
                int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                furnituresShopModel.setTotalPrice((long) totalprice);

                if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
                    furnituresShopModel.getFurnituresModel().setPathToFurniture(check_path);
                } else {
                    if (check_path != null) {
                        File uploadDirMove = new File(furnituresShopModel.getFurnituresModel().getPathToFurniture());
                        boolean wasSuccessful = uploadDirMove.delete();
                        if (wasSuccessful)
                            System.out.println("File moved");
                    }

                    File uploadDir = new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories());
                    boolean wasSuccessful = uploadDir.mkdirs();
                    if (wasSuccessful)
                        System.out.println("Create path");

                    multipartFile.transferTo(new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend"));
                    furnituresShopModel.getFurnituresModel().setPathToFurniture(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                            + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend");
                }
                furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                furnituresShopRepository.save(furnituresShopModel);
            } else {
                FurnituresModel checkAddFurniture = furnituresRepository.findFurnitureModelByArticul(furnituresShopModel.getFurnituresModel().getArticul());

                if (checkAddFurniture != null) {
                    List<FurnituresShopModel> FurnituresShopModelList = furnituresShopRepository.findAllByFurnituresModel(checkAddFurniture);

                    for (FurnituresShopModel item : FurnituresShopModelList) {
                        if (item.getShopsModel() == furnituresShopModel.getShopsModel()) {
                            checkId = Math.toIntExact(item.getId());
                            checkShop = true;
                            break;
                        }
                    }

                    if (checkShop) {
                        FurnituresShopModel currentFurniture = furnituresShopRepository.findById(checkId); // текущая мебель

                        int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();

                        currentFurniture.setPrice(furnituresShopModel.getPrice()); // перезапись цены
                        currentFurniture.setCount(furnituresShopModel.getCount()); // перезапись количества
                        currentFurniture.setTotalPrice((long) totalprice); // перезапись суммы

                        furnituresShopRepository.save(currentFurniture);
                    } else {
                        if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
                            int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                            furnituresShopModel.setTotalPrice((long) totalprice);
                        } else {
                            File uploadDir = new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                    + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories());
                            boolean wasSuccessful = uploadDir.mkdirs();
                            if (wasSuccessful)
                                System.out.println("Create path");

                            furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                            furnituresShopRepository.save(furnituresShopModel);

                            multipartFile.transferTo(new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                    + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                                    + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend"));
                            furnituresShopModel.getFurnituresModel().setPathToFurniture(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                    + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories()
                                    + "/" + furnituresShopModel.getFurnituresModel().getId() + ".blend");

                            int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                            furnituresShopModel.setTotalPrice((long) totalprice);
                        }
                        furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                        furnituresShopRepository.save(furnituresShopModel);
                    }
                } else {
                    if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
                        int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                        furnituresShopModel.setTotalPrice((long) totalprice);
                        if (!check_path.equals(furnituresShopModel.getFurnituresModel().getPathToFurniture())) {
                            File uploadDirMove = new File(check_path);
                            boolean wasSuccessful = uploadDirMove.delete();
                            if (wasSuccessful)
                                System.out.println("File moved");
                        }
                    } else {
                        if (!check_path.equals(furnituresShopModel.getFurnituresModel().getPathToFurniture())) {
                            File uploadDirMove = new File(furnituresShopModel.getFurnituresModel().getPathToFurniture());
                            boolean wasSuccessful = uploadDirMove.delete();
                            if (wasSuccessful)
                                System.out.println("File moved");
                        }

                        File uploadDir = new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories());
                        boolean wasSuccessful = uploadDir.mkdirs();
                        if (wasSuccessful)
                            System.out.println("Create path");

                        multipartFile.transferTo(new File(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories() + "/" + furnituresShopModel.getId() + ".blend"));
                        furnituresShopModel.getFurnituresModel().setPathToFurniture(uploadModelFurniture + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getParentCategories()
                                + "/" + furnituresShopModel.getFurnituresModel().getCategoriesModel().getCategories() + "/" + furnituresShopModel.getId() + ".blend");

                        int totalprice = furnituresShopModel.getPrice().intValue() * furnituresShopModel.getCount().intValue();
                        furnituresShopModel.setTotalPrice((long) totalprice);
                    }
                    furnituresRepository.save(furnituresShopModel.getFurnituresModel());
                    furnituresShopRepository.save(furnituresShopModel);
                }
            }
            return "redirect:/" + admin;
        }
    }

    @PostMapping("/arhouse.arh/admin/delfurniture/{id}")
    public String postDelFurniture(@PathVariable(value = "id") long id) {
        String admin = "arhouse.arh/admin#shops";
        FurnituresShopModel furnituresShopModel = furnituresShopRepository.findById(id);

        if (furnituresShopModel.getFurnituresModel().getPathToFurniture() != null) {
            File uploadDir = new File(furnituresShopModel.getFurnituresModel().getPathToFurniture());
            boolean wasSuccessful = uploadDir.delete();
            if (wasSuccessful)
                System.out.println("File deleted");
        }
        furnituresShopRepository.delete(furnituresShopModel);
        furnituresRepository.delete(furnituresShopModel.getFurnituresModel());
        return "redirect:/" + admin;
    }
}