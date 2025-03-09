package service;

/**
 * Indicates there was an error connecting to the database
 */
public class AlreadyTakenException extends ServiceException {
    public AlreadyTakenException() {
        super("already taken");
    }

    @Override
    public int getStatusCode () {
        return 403;
    }
}