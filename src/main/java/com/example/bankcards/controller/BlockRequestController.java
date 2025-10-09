package com.example.bankcards.controller;

import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.BlockRequestTicket;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.BlockRequestTicketService;
import com.example.bankcards.util.ModelDTOConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/block-requests")
@RequiredArgsConstructor
public class BlockRequestController {

    private final BlockRequestTicketService  blockRequestTicketService;

    @GetMapping
    public PageResponse<BlockRequestTicket> getBlockRequests(
            @RequestParam(defaultValue = "0") @Valid Integer page,
            @RequestParam(defaultValue = "10") @Valid Integer size,
            @RequestParam(defaultValue = "true") @Valid Boolean pending) {
        Page<BlockRequestTicket> blockRequestsPage = blockRequestTicketService
                .getBlockRequestTickets(page, size, pending);
        List<BlockRequestTicket> items = blockRequestsPage.getContent();
        return new PageResponse<>(
                items,
                page,
                size,
                blockRequestsPage.getTotalElements(),
                blockRequestsPage.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public BlockRequestTicket getBlockRequest(@PathVariable @Valid Long id) {
        return blockRequestTicketService.getById(id);
    }

    @PostMapping("/{id}")
    public BlockRequestTicket approveBlockRequest(@PathVariable @Valid Long id) {
        return blockRequestTicketService.approveById(id);
    }

    @DeleteMapping("/{id}")
    public BlockRequestTicket rejectBlockRequest(@PathVariable @Valid Long id) {
        return blockRequestTicketService.rejectById(id);
    }

}
