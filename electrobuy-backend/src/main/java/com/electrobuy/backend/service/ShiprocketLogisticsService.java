@Service
@Slf4j
public class ShiprocketLogisticsService implements LogisticsService {

    private final WebClient webClient;

    public ShiprocketLogisticsService(WebClient.Builder webClientBuilder,
            @Value("${logistics.api.base-url}") String baseUrl,
            @Value("${logistics.api.token}") String apiToken) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public ShippingResponse initiateShipment(ShippingRequest request) {
        log.info("Initiating 3PL shipment booking for order: {}", request.orderId());

        try {
            return webClient.post()
                    .uri("/shipments/create")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ShippingResponse.class)
                    .block(); // Blocking here safely if called inside a transaction-isolated backend worker
        } catch (WebClientResponseException e) {
            log.error("Failed to book shipment with 3PL. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new LogisticsIntegrationException("3PL Courier service booking failed: " + e.getMessage());
        }
    }
}