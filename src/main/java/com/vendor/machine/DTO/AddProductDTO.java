package com.vendor.machine.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AddProductDTO {
    private String productName;
    private Long amountAvailable;
    private String cost;
}
