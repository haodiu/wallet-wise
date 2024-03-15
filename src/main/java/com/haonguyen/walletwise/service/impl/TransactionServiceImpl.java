package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.exception.NotFoundException;
import com.haonguyen.walletwise.model.dto.TokenDto;
import com.haonguyen.walletwise.model.dto.TotalAmountDto;
import com.haonguyen.walletwise.model.dto.TransactionDto;
import com.haonguyen.walletwise.model.entity.Category;
import com.haonguyen.walletwise.model.entity.Transaction;
import com.haonguyen.walletwise.repository.ITransactionRepository;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements IBaseService<TransactionDto, Long>, IModelMapper<TransactionDto, Transaction> {
    private final ITransactionRepository iTransactionRepository;
    private final CategoryServiceImpl categoryServiceImpl;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public List<TransactionDto> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Transaction> pagedResult = iTransactionRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent().stream()
                    .map(this::createFromE)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public TransactionDto findById(Long id) {
        Transaction transaction = iTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Transaction.class, id));
        return createFromE(transaction);

    }

    @Override
    public TransactionDto update(Long id, TransactionDto dto) {
        Transaction transaction = iTransactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Transaction.class, id));
        updateEntity(transaction, dto);
        transaction.setId(id);
        return createFromE(iTransactionRepository.save(transaction));
    }

    @Override
    public TransactionDto save(TransactionDto dto) {
        Transaction transaction = createFromD(dto);
        Long categoryId = dto.getCategoryId();
        Category category = categoryServiceImpl.getOne(categoryId);
        transaction.setCategory(category);
        return createFromE(iTransactionRepository.save(transaction));
    }

    @Override
    public void delete(Long id) {
        iTransactionRepository.deleteById(id);
    }

    @Override
    public Transaction createFromD(TransactionDto dto) {
        return modelMapper.map(dto, Transaction.class);
    }

    @Override
    public TransactionDto createFromE(Transaction entity) {
        TransactionDto dto = modelMapper.map(entity, TransactionDto.class);
        dto.setCategoryId(entity.getId());
        return dto;
    }

    @Override
    public Transaction updateEntity(Transaction entity, TransactionDto dto) {
        // Skip null fields when mapping
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, entity);
        return entity;
    }

//    public List<TotalAmountDto> computeTotalAmount(Date start, Date end) {
//        logger.error(start.toString());
//        List<Object[]> result = iTransactionRepository.getTotalAmountByDateRange(start, end);
//        return result.stream()
//                .map(this::createDtoFromObject)
//                .collect(Collectors.toList());
//    }

    private TotalAmountDto createDtoFromObject(Object[] objArray) {
        TotalAmountDto dto = new TotalAmountDto();
        dto.setCategoryId((Long) objArray[0]);
        dto.setAmount((Long) objArray[1]);
        return dto;
    }

}
