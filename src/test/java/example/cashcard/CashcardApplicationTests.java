package example.cashcard;

import java.net.URI;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
	}

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldCreateANewCashCard() {
        // The database will create and manage all unique CashCard.id values for us. We should not provide one.
        CashCard newCashCard = new CashCard(null, 250.00);
        // We must provide `newCashCard` data for the new CashCard.
        // We don't expect a CashCard to be returned to us, so we expect a Void response body.
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        // We expect the HTTP response status code to be 201 CREATED,
        // which is semantically correct if our API creates a new CashCard from our request.
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Send a 201 (Created) response containing a Location header field
        // that provides an identifier for the primary resource created.
        // A URL is a type of URI
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        // We use the Location header's information to fetch the newly created CashCard.
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify the new CashCard.id is not null, and the newly created CashCard.amount is 250.00,
        // just as we specified at creation time.
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");
        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }
}
