package ru.tinkoff.edu.java.scrapper.service;

public interface FindOrDoService <O, F> {
    O findOrThrow(F findParams);

    O findOrCreate(F findParams);
}
