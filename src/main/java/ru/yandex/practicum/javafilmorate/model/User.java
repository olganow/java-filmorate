package ru.yandex.practicum.javafilmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Objects;

@Data
public class User {
    @NotNull(message = "User Id can't be empty")
    private int id;
    @Email(message = "Email is not valid ")
    @NotNull(message = "Email can't be empty")
    private String email;
    @NotNull(message = "Login can't be empty")
    @NotBlank(message = "Login can't be blank")
    private String login;
    private String name;

    @NotNull(message = "Birthday can't be empty")
    @Past(message = "The birthday has to be before today")
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
