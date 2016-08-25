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
package com.fishbeans.stream;

import com.android.ddmlib.*;
import com.fishbeans.app.ui.dialog.DeviceOffLineAlert;
import com.fishbeans.util.ADB_ERRORS;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 * this class basically removes code from a hierarchy previously in the android-studio source code
 *
 */
@Component
public class StreamReceiver {

    private static final String TAG = StreamReceiver.class.getSimpleName();


    private String mUnfinishedLine = null;
    private boolean mTrimLines = true;
    private int myDelayedNewlineCount;
    private static final String STACK_TRACE_LINE_PREFIX = StreamReceiver.repeatSymbol(' ', 4);
    private static final String STACK_TRACE_CAUSE_LINE_PREFIX = Character.toString(' ');
    private int myLineIndex;

    private final StackTraceExpander myStackTraceExpander;
    private LogCatHeader myActiveHeader;
    private final LogCatMessageParser myParser = new LogCatMessageParser();
    private final ArrayList<String> internalStringDataBuffer = new ArrayList<String>();

    private LogLineListener myLogLineListener;

    private interface LogLineListener {
        void receiveLogLine( LogCatMessage line);
        List<LogCatDTO>  getData();
    }

    public StreamReceiver( ) {

        myLogLineListener  = new LogLineListener() {
            private final List<LogCatDTO> buffer = new LinkedList();

            @Override
            public void receiveLogLine(LogCatMessage mssg) {
                buffer.add(new LogCatDTO(mssg));
            }

            @Override
            public  List<LogCatDTO> getData() {
                return buffer;
            }
        };
        myStackTraceExpander = new StackTraceExpander(STACK_TRACE_LINE_PREFIX, STACK_TRACE_CAUSE_LINE_PREFIX);
    }

    // this gets piped thru
    private void notifyLine( LogCatHeader header, String line) {
        myLogLineListener.receiveLogLine(new LogCatMessage(header, line));
        myLineIndex++;
    }


    public final void addOutput(byte[] data, int offset, int length) {

        String rawData = new String(data, offset, length );

        if(rawData.contains(ADB_ERRORS.FAIL_ERROR.getErrorKey())) {

            if(rawData.contains(ADB_ERRORS.OFFLINE_ERROR.getErrorKey())) {
                DeviceOffLineAlert alert = new DeviceOffLineAlert();
                Optional<Boolean> result =  alert.showAndWait();
                if ((result.isPresent()) && result.get() ) {

                    Platform.runLater(() ->  Platform.exit());
                }
            }

        }

        // ok we've got a string
        // if we had an unfinished line we add it.
        if (mUnfinishedLine != null) {
            rawData = mUnfinishedLine + rawData;
            mUnfinishedLine = null;
        }

        // now we split the lines
        internalStringDataBuffer.clear();
        int start = 0;
        do {
            int index = rawData.indexOf('\n', start);

            // if \n was not found, this is an unfinished line
            // and we store it to be processed for the next packet
            if (index == -1) {
                mUnfinishedLine = rawData.substring(start);
                break;
            }

            // we found a \n, in older devices, this is preceded by a \r
            int newlineLength = 1;
            if (index > 0 && rawData.charAt(index - 1) == '\r') {
                index--;
                newlineLength = 2;
            }

            // extract the line
            String line = rawData.substring(start, index);
            if (mTrimLines) {
                line = line.trim();
            }
            internalStringDataBuffer.add(line);

            // move start to after the \r\n we found
            start = index + newlineLength;
        } while (true);

        if (!internalStringDataBuffer.isEmpty()) {
            // at this point we've split all the lines.
            // make the array
            String[] lines = internalStringDataBuffer.toArray(new String[internalStringDataBuffer.size()]);

            // send for final processing
            for (String line : lines) {
                processNewLine(line);

            }
        }

    }


    public void processNewLine( String line) {

        line = line.replaceAll("\\r", "");

        if (line.isEmpty()) {
            myDelayedNewlineCount++;
            return;
        }

        // line is a header or something else not both
        LogCatHeader header = myParser.processLogHeader(line, null );

        if (header != null) {
            myStackTraceExpander.reset();
            myActiveHeader = header;
            myLineIndex = 0;

            myDelayedNewlineCount = 0;

        }
        else if (myActiveHeader != null) {
            if (myDelayedNewlineCount > 0 && myLineIndex == 0) {

                myDelayedNewlineCount = 0;
            }
            else {
                processAnyDelayedNewlines(myActiveHeader);
            }
            for (String processedLine : myStackTraceExpander.process(line)) {
                notifyLine(myActiveHeader, processedLine);
            }
        }


    }

    public List<LogCatDTO>  getStreamSink() {
        return myLogLineListener.getData();
    }

    //==  utility methods



    private void processAnyDelayedNewlines( LogCatHeader header) {
        if (myDelayedNewlineCount == 0) {
            return;
        }
        for (int i = 0; i < myDelayedNewlineCount; i++) {
            notifyLine(header, "");
        }
        myDelayedNewlineCount = 0;
    }


    private static String repeatSymbol(final char aChar, final int count) {
        char[] buffer = new char[count];
        Arrays.fill(buffer, aChar);

        return new String(buffer);
    }

}
