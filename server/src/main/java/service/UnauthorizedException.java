package service;

/**
 * Indicates there was an error connecting to the database
 */
public class UnauthorizedException extends ServiceException {
    public UnauthorizedException() {
        super("unauthorized");
    }

    @Override
    public int getStatusCode () {
        return 401;
    }
}