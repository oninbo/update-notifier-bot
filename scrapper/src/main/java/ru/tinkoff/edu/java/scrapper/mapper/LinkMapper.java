package ru.tinkoff.edu.java.scrapper.mapper;

import org.mapstruct.Mapper;
import ru.tinkoff.edu.java.scrapper.dto.Link;
import ru.tinkoff.edu.java.scrapper.entity.LinkEntity;

@Mapper
public interface LinkMapper {
    Link fromEntity(LinkEntity linkEntity);
}
