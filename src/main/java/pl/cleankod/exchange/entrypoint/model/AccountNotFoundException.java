package pl.cleankod.exchange.entrypoint.model;

public final class AccountNotFoundException extends RuntimeException {

    private final String cid;
    public AccountNotFoundException(String cid){
        super("The requested account was not found. ");
        this.cid = cid;
    }

    public String getCid() {
        return cid;
    }
}
