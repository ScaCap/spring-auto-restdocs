package capital.scalable.restdocs.jackson.payload;

public class JacksonFieldProcessingException extends RuntimeException {

    public JacksonFieldProcessingException(String message, Throwable e) {
        super(message, e);
    }
}
