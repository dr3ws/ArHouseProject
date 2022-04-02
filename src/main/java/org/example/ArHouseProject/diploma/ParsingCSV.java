package org.example.ArHouseProject.diploma;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.example.ArHouseProject.diploma.models.FurnituresShopModel;
import org.example.ArHouseProject.diploma.repository.CatigoriesRepository;
import org.example.ArHouseProject.diploma.repository.FurnituresRepository;
import org.example.ArHouseProject.diploma.repository.FurnituresShopRepository;
import org.example.ArHouseProject.diploma.repository.ShopsRepository;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ParsingCSV {
    private final CatigoriesRepository catigoriesRepository;
    private final FurnituresShopRepository furnituresShopRepository;
    private final FurnituresRepository furnituresRepository;
    private final ShopsRepository shopsRepository;

    public ParsingCSV(CatigoriesRepository catigoriesRepository, FurnituresShopRepository furnituresShopRepository,
                      FurnituresRepository furnituresRepository, ShopsRepository shopsRepository) {
        this.catigoriesRepository = catigoriesRepository;
        this.furnituresShopRepository = furnituresShopRepository;
        this.furnituresRepository = furnituresRepository;
        this.shopsRepository = shopsRepository;
    }

    public void inputStreamListCSV (InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        scanner.nextLine();

        while (scanner.hasNext()) {
            List<String> buf = Arrays.asList(scanner.nextLine().split(";"));

            FurnituresModel furnitureModel = new FurnituresModel();
            FurnituresShopModel currentFurniture;

            System.out.println(buf.get(6));

            furnitureModel.setArticul(buf.get(0));
            furnitureModel.setNameFurniture(buf.get(1));
            furnitureModel.setCategoriesModel(catigoriesRepository.findCategoriesModelByCategoriesAndParentCategories(buf.get(2), buf.get(3)));

            FurnituresModel chekModel = furnituresRepository.findFurnitureModelByArticul(furnitureModel.getArticul());

            if(chekModel == null)
                chekModel = furnituresRepository.save(furnitureModel);

            currentFurniture = furnituresShopRepository.findFurnituresShopModelByFurnituresModelAndShopsModel(chekModel,
                    shopsRepository.findByNameShop(buf.get(6)));

            if (currentFurniture == null)
                currentFurniture = new FurnituresShopModel();

            currentFurniture.setFurnituresModel(chekModel);
            currentFurniture.setCount(Long.valueOf(buf.get(4)));
            currentFurniture.setPrice(Long.valueOf(buf.get(5)));

            currentFurniture.setTotalPrice(Long.parseLong(buf.get(4)) * Long.parseLong(buf.get(5)));
            currentFurniture.setShopsModel(shopsRepository.findByNameShop(buf.get(6)));
            furnituresShopRepository.save(currentFurniture);
        }
        System.out.println("Мебель успешно загружена!");
    }
}