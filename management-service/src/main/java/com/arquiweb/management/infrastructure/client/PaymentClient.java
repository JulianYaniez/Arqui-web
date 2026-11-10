package com.arquiweb.management.infrastructure.client;

import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.responses.PaymentRefundRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment-client", url = "${app.feign.payment.url}")
public interface PaymentClient {

    @PostMapping("/{id}/refunds")
    void  refundPayment(@PathVariable UUID id);

    @PostMapping("/payment/bulk-refund")
    void bulkRefund(@RequestBody List<PaymentRefundRequest> refundRequests);
}
