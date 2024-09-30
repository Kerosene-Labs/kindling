package com.kerosenelabs.kindling.constant;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int number;
    private final String plainEnglish;

    HttpStatus(int number, String plainEnglish) {
        this.number = number;
        this.plainEnglish = plainEnglish;
    }

    /**
     * Get the number representation of this status code.
     * 
     * @return Int, ex: 200
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the plain english description of this status code.
     * 
     * @return String, ex: OK
     */
    public String getDescription() {
        return plainEnglish;
    }

    @Override
    public String toString() {
        return Integer.toString(number) + " " + plainEnglish;
    }
}
