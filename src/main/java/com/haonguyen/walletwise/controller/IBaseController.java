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
    default List<D> getList(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return getService().findAll(pageNo, pageSize, sortBy);
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
