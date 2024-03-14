package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.exception.NotFoundException;
import com.haonguyen.walletwise.model.dto.CategoryDto;
import com.haonguyen.walletwise.model.entity.Category;
import com.haonguyen.walletwise.repository.ICategoryRepository;
import com.haonguyen.walletwise.service.IBaseService;
import com.haonguyen.walletwise.service.IModelMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements IBaseService<CategoryDto, Long>, IModelMapper<CategoryDto, Category> {
    private final ICategoryRepository iCategoryRepository;
    private final ModelMapper modelMapper;
    private final static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public Category getOne(Long id) {
        return iCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Category.class, id));
    }

    @Override
    public List<CategoryDto> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Category> pageResult = iCategoryRepository.findAll(paging);
        if (pageResult.hasContent()) {
            return pageResult.getContent().stream()
                    .map(this::createFromE)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public CategoryDto findById(Long id) {
        return iCategoryRepository.findById(id)
                .map(this::createFromE)
                .orElseThrow(() -> new NotFoundException(Category.class, id));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = iCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Category.class, id));
        dto.setId(id);
        updateEntity(category, dto);
        return createFromE(iCategoryRepository.save(category));
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category category = createFromD(dto);
        return createFromE(iCategoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        iCategoryRepository.deleteById(id);
    }

    @Override
    public Category createFromD(CategoryDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    @Override
    public CategoryDto createFromE(Category entity) {
        return modelMapper.map(entity, CategoryDto.class);
    }

    @Override
    public Category updateEntity(Category entity, CategoryDto dto) {
        modelMapper.map(dto, entity);
        return entity;
    }
}
