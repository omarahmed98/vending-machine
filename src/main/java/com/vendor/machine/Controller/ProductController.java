package com.vendor.machine.Controller;

import com.vendor.machine.DTO.ApiResponse;
import com.vendor.machine.DTO.UpdateProductDTO;
import com.vendor.machine.DTO.AddProductDTO;
import com.vendor.machine.Entity.Product;
import com.vendor.machine.Entity.User;
import com.vendor.machine.Mapper.ProductMapper;
import com.vendor.machine.Repository.ProductRepository;
import com.vendor.machine.Repository.UserRepository;
import com.vendor.machine.Service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;


@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("api/v1/product")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Operation(summary = "get all products")
    @GetMapping("")
    public ResponseEntity<?> getProducts() {
        List<UpdateProductDTO> products = productService.getallProducts();
        return new ResponseEntity<>(new ApiResponse<>(true,products,"Products Retreived Successfully"),HttpStatus.OK);
    }

    @Operation(summary = "get product by id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductDetails(@PathVariable("id") Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if(product == null)
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Product Not found"),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>(true,product,"Product Retreived Successfully"),HttpStatus.OK);
    }

    @Operation(summary = "add product to list of products")
    @PostMapping("")
    public ResponseEntity<?> addProduct(HttpServletRequest request ,@RequestBody AddProductDTO addProductDTO) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if(user.getRole().equals("seller"))
        {
            Product productSaved = productMapper.toAddProduct(addProductDTO);
            Product savedProduct = productService.saveProduct(request,productSaved);
            return new ResponseEntity<>(new ApiResponse<>(true,savedProduct.getId(),"Product Added Successfully"),HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Not a seller to sell product"),HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update data of product added by logged user")
    @PutMapping("")
    public ResponseEntity<?> updateUserData(HttpServletRequest request, @RequestBody UpdateProductDTO product) {
            Principal principal = request.getUserPrincipal();
            String username = principal.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            Long userId = user.getId();
            Product productSeller = productRepository.findById(product.getId()).orElse(null);
            if(productSeller == null)
            {
                return new ResponseEntity<>(new ApiResponse<>(false,null,"Wrong Product Id"),HttpStatus.NOT_FOUND);
            }
            Long sellerId = productSeller.getSellerId();

            if(userId.equals(sellerId))
            {
                int response = productService.updateProduct(product);
                if(response!=0)
                {
                    return new ResponseEntity<>(new ApiResponse<>(true,product.getId(),"Product Updated Successfully"),HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>(new ApiResponse<>(false,null,"No rows affected"),HttpStatus.NOT_ACCEPTABLE);
                }
            }
            else {
                return new ResponseEntity<>(new ApiResponse<>(false,null,"Not a product of logged user"),HttpStatus.NOT_ACCEPTABLE);
            }
    }

    @Operation(summary = "delete the product of logged user using the id of the product")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @PathVariable("id") Long id){
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        Long userId = user.getId();
        Product productSeller = productRepository.findById(id).orElse(null);
        if(productSeller == null)
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Wrong Product Id"),HttpStatus.NOT_FOUND);
        }
        Long sellerId = productSeller.getSellerId();
        boolean vaildUserProduct = userId.equals(sellerId);
        if(!vaildUserProduct)
        {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Not a product of logged user"),HttpStatus.NOT_ACCEPTABLE);
        }
        productRepository.deleteById(id);
        return new ResponseEntity<>(new ApiResponse<>(true,null,"Successfully deleted"),HttpStatus.OK);

    }
}
