package com.arquiweb.management.infrastructure.client;

import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.responses.PaymentRefundRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "payment-client", url = "${app.feign.payment.url}")
public interface PaymentClient {

    @GetMapping("/payment/refunds")
    EventStateResponse refundPayment(@RequestBody PaymentRefundRequest refundRequest);
}
