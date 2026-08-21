package com.financetracker.service;

import com.financetracker.constant.Constants;
import com.financetracker.dto.request.TransactionRequest;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.dto.response.TransactionResponse;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostgresqlTransactionService implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(PostgresqlTransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public PostgresqlTransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        User user = findUserById(userId);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        applyRequestToTransaction(transaction, request);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info(Constants.TRANSACTION_CREATED, savedTransaction.getId(), userId);
        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsForUser(Long userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = findTransactionForUser(userId, transactionId);
        applyRequestToTransaction(transaction, request);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        logger.info(Constants.TRANSACTION_UPDATED, transactionId, userId);
        return TransactionResponse.fromEntity(updatedTransaction);
    }

    @Override
    @Transactional
    public MessageResponse deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = findTransactionForUser(userId, transactionId);
        transactionRepository.delete(transaction);
        logger.info(Constants.TRANSACTION_DELETED, transactionId, userId);
        return new MessageResponse("Deleted");
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private Transaction findTransactionForUser(Long userId, Long transactionId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> {
                    logger.warn(Constants.TRANSACTION_NOT_FOUND, transactionId, userId);
                    return new NoSuchElementException("Transaction not found");
                });
    }

    private void applyRequestToTransaction(Transaction transaction, TransactionRequest request) {
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCategory(request.category());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
    }
}
