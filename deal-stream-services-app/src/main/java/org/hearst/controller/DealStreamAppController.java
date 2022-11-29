package main.java.org.hearst.controller;

import main.java.org.hearst.exception.DealStreamServiceException;
import main.java.org.hearst.model.PriceUpdate;
import main.java.org.hearst.model.ProductPrice;
import main.java.org.hearst.service.DealStreamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DealStreamAppController {

    @Autowired
    private DealStreamServiceImpl dealStreamService;

    @GetMapping(value = "/v1/deals/search/{sku}")
    public ProductPrice findProductPrice(@PathVariable String sku) throws DealStreamServiceException {
        return dealStreamService.findPrice(sku);
    }

    @PostMapping(value = "/v1/deals/update")
    public void updateProductPrice(@RequestBody PriceUpdate priceUpdate) throws DealStreamServiceException {
        dealStreamService.receivePriceUpdate(priceUpdate);
    }
}
