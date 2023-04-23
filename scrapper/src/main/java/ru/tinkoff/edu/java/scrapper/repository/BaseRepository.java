package ru.tinkoff.edu.java.scrapper.repository;

import java.util.List;
import java.util.UUID;

interface BaseRepository <ENTITY, ADD_PARAMS>{
    ENTITY add(ADD_PARAMS addParams);
    List<ENTITY> findAll();
    void remove(UUID id);
}
