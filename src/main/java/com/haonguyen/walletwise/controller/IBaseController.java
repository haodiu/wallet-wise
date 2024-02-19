package com.haonguyen.walletwise.controller;

import com.haonguyen.walletwise.service.IBaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IBaseController<D, ID, S extends IBaseService<D, ID>> {
    S getService();

    @GetMapping("/{id}")
    default D get(@PathVariable ID id) {
        return getService().findById(id);
    }

    @GetMapping("")
    default List<D> getList() {
        return getService().findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    default D insert(@Valid @RequestBody D dto) {
        return getService().save(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    default D update(@Valid @PathVariable ID id, @RequestBody D dto) {
        return getService().update(id, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    default void delete(@PathVariable ID id) {
        getService().delete(id);
    }
}
