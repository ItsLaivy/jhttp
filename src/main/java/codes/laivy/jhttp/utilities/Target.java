package codes.laivy.jhttp.utilities;

public enum Target {

    REQUEST(true, false),
    RESPONSE(false, true),
    BOTH(true, true),
    ;

    private final boolean requests;
    private final boolean responses;

    Target(boolean requests, boolean responses) {
        this.requests = requests;
        this.responses = responses;
    }

    public boolean isRequests() {
        return requests;
    }
    public boolean isResponses() {
        return responses;
    }

}
