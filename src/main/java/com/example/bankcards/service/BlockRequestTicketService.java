package com.example.bankcards.service;

import com.example.bankcards.entity.BlockRequestTicket;
import com.example.bankcards.entity.BlockTicketStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.InvalidSearchQueryException;
import com.example.bankcards.exception.TicketConflictException;
import com.example.bankcards.repository.BlockRequestTicketRepository;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BlockRequestTicketService {

    private final BlockRequestTicketRepository blockRequestTicketRepo;

    private final CardRepository cardRepository;

    @Transactional
    public BlockRequestTicket getById(Long id) {
        return blockRequestTicketRepo.findById(id).orElseThrow(() ->
                new NoSuchElementException("BlockRequest with id " + id + " not found"));
    }

    @Transactional
    public BlockRequestTicket approveById(Long id) {
        BlockRequestTicket blockRequestTicket = getById(id);
        if (blockRequestTicket.getStatus() != BlockTicketStatus.PENDING) {
            throw new TicketConflictException("BlockRequest with id " + id + " is already resolved");
        }
        blockRequestTicket.setStatus(BlockTicketStatus.APPROVED);
        Card card = blockRequestTicket.getCard();
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return blockRequestTicketRepo.save(blockRequestTicket);
    }

    @Transactional
    public BlockRequestTicket rejectById(Long id) {
        BlockRequestTicket blockRequestTicket = getById(id);
        if (blockRequestTicket.getStatus() != BlockTicketStatus.PENDING) {
            throw new TicketConflictException("BlockRequest with id " + id + " is already resolved");
        }
        blockRequestTicket.setStatus(BlockTicketStatus.REJECTED);
        return  blockRequestTicketRepo.save(blockRequestTicket);
    }

    @Transactional
    public Page<BlockRequestTicket> getBlockRequestTickets(Integer page, Integer size, Boolean pending) {
        try {
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            Specification<BlockRequestTicket> spec = Specification.unrestricted();
            if (pending) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("status"), BlockTicketStatus.PENDING));
            }
            return blockRequestTicketRepo.findAll(spec, pageable);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InvalidSearchQueryException("Invalid filters or sorting parameters");
        }
    }

    @Transactional
    public void createBlockRequest(Card card) {
        if (blockRequestTicketRepo.existsByCard(card)) {
            throw new TicketConflictException("Blocking for card with id " + card.getId() + " was already requested.");
        }
        BlockRequestTicket blockRequestTicket = BlockRequestTicket.builder()
                .card(card)
                .timestamp(LocalDateTime.now())
                .status(BlockTicketStatus.PENDING)
                .build();
        blockRequestTicketRepo.save(blockRequestTicket);
    }

}
