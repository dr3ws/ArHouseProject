package org.example.ArHouseProject.diploma.models;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Data
public class OrganizationsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Введите организацию")
    private String nameOrganization;
    @NotEmpty(message = "Введите адрес")
    private String urAddress;
    @Size(min=10, max=12, message = "ИНН должен содержать у физических лиц 12 цифр, у юридических лиц 10")
    private String inn;
    @Size(min=13, max=13, message = "ОГРН должен содержать 13 цифр")
    private String ogrn;
    @NotEmpty(message = "Введите реквизиты")
    private String bankDetails;
    private String telephone;
    @Past(message = "Укажите дату не позднее текущей")
    @Temporal(TemporalType.DATE)
    private Date dateRegistration = new Date();

    public String getDateRegistration() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(this.dateRegistration);
    }

    public void setDateRegistration(String dateregistration) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.dateRegistration = simpleDateFormat.parse(dateregistration);
    }
}