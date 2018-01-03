package capital.scalable.restdocs.payload;

public class JacksonFieldProcessingException extends RuntimeException {

    public JacksonFieldProcessingException(String message, Throwable e) {
        super(message, e);
    }
}
