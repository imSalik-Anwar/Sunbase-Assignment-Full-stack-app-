package sunbase.assignment.DTOs.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestForEdit {
    String uuid;

    String firstName;

    String lastName;

    String street;

    String address;

    String city;

    String state;

    String email;

    String phone;
}
