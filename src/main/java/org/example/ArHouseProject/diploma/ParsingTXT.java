package org.example.ArHouseProject.diploma;

import org.example.ArHouseProject.diploma.models.FurnituresModel;
import org.example.ArHouseProject.diploma.models.FurnituresProjectModel;
import org.example.ArHouseProject.diploma.models.ProjectsModel;
import org.example.ArHouseProject.diploma.models.RequestsModel;
import org.example.ArHouseProject.diploma.repository.FurnituresProjectRepository;
import org.example.ArHouseProject.diploma.repository.FurnituresRepository;
import org.example.ArHouseProject.diploma.repository.ProjectsRepository;
import org.example.ArHouseProject.diploma.repository.RequestsRepository;

import java.io.InputStream;
import java.util.*;

public class ParsingTXT {
    private final FurnituresProjectRepository furnituresProjectRepository;
    private final FurnituresRepository furnituresRepository;
    private final ProjectsRepository projectsRepository;
    private final RequestsRepository requestsRepository;

    public ParsingTXT(FurnituresProjectRepository furnituresProjectRepository, FurnituresRepository furnituresRepository,
                      ProjectsRepository projectsRepository, RequestsRepository requestsRepository) {
        this.furnituresProjectRepository = furnituresProjectRepository;
        this.furnituresRepository = furnituresRepository;
        this.projectsRepository = projectsRepository;
        this.requestsRepository = requestsRepository;
    }

    public void myListTXT (InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        HashMap<String, Integer> hashMap = new HashMap<>();
        Set<String> key;
        List<String> s = Arrays.asList(scanner.nextLine().split("\\."));
        String nameProjectTXT = s.get(0);
        scanner.nextLine();

        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            String upd_string = str.contains(".") ? str.substring(0, str.indexOf(".")) : str;

            Integer hashMap_count = hashMap.get(upd_string);
            if (hashMap_count == null)
                hashMap.put(upd_string, 1);
            else
                hashMap.replace(upd_string, ++hashMap_count);
        }

        key = hashMap.keySet();
        for (String str: key) {
            FurnituresModel furnitureModel = furnituresRepository.findFurnitureModelById(Long.parseLong(str));
            FurnituresProjectModel furnitureProjectModel = new FurnituresProjectModel();
            furnitureProjectModel.setFurnituresModel(furnitureModel); // добавляем id
            furnitureProjectModel.setCount((long) hashMap.get(str)); // добавляем кол-во

            RequestsModel requestsModel = requestsRepository.findByNameProject(nameProjectTXT);
            ProjectsModel projectsModel = projectsRepository.findProjectsModelsByRequestsModel(requestsModel);
            projectsModel.setApprovedReport(true);
            furnitureProjectModel.setProjectsModel(projectsModel);

            furnituresProjectRepository.save(furnitureProjectModel);
        }
    }
}