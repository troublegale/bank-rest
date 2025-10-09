package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockRequestTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BlockRequestTicketRepository extends JpaRepository<BlockRequestTicket, Long>,
        JpaSpecificationExecutor<BlockRequestTicket> {



}
