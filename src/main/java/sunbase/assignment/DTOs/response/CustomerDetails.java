package sunbase.assignment.DTOs.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetails {
    List<CustomerDTO> customers = new ArrayList<>();
}
