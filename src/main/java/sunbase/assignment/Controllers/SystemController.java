package sunbase.assignment.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sunbase.assignment.DTOs.request.CustomerRequest;
import sunbase.assignment.DTOs.request.CustomerRequestForEdit;
import sunbase.assignment.DTOs.response.CustomerDTO;
import sunbase.assignment.DTOs.response.CustomerDetails;
import sunbase.assignment.DTOs.response.TokenResponse;
import sunbase.assignment.Services.SystemService;

@RestController
@RequestMapping("/system")
public class SystemController {

    final SystemService systemService;
    @Autowired
    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }
    @GetMapping("/log-in/logInId/{loginId}/password/{password}")
    public ResponseEntity<?> logInUser(@PathVariable("loginId") String logInId, @PathVariable("password") String password){
        try{
            TokenResponse token = systemService.logInUser(logInId, password);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-customer-details/authorization-token/{auth-token}")
    public ResponseEntity<?> getCustomerDetails(@PathVariable("auth-token") String token){
        try{
            CustomerDetails response = systemService.getCustomerDetails(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-customer")
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRequest request){
        try{
            CustomerDTO response = systemService.addCustomer(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-customer/customer-id/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("customerId") String customerId){
        try{
            String response = systemService.deleteCustomer(customerId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-customer")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerRequestForEdit request){
        try{
            String response = systemService.updateCustomer(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
