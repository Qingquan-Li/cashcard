package example.cashcard;

import java.util.Optional;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private CashCardRepository cashCardRepository;

    // Utilize the CashCardRepository to manage our CashCard data.
    // Inject the CashCardRepository into CashCardController.
    // Spring's Auto Configuration is utilizing its dependency
    // injection (DI) framework, specifically constructor injection,
    // to supply CashCardController with the correct implementation
    // of CashCardRepository at runtime.
    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        // Find the CashCard using `CrudRepository.findById`.
        // The CrudRepository interface provides many helpful methods, including `findById(ID id)`.
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        // We're calling CrudRepository.findById which returns an Optional. This smart object might
        // or might not contain the CashCard for which we're searching.
        // If cashCardOptional.isPresent() is true then the repository successfully found
        // the CashCard and we can retrieve it with cashCardOptional.get().
        // If not, the repository has not found the CashCard.
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        // We must provide a Location header with the URI for
        // where to find the newly created CashCard.
        // Spring Data's CrudRepository provides methods that support creating, reading, updating,
        // and deleting data from a data store.
        // `cashCardRepository.save(newCashCardRequest)` does just as it says: it saves a new CashCard
        // for us, and returns the saved object with a unique id provided by the database.
        // We add UriComponentsBuilder ucb as a method argument to this POST handler method and it was
        // automatically passed in. How so? It was injected from Spring's IoC Container.
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = ucb
            .path("/cashcards/{id}")
            .buildAndExpand(savedCashCard.id())
            .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
