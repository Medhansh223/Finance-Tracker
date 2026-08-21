package com.financetracker.service;

import com.financetracker.dto.request.TransactionRequest;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(Long userId, TransactionRequest request);
    List<TransactionResponse> getTransactionsForUser(Long userId);
    TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request);
    MessageResponse deleteTransaction(Long userId, Long transactionId);
}
