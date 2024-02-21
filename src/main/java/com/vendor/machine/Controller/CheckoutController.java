package com.vendor.machine.Controller;

import com.vendor.machine.DTO.ApiResponse;
import com.vendor.machine.Entity.User;
import com.vendor.machine.Entity.Product;
import com.vendor.machine.Repository.ProductRepository;
import com.vendor.machine.Repository.UserRepository;
import com.vendor.machine.Service.ProductService;
import com.vendor.machine.Service.CheckoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("api/v1/checkout")
@Slf4j
public class CheckoutController {

    private final CheckoutService checkoutsService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Operation(summary = "deposit only accecpt 5, 10, 20, 50, 100 cent coins")
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request , @RequestParam("coins") Long coins) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if(user.getRole().equals("buyer"))
        {
            if (coins != 5 && coins != 10 && coins != 20 && coins != 50 && coins != 100) {
                return new ResponseEntity<>(new ApiResponse<>(false,null,"Invalid coin denomination. Accepted denominations: 5, 10, 20, 50, 100"),HttpStatus.NOT_FOUND);
            }
            user.setDeposit(coins+user.getDeposit());
            userRepository.save(user);
            return new ResponseEntity<>(new ApiResponse<>(true,user.getId(),"Coins Added Successfully"), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Not a buyer to deposit coins"),HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "deposit only accecpt 5, 10, 20, 50, 100 cent coins")
    @PostMapping("/buy")
    public ResponseEntity<?> buy(HttpServletRequest request , @RequestParam("productId") Long productId,
        @RequestParam("amount") Long amount) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        if(user.getRole().equals("buyer"))
        {
            if(product.getAmountAvailable()<amount)
            {
                return new ResponseEntity<>(new ApiResponse<>(false,null,"the required amount not availabe in store"),HttpStatus.NOT_FOUND);
            }
            Long totalCost = checkoutsService.calculateTotalCost(product, amount);
            if (user.getDeposit() < totalCost) {
                return new ResponseEntity<>(new ApiResponse<>(false,null,"Insufficient funds"),HttpStatus.NOT_FOUND);
            }
            user.setDeposit(user.getDeposit() - totalCost);
            userRepository.save(user);

            Map<Long, Long> change = checkoutsService.generateChange(user.getDeposit());

            Map<String, Object> response = new HashMap<>();
            response.put("totalSpent", totalCost);
            response.put("Product Name", product.getProductName());
            response.put("change", change);

            return new ResponseEntity<>(new ApiResponse<>(true,response,"Success checkout process"),HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Not a buyer to deposit coins"),HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "reset the deposit of the logged in buyer role")
    @PostMapping("/reset")
    public ResponseEntity<?> reset(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if(user.getRole().equals("buyer"))
        {
            user.setDeposit(0L);
            userRepository.save(user);
            return new ResponseEntity<>(new ApiResponse<>(true,user.getId(),"Request to reset deopsit succeeded"), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Not a buyer to reset coins"),HttpStatus.NOT_FOUND);
        }
    }
}
