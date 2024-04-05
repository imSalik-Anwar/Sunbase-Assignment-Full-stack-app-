package sunbase.assignment.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customer_table")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {

    @Id
    String uuid;

    String first_name;

    String last_name;

    String address;

    String city;

    String state;

    String email;

    String phone;
}
