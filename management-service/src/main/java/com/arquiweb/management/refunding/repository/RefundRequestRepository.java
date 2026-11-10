package com.arquiweb.management.refunding.repository;

import com.arquiweb.management.refunding.domain.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID> {
}