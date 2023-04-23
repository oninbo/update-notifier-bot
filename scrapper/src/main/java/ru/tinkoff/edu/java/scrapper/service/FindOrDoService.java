package ru.tinkoff.edu.java.scrapper.service;

public interface FindOrDoService <O, F> {
    O findOrCreate(F findParams);
}
