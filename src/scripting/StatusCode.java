package scripting;

public enum StatusCode {

    UNKNOWN("Unknown"),
    NOT_STARTED("Not Started"), STARTED("Started"), // may be useful in certain context
    IN_PROGRESS("In Progress"), RUNNING("Running"), // may be useful in certain context
    QUEUED("Queued"),
    SUCCESSFUL("Successful"),
    FAILED("Failed"),
    UNSUCCESSFULL("Unsuccessful"),
    COMPLETED("Completed"), // may be useful in certain context
    CANCELLED("Cancelled"),
    STOPPED("Stopped"),
    PARTIAL("Partial"),;


    private String status;

    private StatusCode(String status) {
        this.status = status;
    }

    public static StatusCode getStatusCode(StatusType status) {
        switch (status) {
            case NO_ERROR:
                return StatusCode.SUCCESSFUL;

            case PARTIAL_ERROR:
                return StatusCode.PARTIAL;

            default:
                return StatusCode.UNSUCCESSFULL;
        }
    }

    @Override
    public String toString() {
        return this.status;
    }
}