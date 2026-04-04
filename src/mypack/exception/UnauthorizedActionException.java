package mypack.exception;

public class UnauthorizedActionException extends Exception {
    public UnauthorizedActionException(String msg) {
        super(msg);
    }
}