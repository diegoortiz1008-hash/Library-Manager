package prueba.tecnica.libreria.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Builds the request that would be sent to the external ISBN metadata
// provider used to enrich book records. The actual HTTP call is intentionally
// not performed in this lab environment (fully local / offline).
@Service
public class IsbnLookupService {

    private static final String EXTERNAL_ISBN_API_BASE = "https://isbn-metadata.example-provider.invalid/v1/lookup";

    // No literal credential in source: read from isbn.api.key, which in turn
    // resolves from the ISBN_API_KEY environment variable (empty by default).
    private final String apiKey;

    public IsbnLookupService(@Value("${isbn.api.key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    public String buildLookupRequestUrl(String isbn) {
        return EXTERNAL_ISBN_API_BASE + "?isbn=" + isbn + "&apiKey=" + apiKey;
    }
}
