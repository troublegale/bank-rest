package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockRequestTicket;
import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BlockRequestTicketRepository extends JpaRepository<BlockRequestTicket, Long>,
        JpaSpecificationExecutor<BlockRequestTicket> {


    boolean existsByCard(Card card);
}
