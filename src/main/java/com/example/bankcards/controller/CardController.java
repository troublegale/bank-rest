package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.ModelDTOConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public PageResponse<Card> getCards(
            @RequestParam(defaultValue = "0") @Valid Integer page,
            @RequestParam(defaultValue = "10") @Valid Integer size,
            @RequestParam(required = false) @Valid List<String> sort,
            @RequestParam(required = false) @Valid Map<String, String> filters
    ) {
        return null;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse createCard(@NotNull @Valid @RequestBody CardRequest cardRequest) {
        Card card = cardService.createCard(cardRequest);
        return ModelDTOConverter.convert(card);
    }

    @GetMapping("/{id}")
    public CardResponse getCard(@PathVariable @Valid Long id, Authentication authentication) {
        Card card = cardService.getCard(id, authentication);
        return ModelDTOConverter.convert(card);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse updateCard(@PathVariable @Valid Long id, @NotNull @Valid @RequestBody CardRequest cardRequest) {
        Card card = cardService.updateCard(id, cardRequest);
        return ModelDTOConverter.convert(card);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCard(@PathVariable @Valid Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponse activateCard(@PathVariable @Valid Long id) {
        Card card = cardService.activateCard(id);
        return ModelDTOConverter.convert(card);
    }

    @PostMapping("/{id}/block-request")
    public ResponseEntity<?> requestCardBlocking(@PathVariable @Valid Long id, Authentication authentication) {
        cardService.requestCardBlocking(id, authentication);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getCardBalance(@PathVariable @Valid Long id, Authentication authentication) {
        return cardService.getCardBalance(id, authentication);
    }

    @GetMapping("/balance")
    public TotalBalanceResponse getTotalBalance(Authentication authentication) {
        List<BalanceResponse> balances = cardService.getCardBalances(authentication);
        BigDecimal totalBalance = balances.stream().map(BalanceResponse::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TotalBalanceResponse(balances, totalBalance);
    }

    @PostMapping("/transfer")
    public Map<String, BalanceResponse> transfer(@NotNull @RequestBody @Valid TransferRequest transferRequest,
                                                 Authentication authentication) {
        return cardService.transfer(transferRequest, authentication);
    }

}
