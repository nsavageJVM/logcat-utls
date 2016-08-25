package com.fishbeans.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


import java.net.URL;

/**
 * Created by goran on 14/04/2015.
 * Resource Utilities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceUtils {

    /**
     * Gets fxmlView from classpath.
     *
     * @param name The fxmlView name. Required.
     * @return The URL of the fxmlView, or null if not found.
     * @throws java.lang.NullPointerException     if name arg is null.
     * @throws java.lang.IllegalArgumentException if name arg is empty string.
     */
    public static URL getResource(String name) {


        URL url = Thread.currentThread().getContextClassLoader().getResource(name);

        if (url == null) {
            url = ResourceUtils.class.getResource(name);
        }

        if (url == null) {
            url = ResourceUtils.class.getClassLoader().getResource(name);
        }

        if (url == null) {
            url = ClassLoader.getSystemResource(name);
        }

        return url;
    }
}
