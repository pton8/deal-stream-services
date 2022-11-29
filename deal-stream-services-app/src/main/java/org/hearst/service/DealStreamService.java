package main.java.org.hearst.service;

import main.java.org.hearst.exception.DealStreamServiceException;
import main.java.org.hearst.model.PriceUpdate;
import main.java.org.hearst.model.ProductPrice;

public interface DealStreamService {

    public void receivePriceUpdate(PriceUpdate priceUpdate) throws DealStreamServiceException;

    public ProductPrice findPrice(String sku) throws DealStreamServiceException;

}
