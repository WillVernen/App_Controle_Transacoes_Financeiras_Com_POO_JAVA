package br.com.lab.banco.exception;

/**
 * Exceção de regra de negócio da aplicação.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String message) {
        super(message);
    }
}
