package com.example.bankcards.service;

import com.example.bankcards.entity.BlockRequestTicket;
import com.example.bankcards.entity.BlockTicketStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidSearchQueryException;
import com.example.bankcards.exception.TicketAlreadyResolvedException;
import com.example.bankcards.repository.BlockRequestTicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BlockRequestTicketService {

    private final BlockRequestTicketRepository blockRequestTicketRepo;

    private final CardService cardService;

    @Transactional
    public BlockRequestTicket getById(Long id) {
        return blockRequestTicketRepo.findById(id).orElseThrow(() ->
                new NoSuchElementException("BlockRequest with id " + id + " not found"));
    }

    @Transactional
    public BlockRequestTicket approveById(Long id) {
        BlockRequestTicket blockRequestTicket = getById(id);
        if (blockRequestTicket.getStatus() != BlockTicketStatus.PENDING) {
            throw new TicketAlreadyResolvedException("BlockRequest with id " + id + " is already resolved");
        }
        blockRequestTicket.setStatus(BlockTicketStatus.APPROVED);
        cardService.blockCard(blockRequestTicket.getCard());
        return blockRequestTicketRepo.save(blockRequestTicket);
    }

    @Transactional
    public BlockRequestTicket rejectById(Long id) {
        BlockRequestTicket blockRequestTicket = getById(id);
        if (blockRequestTicket.getStatus() != BlockTicketStatus.PENDING) {
            throw new TicketAlreadyResolvedException("BlockRequest with id " + id + " is already resolved");
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

}
