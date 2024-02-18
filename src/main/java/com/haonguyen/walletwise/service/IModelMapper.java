package com.haonguyen.walletwise.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface IModelMapper<D, E> {
    E createFromD(D dto);
    D createFromE(E entity);
    void updateEntity(E entity, D dto);

    default List<D> createFromEntities(final Collection<E> entities) {
        return entities.stream()
                .map(this::createFromE)
                .collect(Collectors.toList());
    }
}
