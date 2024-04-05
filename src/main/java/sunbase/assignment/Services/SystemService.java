package sunbase.assignment.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sunbase.assignment.DTOs.request.CustomerRequest;
import sunbase.assignment.DTOs.request.CustomerRequestForEdit;
import sunbase.assignment.DTOs.response.CustomerDTO;
import sunbase.assignment.DTOs.response.CustomerDetails;
import sunbase.assignment.DTOs.response.TokenResponse;
import sunbase.assignment.Exception.CustomerNotFoundException;
import sunbase.assignment.Repositories.CustomerRepository;
import sunbase.assignment.Utility.Converter;
import sunbase.assignment.models.Customer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class SystemService {

    /**
     * Injecting dependencies through constructor.
     * Constructor injections facilitates better unit testing and avoids error
     *
     * Dependencies:
     * RestTemplate for making remote API calls to sunbasedata.com
     * CustomerRepository for storing data on local server and performing CRUD operations
     */
    final RestTemplate restTemplate;
    final CustomerRepository customerRepository;
    @Autowired
    public SystemService(RestTemplate restTemplate, CustomerRepository customerRepository) {
        this.restTemplate = restTemplate;
        this.customerRepository = customerRepository;
    }

    /**
     * Takes login id and password as user input on the client side,
     * forwards a remote API call to qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp
     * for generating authorization token.
     * @param logInId
     * @param password
     * @return Authorization token to the client side to use the token for further API calls
     * @throws Exception
     */
    public TokenResponse logInUser(String logInId, String password) throws Exception {
        try {
            // make API call to sunbase.com for generating auth token
            String sunbaseDataGetTokenURL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = "{\"login_id\": \"" + logInId + "\", \"password\": \"" + password + "\"}";
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sunbaseDataGetTokenURL, request, String.class);
            // extract token from the response
            String token = Objects.requireNonNull(response.getBody()).substring(19, request.getBody().length() - 1);
            System.out.println(token);
            // send token to the client
            return new TokenResponse(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetches customer details from sunbase.data
     * @param token: Takes auth token generated in the last API call
     * @return an object of CustomerDetails class which consists a list of CustometDTO objects
     */
    public CustomerDetails getCustomerDetails(String token) {
        String sunbaseDataGetCustomerDetailsURL = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";
        try {
            // Prepare headers with the Bearer token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // Create the request entity with headers
            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(sunbaseDataGetCustomerDetailsURL));

            // Send the request and receive the response
            CustomerDTO[] response = restTemplate.exchange(requestEntity, CustomerDTO[].class).getBody();

            //sync the fetched data with database
            for (CustomerDTO c : response){
                // make a new customer and store it in DB
                Customer customer = Converter.fromCustomerDTOtoCustomer(c);
                customerRepository.save(customer);
            }
            // get all the customer data from database and send to client
            List<Customer> customerList = customerRepository.findAll();
            CustomerDetails customerDetails = new CustomerDetails();
            Set<String> set = new HashSet<>();
            for(Customer customer : customerList){
                if(!set.contains(customer.getUuid())) {
                    set.add(customer.getUuid());
                    // make a customer DTO for client side
                    CustomerDTO customerDTO = Converter.fromCustomertoCustomerDTO(customer);
                    // store it in customerDetails to send to the client side
                    customerDetails.getCustomers().add(customerDTO);
                }
            }
            return customerDetails;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            // Handle URI syntax exception
            return null;
        }
    }

    /**
     * adds new customer to database
     * @param request: takes an object of CustomerRequest DTO as input, extracts values from it and saves into
     *               a Customer clas object. Saves Customer class object inti DB.
     * @return: returns CustomerDTO object to send customer data to the client to render on frontend
     */
    public CustomerDTO addCustomer(CustomerRequest request) {
        // convert request DTO into Customer object
        Customer customer = Converter.fromCustomerRequestToCustomer(request);
        // save customer object in DB
        Customer savedCustomer = customerRepository.save(customer);
        // prepare a Customer DTO object from saved customer to send to the client
        return Converter.fromCustomertoCustomerDTO(savedCustomer);
    }

    /**
     * Deletes a customer from the DB
     * @param customerId: takes unique UUID which is primary key to identify the targeted object
     * @return a success message
     */
    public String deleteCustomer(String customerId) {
        // check if customer exists in the database
        Optional<Customer> customerOptional = customerRepository.findByUuid(customerId);
        if(customerOptional.isEmpty()){
            throw new CustomerNotFoundException("Customer does not exists.");
        }
        // if customer exists, delete this customer from DB
        customerRepository.delete(customerOptional.get());
        // return success message
        return "Customer was deleted.";
    }

    /**
     * Updates existing customer's data in DB
     * @param request: takes CustomerRequestForEdit object as DTO which carries data-to-be-updated
     * @return a success message of String type
     */
    public String updateCustomer(CustomerRequestForEdit request) {
        // check if customer exists in the database
        Optional<Customer> customerOptional = customerRepository.findByUuid(request.getUuid());
        if(customerOptional.isEmpty()){
            throw new CustomerNotFoundException("Customer does not exists.");
        }
        Customer customer = customerOptional.get();
        // update customer
        customer.setFirst_name(request.getFirstName());
        customer.setLast_name(request.getLastName());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        // save updated customer
        customerRepository.save(customer);

        return "Customer details were updated";
    }
}
