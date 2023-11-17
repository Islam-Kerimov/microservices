package com.kerimovikh.customer;

import com.kerimovikh.clients.fraud.FraudCheckResponse;
import com.kerimovikh.clients.fraud.FraudClient;
import com.kerimovikh.clients.notification.NotificationClient;
import com.kerimovikh.clients.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    public void registrationCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .build();

        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.getIsFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        notificationClient.sendNotification(
            new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                format("Hi %s, welcome to Kerimovikh...", customer.getFirstName()))
        );
    }
}
