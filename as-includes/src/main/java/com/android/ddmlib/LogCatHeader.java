/*
 * Copyright (C) 2015 The Android Open Source Project
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
 * Data class for message header information which gets reported by logcat.
 */
public final class LogCatHeader {

    
    private final LogLevel mLogLevel;

    private final int mPid;

    private final int mTid;

     
    private final String mAppName;


    private final String mTag;


    private final LogCatTimestamp mTimestamp;

    /**
     * Construct an immutable log message object.
     */
    public LogCatHeader(  LogLevel logLevel, int pid, int tid,  String appName,
              String tag, LogCatTimestamp timestamp) {
        mLogLevel = logLevel;
        mAppName = appName;
        mTag = tag;
        mTimestamp = timestamp;
        mPid = pid;
        mTid = tid;
    }


    public LogLevel getLogLevel() {
        return mLogLevel;
    }

    public int getPid() {
        return mPid;
    }

    public int getTid() {
        return mTid;
    }


    public String getAppName() {
        return mAppName;
    }


    public String getTag() {
        return mTag;
    }


    public LogCatTimestamp getTimestamp() {
        return mTimestamp;
    }

    @Override
    public String toString() {
        return String.format("%s: %s/%s(%s)", mTimestamp, mLogLevel.getPriorityLetter(), mTag,
                mPid);
    }
}
