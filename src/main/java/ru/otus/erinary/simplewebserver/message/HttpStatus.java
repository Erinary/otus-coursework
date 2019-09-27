package ru.otus.erinary.simplewebserver.message;

public enum  HttpStatus {

    /*1xx Informational*/
    CONTINUE(100, "Continue"),

    /*2xx Success*/
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    /*3xx Redirection*/
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    NOT_MODIFIED(304, "Not Modified"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),

    /*4xx Client Error*/
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),

    /*5xx Server Error*/
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(502, "Service Unavailable");


    private int code;
    private String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
