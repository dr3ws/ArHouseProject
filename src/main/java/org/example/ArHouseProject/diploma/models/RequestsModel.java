package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.validation.constraints.*;
import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Data
public class RequestsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min=2, max=40, message = "Имя должно быть длинее 2 символов")
    private String firstName;
    @Size(min=2, max=40, message = "Фамилия должна быть длинее 2 символов")
    private String secondName;
    @Email(message = "Адрес должен иметь формат адреса электронной почты")
    private String email;
    @NotEmpty(message = "Введите пожалуйста ваш адрес")
    private String address;
    @Size(min=5, max=40, message = "Название должно быть длинее 5 символов")
    private String nameProject; // название объекта
//    @NotEmpty(message = "Загрузите пожалуйста план помещения")
    private String planeProject; // план объекта
    @Temporal(TemporalType.DATE)
    private Date date = new Date();
    private String passwordProject; // пароль заказчика
    private Boolean techTask;
    private String token;

    @OneToOne
    private StatusModel statusModel;
    @OneToOne
    private WorksModel worksModel;

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(this.date);
    }

    public void setDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.date = simpleDateFormat.parse(date);
    }

    public boolean getAuthUsers(Long id, String password_project) {
        return id.equals(this.id) && password_project.equals(this.passwordProject);
    }
}