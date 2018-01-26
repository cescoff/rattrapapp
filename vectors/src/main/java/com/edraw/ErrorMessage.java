package com.edraw;

public class ErrorMessage {

    private final String displayMessage;

    private final int code;

    private final String details;

    private ErrorMessage(final String displayMessage, final int code, final String details) {
        this.displayMessage = displayMessage;
        this.code = code;
        this.details = details;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }

    public static ErrorMessage create(final String displayMessage) {
        return new ErrorMessage(displayMessage, -1, null);
    }

    public static ErrorMessage create(final String displayMessage, final String details) {
        return new ErrorMessage(displayMessage, -1, details);
    }

    public static ErrorMessage create(final String displayMessage, final int code, final String details) {
        return new ErrorMessage(displayMessage, code, details);
    }

    public String toString() {
        return new StringBuilder("Display message='").append(displayMessage).append("', code=").append(code).append(" details='").append(details).append("'").toString();
    }

}


