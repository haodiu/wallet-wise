package com.haonguyen.walletwise.service;

import java.util.List;

public interface IBaseService<D, ID> {
    List<D> findAll(Integer pageNo, Integer pageSize, String sortBy);
    D findById(ID id);
    D update(ID id, D dto);
    D save(D dto);
    void delete(ID id);
}
