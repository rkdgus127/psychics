package Core.Manager.CoolDown;

public class NoCoolException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    @Override
    public String getMessage() {
        return "This Message is normal output";
    }

    @Override
    public String toString() {
        return "This Message is normal output";
    }
}