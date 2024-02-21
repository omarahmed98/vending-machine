package com.vendor.machine.Repository;

import com.vendor.machine.Entity.Product;
import com.vendor.machine.DTO.UpdateProductDTO;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

 
    @Transactional
    @Modifying
    @Query(value = "UPDATE Product p SET p.productName = :#{#data.productName} ," +
                    " p.amountAvailable = :#{#data.amountAvailable} ," +
                    " p.cost = :#{#data.cost} " +
                    "WHERE p.id = :#{#data.id}")
    int updateProduct( @Param("data") UpdateProductDTO data );
}
