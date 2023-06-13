package example.cashcard;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
