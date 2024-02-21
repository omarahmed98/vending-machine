package com.vendor.machine.Service;

import com.vendor.machine.Entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;



@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    public Long calculateTotalCost(Product product, Long amount)
    {
        Long costofProduct = Long.parseLong(product.getCost());
        return costofProduct * amount;
    }
    public Map<Long, Long> generateChange(Long remainingDeposit) {
    Long[] coinValues = {100L, 50L, 20L, 10L, 5L};
    Map<Long, Long> change = new HashMap<>();

    for (Long coin : coinValues) {
        Long numCoins = remainingDeposit / coin;
        if (numCoins > 0) {
            change.put(coin, numCoins);
            remainingDeposit %= coin; 
        }
    }
    return change;
    }
  
}
