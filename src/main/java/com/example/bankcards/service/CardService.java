package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardConflictException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.UserService;
import com.example.bankcards.util.ModelDTOConverter;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final UserService userService;
    private final BlockRequestTicketService blockRequestTicketService;

    private Card findById(Long id) {
        return cardRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Card with id " + id + " not found."));
    }

    @Transactional
    public Card getCard(Long id, Authentication authentication) {
        Card card = findById(id);
        if (checkOwnershipMismatch(card, authentication)) {
            throw new AccessDeniedException("You don't have access to card with id " + id);
        }
        return card;
    }

    @Transactional
    public Card createCard(CardRequest cardRequest) {
        User user = userService.getById(cardRequest.userId());
        if (cardRepository.existsByNumber(cardRequest.number())) {
            throw new CardConflictException("Card with number " + cardRequest.number() + " already exists.");
        }
        Card card = ModelDTOConverter.convert(cardRequest);
        card.setUser(user);
        return cardRepository.save(card);
    }

    @Transactional
    public Card updateCard(Long id, CardRequest cardRequest) {
        Card card = findById(id);
        User user = userService.getById(cardRequest.userId());
        if (cardRepository.existsByNumber(cardRequest.number())) {
            throw new CardConflictException("Card with number " + cardRequest.number() + " already exists.");

        }
        card.setUser(user);
        card.setNumber(cardRequest.number());
        card.setCardholderName(cardRequest.cardholderName());
        short year = cardRequest.expirationYear();
        short month = cardRequest.expirationMonth();
        card.setExpirationYear(year);
        card.setExpirationMonth(month);
        if (card.getStatus() == CardStatus.ACTIVE && isExpired(year, month)) {
            card.setStatus(CardStatus.EXPIRED);
        } else if (card.getStatus() == CardStatus.EXPIRED && !isExpired(year, month)) {
            card.setStatus(CardStatus.ACTIVE);
        }
        return cardRepository.save(card);
    }

    private boolean isExpired(int year, int month) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        return (currentYear > year) || (currentYear == year && currentMonth > month);
    }

    @Transactional
    public void deleteCard(Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
        } else {
            throw new NoSuchElementException(String.format("Card with id %d not found", id));
        }
    }

    @Transactional
    public Card activateCard(Long id) {
        Card card = findById(id);
        if (card.getStatus() != CardStatus.CREATED) {
            throw new CardConflictException("Card with id " + id + " has already been activated.");
        }
        if (isExpired(card.getExpirationYear(), card.getExpirationMonth())) {
            card.setStatus(CardStatus.EXPIRED);
        } else {
            card.setStatus(CardStatus.ACTIVE);
        }
        return cardRepository.save(card);
    }

    @Transactional
    public void requestCardBlocking(Long id, Authentication authentication) {
        Card card = findById(id);
        if (checkOwnershipMismatch(card, authentication)) {
            throw new AccessDeniedException("You don't have access to card with id " + id);
        }
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardConflictException("Card with id " + id + " is already blocked.");
        }
        blockRequestTicketService.createBlockRequest(card);
    }

    private boolean checkOwnershipMismatch(Card card, Authentication authentication) {
        String ownerEmail = card.getUser().getEmail();
        List<String> roles = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
        return !ownerEmail.equals(authentication.getName()) && !roles.contains("ROLE_ADMIN");
    }

    @Transactional
    public BalanceResponse getCardBalance(Long id, Authentication authentication) {
        Card card = findById(id);
        if (!card.getUser().getEmail().equals(authentication.getName())) {
            throw new AccessDeniedException("You are not allowed to see the balance of card with id " + id);
        }
        return new BalanceResponse(maskedNumber(card), card.getBalance());
    }

    @Transactional
    public List<BalanceResponse> getCardBalances(Authentication authentication) {
        Collection<Card> cards = cardRepository.findCardsByUser_Email(authentication.getName());
        if (cards.isEmpty()) {
            throw new NoSuchElementException("Not found any cards owned by user.");
        }
        List<BalanceResponse> balances = new ArrayList<>();
        cards.forEach(card -> balances.add(new BalanceResponse(maskedNumber(card), card.getBalance())));
        return balances;
    }

    @Transactional
    public Map<String, BalanceResponse> transfer(TransferRequest request, Authentication authentication) {
        Card fromCard = findById(request.fromId());
        Card toCard = findById(request.toId());
        if (!fromCard.getUser().getEmail().equals(authentication.getName())
        || !toCard.getUser().getEmail().equals(authentication.getName())) {
            throw new AccessDeniedException("Transfers are allowed only between owned cards.");
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE ||  toCard.getStatus() != CardStatus.ACTIVE) {
            throw new TransferException("One or both cards are not active.");
        }
        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new TransferException("Insufficient balance.");
        }
        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        BalanceResponse from = new BalanceResponse(maskedNumber(fromCard), fromCard.getBalance());
        BalanceResponse to = new BalanceResponse(maskedNumber(toCard), toCard.getBalance());
        return Map.of("fromCard", from, "toCard", to);
    }

    private String maskedNumber(Card card) {
        return "**** **** **** " + card.getLast4();
    }

}
