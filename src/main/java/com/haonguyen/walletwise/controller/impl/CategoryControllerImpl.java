package com.haonguyen.walletwise.controller.impl;

import com.haonguyen.walletwise.controller.IBaseController;
import com.haonguyen.walletwise.model.dto.CategoryDto;
import com.haonguyen.walletwise.service.impl.CategoryServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryControllerImpl implements IBaseController<CategoryDto, Long, CategoryServiceImpl> {
    @Resource
    private CategoryServiceImpl categoryService;

    @Override
    public CategoryServiceImpl getService() {
        return this.categoryService;
    }
}
