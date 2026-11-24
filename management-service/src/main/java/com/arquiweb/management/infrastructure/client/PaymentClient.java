package com.arquiweb.management.infrastructure.client;

import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.responses.PaymentRefundRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment-client", url = "${app.feign.payment.url}")
public interface PaymentClient {

    @PostMapping("/payment/{id}/refunds")
    void  refundPayment(@PathVariable UUID id,
                        @RequestHeader("X-User-Role") String serviceRole);

    @PostMapping("/payment/bulk-refund")
    void bulkRefund(@RequestBody List<PaymentRefundRequest> refundRequests,
                    @RequestHeader("X-User-Role") String serviceRole);
}
