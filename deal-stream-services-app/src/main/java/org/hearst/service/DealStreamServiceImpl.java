package main.java.org.hearst.service;

import main.java.org.hearst.exception.DealStreamServiceException;
import main.java.org.hearst.model.PriceUpdate;
import main.java.org.hearst.model.ProductPrice;
import main.java.org.hearst.util.DealStreamServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.ConcurrentHashMap;


public class DealStreamServiceImpl implements DealStreamService {
    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(DealStreamServiceImpl.class);

    private ConcurrentHashMap<String, Double> priceCache = new ConcurrentHashMap<>(); //cache to store price for each sku

    /**
     * Receives prices updates for a sku
     * @param priceUpdate
     * @throws DealStreamServiceException
     */
    @Override
    public void receivePriceUpdate(PriceUpdate priceUpdate) throws DealStreamServiceException {
        String sku = priceUpdate.getSku();
        UriComponentsBuilder builder = buildResponseEntity(DealStreamServiceConstants.UPDATE_END_POINT_URL, sku);
        ResponseEntity<PriceUpdate> updatePriceResponseEntity = restTemplate.exchange(builder.toUriString(),
                HttpMethod.POST, null, PriceUpdate.class);

        PriceUpdate updatedPrice = updatePriceResponseEntity.getBody();
        Double newPrice = updatedPrice.getPrice();
        log.info("New price for sku " +sku+ " is " +newPrice);
        //Everytime we receive a price update
        // 1. Update database with new price along with timestamp suffix to maintain a history of prices
        // 2. Update cache with new price to maintain latest price in cache
        Double currentPrice = priceCache.get(sku);
        log.info("Current price for sku " +sku+ " is " +currentPrice);
        if(currentPrice == null) { //if cache doesn't have entry, make a service call
            ProductPrice price = findPrice(sku);
            if(price != null) {
                currentPrice = price.getPrice();
            } else {
                log.error("Invalid sku" + sku+ " provided while doing a price lookup");
            }
        }
        if(newPrice < currentPrice) { //indicates best offer
            priceCache.put(sku, newPrice); //update cache with new/updated price.
            //Update database column best_price with the new price/best offer
            //TODO: Add DB wiring to update new price with timestamp, and the best price for the sku
        } else {
            //Update database column best_price with the current price/best offer
        }
    }

    /**
     * Gets price for a sku
     * @param sku
     * @return
     * @throws DealStreamServiceException
     */
    @Override
    public ProductPrice findPrice(String sku) throws DealStreamServiceException {
        ProductPrice productPrice = null;
        if (sku == null) {
            throw new IllegalArgumentException();
        }
        try {
            UriComponentsBuilder builder = buildResponseEntity(DealStreamServiceConstants.SEARCH_END_POINT_URL, sku);
            ResponseEntity<ProductPrice> searchResponseEntity = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.GET, null, ProductPrice.class);

            productPrice = searchResponseEntity.getBody();
            priceCache.put(sku, productPrice.getPrice());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DealStreamServiceException(e);
        }
        return productPrice;
    }

    private UriComponentsBuilder buildResponseEntity(String endpointURL, String sku) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return UriComponentsBuilder.fromHttpUrl(endpointURL)
                .queryParam("api_key", DealStreamServiceConstants.API_KEY).queryParam("q", sku);
    }
}
