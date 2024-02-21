package com.vendor.machine.Service;

import com.vendor.machine.DTO.UpdateProductDTO;
import com.vendor.machine.Mapper.ProductMapper;
import com.vendor.machine.Entity.Product;
import com.vendor.machine.Entity.User;
import com.vendor.machine.Repository.ProductRepository;
import com.vendor.machine.Repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    public List<UpdateProductDTO> getallProducts()
    {
        List<Product> products = productRepository.findAll();
        return productMapper
                .toProductDtos(products);
    }
    public Product saveProduct(HttpServletRequest request, Product product) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        log.info("getAccessTokenCookie response {} ", username);
        User user = userRepository.findByUsername(username).orElse(null);
        product.setSellerId(user.getId());
        return productRepository
                .save(product);         
    }
    public int updateProduct(UpdateProductDTO productDTO) {
        return productRepository
        .updateProduct(productDTO); 
    }
}
