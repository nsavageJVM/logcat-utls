package com.fishbeans.util;

/**
 * Created by ubu on 24.08.16.
 *
 * FAIL0012device offline (x)
 */
public enum ADB_ERRORS {

    FAIL_ERROR("FAIL"),
    OFFLINE_ERROR("device offline ");

    String errorKey;

    ADB_ERRORS(String errorKey) {
        this.errorKey = errorKey;
    }


    public String getErrorKey( ) {
        return errorKey;
    }
}
