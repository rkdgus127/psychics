package Core.Manager.Mana;

public class NoManaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoManaException() {
        super("This Message is normal output");
    }

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