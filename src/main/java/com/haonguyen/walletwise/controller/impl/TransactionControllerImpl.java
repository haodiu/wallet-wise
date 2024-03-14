package com.haonguyen.walletwise.controller.impl;

import com.haonguyen.walletwise.controller.IBaseController;
import com.haonguyen.walletwise.model.dto.TransactionDto;
import com.haonguyen.walletwise.service.impl.TransactionServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionControllerImpl implements IBaseController<TransactionDto, Long, TransactionServiceImpl> {
    @Resource
    private TransactionServiceImpl transactionService;

    @Override
    public TransactionServiceImpl getService() {
        return this.transactionService;
    }
}
