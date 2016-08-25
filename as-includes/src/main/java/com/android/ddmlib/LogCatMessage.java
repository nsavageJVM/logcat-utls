/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ddmlib;


/**
 * Model a single log message output from {@code logcat -v long}.
 *
 * Every message is furthermore associated with a {@link LogCatHeader} which contains additionally
 * meta information about the message.
 */
public final class LogCatMessage {


    private final LogCatHeader mHeader;


    private final String mMessage;

    /**
     * @deprecated Create a {@link LogCatHeader} separately and call {@link
     * #LogCatMessage(LogCatHeader, String)} instead. This approach shares the same header data
     * across multiple messages.
     */
    @Deprecated
    public LogCatMessage(LogLevel logLevel, int pid, int tid,
                         String appName, String tag,
                         LogCatTimestamp timestamp, String msg) {
        this(new LogCatHeader(logLevel, pid, tid, appName, tag, timestamp), msg);
    }

    /**
     * Construct an immutable log message object.
     */
    public LogCatMessage( LogCatHeader header, String msg) {
        mHeader = header;
        mMessage = msg;
    }

    /**
     * Helper constructor to generate a dummy message, useful if we want to add message from code
     * that matches the logcat format.
     */
    public LogCatMessage( LogLevel logLevel,  String message) {
        this(logLevel, -1, -1, "", "", LogCatTimestamp.ZERO, message);
    }

    public LogCatHeader getHeader() {
        return mHeader;
    }

    public String getMessage() {
        return mMessage;
    }

    public LogLevel getLogLevel() {
        return mHeader.getLogLevel();
    }

    public int getPid() {
        return mHeader.getPid();
    }

    public int getTid() {
        return mHeader.getTid();
    }

    public String getAppName() {
        return mHeader.getAppName();
    }

    public String getTag() {
        return mHeader.getTag();
    }

    public LogCatTimestamp getTimestamp() {
        return mHeader.getTimestamp();
    }

    @Override
    public String toString() {
        return mHeader.toString() + ": " + mMessage;
    }
}
