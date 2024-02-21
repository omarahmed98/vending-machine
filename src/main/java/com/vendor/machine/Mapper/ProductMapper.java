package com.vendor.machine.Mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vendor.machine.DTO.AddProductDTO;
import com.vendor.machine.DTO.UpdateProductDTO;
import com.vendor.machine.Entity.Product;

@Component
public class ProductMapper {

    public List<UpdateProductDTO> toProductDtos(List<Product> products) {
        List<UpdateProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            UpdateProductDTO dto = new UpdateProductDTO();
            dto.setId(product.getId());
            dto.setProductName(product.getProductName());
            dto.setAmountAvailable(product.getAmountAvailable());
            dto.setCost(product.getCost());
            dtos.add(dto);
        }
        return dtos;
    }
    public UpdateProductDTO toProductDto(Product product) {
            UpdateProductDTO dto = new UpdateProductDTO();
            dto.setId(product.getId());
            dto.setProductName(product.getProductName());
            dto.setAmountAvailable(product.getAmountAvailable());
            dto.setCost(product.getCost());
            return dto;
    }
    public Product toAddProduct(AddProductDTO product) {
        Product dto = new Product();
        dto.setProductName(product.getProductName());
        dto.setAmountAvailable(product.getAmountAvailable());
        dto.setCost(product.getCost());
        return dto;
    }
    
}
