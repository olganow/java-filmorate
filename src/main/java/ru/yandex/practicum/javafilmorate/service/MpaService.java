package ru.yandex.practicum.javafilmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.MpaDao;
import ru.yandex.practicum.javafilmorate.model.Mpa;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaDao MpaStorage;

    public Mpa getMpaById(Integer id) {
        return MpaStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return MpaStorage.getAllMpa();
    }
}
