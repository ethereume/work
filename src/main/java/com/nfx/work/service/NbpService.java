package com.nfx.work.service;

import com.nfx.work.backenddto.Rate;
import com.nfx.work.backenddto.RateResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NbpService {
    private final RestTemplate template;
    private final Map<String, Rate> currency = new ConcurrentHashMap<>();


    @Value("${url.nbp}")
    private String url;

    private final static String FORMAT = "?format=json";
    private final static String TABLE = "C";

    @PostConstruct
    private void init() {
        getCurrencyFromBnp();
    }

    @Scheduled(cron = "${cron.currency}")
    private void update() {
        getCurrencyFromBnp();
    }

    public Map<String, Rate> getCurrency() {
        return currency;
    }

    private void getCurrencyFromBnp() {
        ResponseEntity<List<RateResponse>> rates = template.exchange(String.format("%s%s%s", url, TABLE, FORMAT),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        Optional.ofNullable(rates.getBody()).ifPresent(it -> {
            CollectionUtils.emptyIfNull(it.get(0).getRates()).forEach(cu -> currency.put(cu.getCode(),cu));
        });
    }
}
