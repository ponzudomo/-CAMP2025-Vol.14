package com.comecan.controller;

import com.comecan.model.MasterCosmetic;
import com.comecan.repository.MasterCosmeticRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cosmetics")
public class CosmeScanController {

    @Value("${rakuten.api.app-id}")
    private String rakutenAppId;

    @Value("${rakuten.api.url}")
    private String rakutenApiUrl;

    private final MasterCosmeticRepository masterCosmeticRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CosmeScanController(MasterCosmeticRepository masterCosmeticRepository) {
        this.masterCosmeticRepository = masterCosmeticRepository;
    }

    @GetMapping("/scan")
    public ResponseEntity<?> scanCosmeticByJanCode(@RequestParam String jan) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String apiUrl = String.format("%s?format=json&applicationId=%s&keyword=%s",
                    rakutenApiUrl, rakutenAppId, jan);
            HttpGet request = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = client.execute(request)) {
                int status = response.getStatusLine().getStatusCode();
                String body = EntityUtils.toString(response.getEntity());

                if (status != 200) {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(Map.of("message", "Rakuten API returned non-200", "status", status, "body", body));
                }

                JsonNode root = objectMapper.readTree(body);
                JsonNode items = root.path("Items");
                if (items.isMissingNode() || items.size() == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "Product not found for jan: " + jan));
                }

                JsonNode item = items.get(0).path("Item");
                MasterCosmetic mc = new MasterCosmetic();
                mc.setJanCode(jan);
                mc.setName(item.path("itemName").asText(""));
                mc.setBrand(item.path("makerName").asText(item.path("brandName").asText("")));
                JsonNode images = item.path("mediumImageUrls");
                if (images.isArray() && images.size() > 0) {
                    mc.setImageUrl(images.get(0).path("imageUrl").asText(""));
                }

                MasterCosmetic saved = masterCosmeticRepository.save(mc);
                return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "External API communication error.", "detail", e.getMessage()));
        }
    }

    // optional helper if you prefer to use it elsewhere — uses rakutenApiUrl (not RAKUTEN_API_URL)
    private JsonNode fetchProductInfoFromRakuten(String janCode) throws IOException {
        String url = String.format("%s?applicationId=%s&keyword=%s&format=json",
                rakutenApiUrl, rakutenAppId, janCode);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() != 200) {
                    System.err.println("Rakuten API Error Status: " + response.getStatusLine().getStatusCode() + ", Body: " + result);
                    throw new IOException("Rakuten API returned non-200 status code.");
                }

                return objectMapper.readTree(result);
            }
        }
    }
}