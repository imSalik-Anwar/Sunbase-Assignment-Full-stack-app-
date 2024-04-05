package sunbase.assignment.Utility;

import sunbase.assignment.DTOs.request.CustomerRequest;
import sunbase.assignment.DTOs.response.CustomerDTO;
import sunbase.assignment.models.Customer;

import java.util.UUID;

public class Converter {

    public static Customer fromCustomerDTOtoCustomer(CustomerDTO obj){
        return Customer.builder()
                .uuid(obj.getUuid())
                .first_name(obj.getFirst_name())
                .last_name(obj.getLast_name())
                .address(obj.getAddress())
                .city(obj.getCity())
                .state(obj.getState())
                .email(obj.getEmail())
                .phone(obj.getPhone())
                .build();
    }

    public static CustomerDTO fromCustomertoCustomerDTO(Customer obj) {
        return CustomerDTO.builder()
                .uuid(obj.getUuid())
                .first_name(obj.getFirst_name())
                .last_name(obj.getLast_name())
                .address(obj.getAddress())
                .city(obj.getCity())
                .state(obj.getState())
                .email(obj.getEmail())
                .phone(obj.getPhone())
                .build();
    }

    public static Customer fromCustomerRequestToCustomer(CustomerRequest request) {
        return Customer.builder()
                .uuid(String.valueOf(UUID.randomUUID()))
                .first_name(request.getFirstName())
                .last_name(request.getLastName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
    }
}
