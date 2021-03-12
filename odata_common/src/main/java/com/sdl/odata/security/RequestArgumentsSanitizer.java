/**
 * Copyright (c) 2014 All Rights Reserved by the SDL Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.security;

import org.slf4j.Logger;

/**
 * This class presents utility methods to protect Loggers from CR (NL, CF) characters,
 * which may fake log messages.
 *
 * The kit supports sanitizing a single parameter with using method 'sanitize',
 * bunch of parameters with method 'getSanitizedCopy' or even as a straight-forward
 * replacing for Logger.
 *
 * USAGE: it's safer to use:
 *    {@link #sanitize}(LOG, LogLevel.DEBUG, "arg {}", publicationId);
 * instead of:
 *    LOG.debug("arg {}", publicationId);
 *
 * or use
 *    {@link #sanitize}(LOG, LogLevel.ERROR, "Invalid arg {} provided", publicationId, new IllegalArfumentException());
 * instead of*
 *    LOG.error("Invalid arg {} provided", publicationId, new IllegalArfumentException());
 */
public class RequestArgumentsSanitizer {
    private static final Logger LOG = LoggerFactory.getLogger(RequestArgumentsSanitizer.class);

    private RequestArgumentsSanitizer() {
    }

    /**
     * Returns an array of given arguments, each of them is safe for printing in logs, as arguments
     * are having CR (NL, CR) replaced with safe character "\u2B0E" (to continue representing replaced
     * chars).
     * @param argArray is an array with elements to sanitize.
     * @return an array with sanitized elements. which may be safe logged.
     */
    public static Object[] getSanitizedCopy(Object... argArray) {
        if (argArray == null || argArray.length == 0) {
            throw new IllegalStateException("Arguments are not provided or empty");
        }
        int length = argArray.length;

        int indexOfException = -1;
        Object lastElement = argArray[length - 1];
        if (lastElement != null && Throwable.class.isAssignableFrom(lastElement.getClass())) {
            indexOfException = length - 1;
        }
        int index = 0;
        Object[] trimmed = new Object[length];
        for (Object arg : argArray) {
            trimmed[index++] = sanitize(arg);
            if (indexOfException > 0 && indexOfException == index) {
                trimmed[length - 1] = lastElement;
                break;
            }
        }
        return trimmed;
    }

    /**
     * Provides replacing CR (NL, CF) character with "\u2B0E".
     * @param arg o sanitize.
     * @return sanitized parameter ( safe for logging it).
     */
    public static Object sanitize(Object arg) {
        if (arg == null) {
            return "null";
        }
        if (Throwable.class.isAssignableFrom(arg.getClass())) {
            return arg;
        }
        String toString = arg.toString();
        return toString
                .replaceAll("\\n|\\r", "\u2B0E")
                .replaceAll("%0[aAdD]", "\u2935");
    }
}
