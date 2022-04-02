package org.example.ArHouseProject.diploma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Value("PathFilesPlane.path") // /images/UploadFiles
//    @Value("uploadFilesPlane.path") // F:/diploma/ArHouseProject/src/main/resources/static/images/UploadFiles
    private String file;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/arhouse.arh/authorization")
                .setViewName("authorization/authorization");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("../images/UploadFiles/**")
                .addResourceLocations("file://" + file + "/");
    }
}