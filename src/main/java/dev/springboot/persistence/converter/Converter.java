package dev.springboot.persistence.converter;

import dev.springboot.entity.model.BaseEntity;
import dev.springboot.persistence.PersistenceEntity;


public interface Converter
{
    PersistenceEntity convertToEntity(BaseEntity baseEntity);

    <T extends BaseEntity> T convertToModel(PersistenceEntity persistenceEntity, final Class<? extends BaseEntity> CLASS);
}
