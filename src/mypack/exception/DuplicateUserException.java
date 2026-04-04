package mypack.exception;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String msg) {
        super(msg);
    }
}