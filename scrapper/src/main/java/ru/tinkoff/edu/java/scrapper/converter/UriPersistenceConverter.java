package ru.tinkoff.edu.java.scrapper.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

import java.net.URI;

/**
 * Взято с <a href="https://stackoverflow.com/a/33785032">ответа на Stack Overflow</a>
 */
@Converter
public class UriPersistenceConverter implements AttributeConverter<URI, String> {
    @Override
    public String convertToDatabaseColumn(URI entityValue) {
        return (entityValue == null) ? null : entityValue.toString();
    }

    @Override
    public URI convertToEntityAttribute(String databaseValue) {
        return (StringUtils.hasLength(databaseValue) ? URI.create(databaseValue.trim()) : null);
    }
}
