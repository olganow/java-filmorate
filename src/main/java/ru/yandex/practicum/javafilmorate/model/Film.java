package ru.yandex.practicum.javafilmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    @NotNull(message = "Film Id can't be empty")
    private int id;
    @NotNull(message = "Film name can't be empty")
    @NotBlank(message = "Film name can't be blank")
    @NotEmpty(message = "Film name can't be empty")
    private String name;
    @Size(max = 200, message = "Film description has to be less than 200 symbols")
    @NotNull(message = "Film description can't be empty")
    private String description;

    @NotNull(message = "Film releaseDate can't be empty")
    @Past(message = "The release date has to be before today")
    private LocalDate releaseDate;
    @NotNull(message = "Film duration can't be empty")
    @Positive(message = "Film duration can't be positive")
    private Integer duration;

    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}