package com.financetracker.service;

import com.financetracker.dto.request.TransactionRequest;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.dto.response.TransactionResponse;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import com.financetracker.entity.User;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostgresqlTransactionService transactionService;

    @Test
    void createTransactionShouldPersistTransactionForUser() {
        User user = buildUser();
        TransactionRequest request = buildTransactionRequest();
        Transaction savedTransaction = buildTransaction(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse response = transactionService.createTransaction(1L, request);

        assertEquals(10L, response.id());
        assertEquals(new BigDecimal("250.50"), response.amount());
        assertEquals(TransactionType.expense, response.type());
    }

    @Test
    void getTransactionsForUserShouldReturnSortedTransactions() {
        User user = buildUser();
        Transaction transaction = buildTransaction(user);

        when(transactionRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getTransactionsForUser(1L);

        assertEquals(1, responses.size());
        assertEquals(10L, responses.getFirst().id());
    }

    @Test
    void updateTransactionShouldThrowWhenTransactionDoesNotBelongToUser() {
        TransactionRequest request = buildTransactionRequest();
        when(transactionRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> transactionService.updateTransaction(1L, 99L, request));
    }

    @Test
    void deleteTransactionShouldRemoveTransactionForUser() {
        User user = buildUser();
        Transaction transaction = buildTransaction(user);

        when(transactionRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(transaction));

        MessageResponse response = transactionService.deleteTransaction(1L, 10L);

        assertEquals("Deleted", response.message());
        verify(transactionRepository).delete(transaction);
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        return user;
    }

    private TransactionRequest buildTransactionRequest() {
        return new TransactionRequest(
                new BigDecimal("250.50"),
                TransactionType.expense,
                "Food",
                "Lunch",
                LocalDate.of(2026, 6, 27)
        );
    }

    private Transaction buildTransaction(User user) {
        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setUser(user);
        transaction.setAmount(new BigDecimal("250.50"));
        transaction.setType(TransactionType.expense);
        transaction.setCategory("Food");
        transaction.setDescription("Lunch");
        transaction.setDate(LocalDate.of(2026, 6, 27));
        return transaction;
    }
}
