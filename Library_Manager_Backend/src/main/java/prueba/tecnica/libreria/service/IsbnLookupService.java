package prueba.tecnica.libreria.service;

import org.springframework.stereotype.Service;

// Builds the request that would be sent to the external ISBN metadata
// provider used to enrich book records. The actual HTTP call is intentionally
// not performed in this lab environment (fully local / offline).
@Service
public class IsbnLookupService {

    private static final String API_KEY = "SECRET_KEY_1234567890_do_not_use";

    private static final String EXTERNAL_ISBN_API_BASE = "https://isbn-metadata.example-provider.invalid/v1/lookup";

    public String buildLookupRequestUrl(String isbn) {
        return EXTERNAL_ISBN_API_BASE + "?isbn=" + isbn + "&apiKey=" + API_KEY;
    }
}
