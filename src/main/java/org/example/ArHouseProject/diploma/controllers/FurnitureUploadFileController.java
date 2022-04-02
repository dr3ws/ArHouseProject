package org.example.ArHouseProject.diploma.controllers;

import org.example.ArHouseProject.diploma.ParsingCSV;
import org.example.ArHouseProject.diploma.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Controller
public class FurnitureUploadFileController {
    private final CatigoriesRepository catigoriesRepository;
    private final FurnituresShopRepository furnituresShopRepository;
    private final FurnituresRepository furnituresRepository;
    private final ShopsRepository shopsRepository;

    public FurnitureUploadFileController(CatigoriesRepository catigoriesRepository, FurnituresShopRepository furnituresShopRepository,
                                         FurnituresRepository furnituresRepository, ShopsRepository shopsRepository) {
        this.catigoriesRepository = catigoriesRepository;
        this.furnituresShopRepository = furnituresShopRepository;
        this.furnituresRepository = furnituresRepository;
        this.shopsRepository = shopsRepository;
    }

    @GetMapping("/arhouse.arh/admin/addfurniture/uploaddata")
    public String getUpLoadFurniture() {
        return "admins/shops/uploadfurniture";
    }

    @PostMapping("/arhouse.arh/admin/addfurniture/uploaddata")
    public String PostUpLoadFurniture(@RequestParam("file") MultipartFile multipartFile) throws IOException
    {
        String admin = "arhouse.arh/admin#shopsfurniture", uploaddata = "arhouse.arh/admin/addfurniture/uploaddata";

        if (Objects.equals(multipartFile.getOriginalFilename(), "")) {
            System.out.println("Нет файла");
            return "redirect:/" + uploaddata;
        } else {
            ParsingCSV parsingCSV = new ParsingCSV(catigoriesRepository, furnituresShopRepository, furnituresRepository, shopsRepository);
            parsingCSV.inputStreamListCSV(multipartFile.getInputStream());
            return "redirect:/" + admin;
        }
    }
}