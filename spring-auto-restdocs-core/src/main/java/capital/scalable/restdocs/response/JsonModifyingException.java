package capital.scalable.restdocs.response;

public class JsonModifyingException extends RuntimeException {

    public JsonModifyingException(String message, Throwable e) {
        super(message, e);
    }
}
