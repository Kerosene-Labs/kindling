package com.kerosenelabs.kindling.constant;

public enum MimeType {
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html");

    private final String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return mimeType;
    }
}
