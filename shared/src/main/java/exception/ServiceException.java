package exception;

/**
 * Indicates there was an error at the service level
 */
public class ServiceException extends Exception{
    public ServiceException(String message) {
        super(message);
    }

    public String getErrorMessage () {
        return "{\"message\": \"Error: "+getMessage()+"\"}";
    }

    public int getStatusCode () {
        return 500;
    }
}