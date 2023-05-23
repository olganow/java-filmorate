package ru.yandex.practicum.javafilmorate.dao;

import ru.yandex.practicum.javafilmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();

    void isMpaExisted(int id);
}
