package example.cashcard;

import org.springframework.data.repository.CrudRepository;

// Specify that it manages the CashCard's data,
// and that the datatype of the Cash Card ID is Long.
public interface CashCardRepository extends CrudRepository<CashCard, Long> {
}
