package prueba.tecnica.libreria.exception;

public class ActiveLoanAlreadyExistsException extends RuntimeException {
    public ActiveLoanAlreadyExistsException(String message) {
        super(message);
    }

}
