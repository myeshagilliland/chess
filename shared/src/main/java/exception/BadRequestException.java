package exception;

/**
 * Indicates there was an error connecting to the database
 */
public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super("bad request");
    }

    @Override
    public int getStatusCode () {
        return 400;
    }
}