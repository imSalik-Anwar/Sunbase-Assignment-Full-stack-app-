package sunbase.assignment.DTOs.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    String uuid;

    String first_name;

    String last_name;

    String address;

    String city;

    String state;

    String email;

    String phone;
}
