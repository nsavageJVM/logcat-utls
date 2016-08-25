package com.fishbeans.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by ubu on 8/22/16.
 */
@Data
@RequiredArgsConstructor
public class UsbDevice {

    private final String serial;


    @Override
    public String toString() {
        return serial;
    }

    public String prettyPrint() {

        return String.format("Connected device %s%n",serial);

    }
}
