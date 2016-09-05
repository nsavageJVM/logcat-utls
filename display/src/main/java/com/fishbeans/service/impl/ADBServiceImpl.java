package com.fishbeans.service.impl;

import com.fishbeans.ConfigTrait;
import com.fishbeans.producer.ADBProducer;
import com.fishbeans.service.ADBService;
import com.fishbeans.stream.LogCatDTO;
import com.fishbeans.stream.StreamReceiver;
import com.fishbeans.util.ADB_COMMANDS;
import com.fishbeans.util.UsbDevice;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * http://stackoverflow.com/questions/23997284/change-substring-color-in-javafx-text-area
 */

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ADBServiceImpl implements ADBService, ConfigTrait {

    public boolean logThreads = false;

    private static Path localFilePath;

    private  boolean  isLogLevelFilter;

    private  String logLevelString = "info";

    @Value("${adb.device}")
    private String device;

    @Value("${lcat.default.filter}")
    private String filter;


    @Value("${adb.exe.location}")
    private String ADB_EXECUTABLE_PATH;

    private final ADBProducer adbProducer;
    private final StreamReceiver logStreamReciever;
    private SocketChannel sock;
    private static final String TAG = ADBServiceImpl.class.getSimpleName();
    private static String adb_home;

    private LogStreamSink sink;
    private LogStreamService stream;

    //<editor-fold desc="==  init ==" >
    @PostConstruct
    @Override
    public void init() {

        localFilePath =    Paths.get(baseStoragePath, outFilePath );

        if (android_home() == null || android_home().equals("")) {
            adb_home = ADB_EXECUTABLE_PATH + adb_relative_path;
        } else {
            adb_home = android_home() + adb_relative_path;
        }

        runProcessCmd(ADB_COMMANDS.START_ADB_SERVER.getCommand());

        sock = adbProducer.getADBSocket();
    }
    //</editor-fold>

    //<editor-fold desc="== start / stop ADBServer ==" >
    @Override
    public void startADBServer() {
        runProcessCmd(ADB_COMMANDS.START_ADB_SERVER.getCommand());

    }

    @Override
    public void stopADBServer() {
        runProcessCmd(ADB_COMMANDS.STOP_ADB_SERVER.getCommand());
    }
    //</editor-fold>

    //<editor-fold desc="==  runLogCatScheduledService uses scheduled services for stream ==" >
    @Override
    public void runLogCatScheduledService(InlineCssTextArea terminalOutput) {
        StringBuffer logger = new StringBuffer("");
        if(!Objects.isNull(terminalOutput)) {
            terminalOutput.replaceText("" );
        }


        byte[]  setLogCatRequest = adbProducer.getRequestAsProtocolBytes(ADB_COMMANDS.LOGCAT_CMD.getCommand());
        System.out.println(new String(setLogCatRequest));


        ByteBuffer buf = ByteBuffer.wrap(setLogCatRequest, 0, setLogCatRequest.length);
        int count = 0;
        while (buf.position() != buf.limit()) {

            try {
                count = sock.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(logThreads) {
            logger.append(TAG).append(String.format(" runLogCatScheduledService first count %s%n", count));
            System.out.println(logger);
            logger.setLength(0);
        }


        byte[] logData = new byte[130712];
        ByteBuffer buffer = ByteBuffer.wrap(logData);
        stream = new LogStreamService();
        stream.setBuffer(buffer);
        stream.setSocketChannel(sock);
        stream.setPeriod(Duration.millis(1000));
        stream.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {

                byte[] data = ((ByteBuffer) t.getSource().getValue()).array();
                int offset = ((ByteBuffer) t.getSource().getValue()).arrayOffset();
                int posn = ((ByteBuffer) t.getSource().getValue()).position();
                if(logThreads) {
                    logger
                            .append(TAG)
                            .append(String.format(
                                    " LogStreamService to reciever  data %s offset %s posn %s%n", data.length, offset, posn));
                    System.out.println(logger);
                }

                logStreamReciever.addOutput(data, offset, posn);
                buffer.clear();

            }
        });
        stream.start();

        sink = new LogStreamSink();
        sink.setPeriod(Duration.millis(1000));
        sink.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {

                List<LogCatDTO> toPrint = (List<LogCatDTO>) t.getSource().getValue();
                if(terminalOutput != null) {
                    terminalOutput.clear();
                    int counter = 0;
                    for (LogCatDTO dto : toPrint) {
                        String line = dto.prettyPrint();
                        terminalOutput.appendText(line);
                        terminalOutput.setStyle(counter, line.length()+ counter , String.format("-fx-fill:%s;", dto.getLineDisplayColor()));
                        counter = counter + line.length();
                    }
                    toPrint.clear();

                } else {

                    StringJoiner fileLogger = new StringJoiner("");

                    for (LogCatDTO dto : toPrint) {
                        fileLogger.add(dto.prettyPrint());

                    }
                    String outData = fileLogger.toString();
                    try (BufferedWriter writer = Files.newBufferedWriter(localFilePath)) {
                        writer.write(outData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sink.start();

    }
    //</editor-fold>

    //<editor-fold desc="== start / stop stream ==" >
   public void  stopLogStream() {
       if(!Objects.isNull(sink)) {
           sink.cancel();
       }

    }

    public void  startLogStream() {
        if(!Objects.isNull(sink)) {
            sink.reset();
            sink.start();
        }
    }
    //</editor-fold>

    //<editor-fold desc="== setFilter ==" >
    @Override
    public void setFilter(String filterText) {
        filter = filterText;
    }

    @Override
    public void clearBuffer() {
        stream.clearBuffer();
    }
    //</editor-fold>

    //<editor-fold desc="== scheduled services for stream ==" >
    private class LogStreamSink extends ScheduledService<List<LogCatDTO>> {


        @Override
        protected Task<List<LogCatDTO> > createTask() {
            return new Task<List<LogCatDTO> >() {
                protected List<LogCatDTO>  call() {

                    List<LogCatDTO> toPrint = null;
                    List<LogCatDTO> results =  logStreamReciever.getStreamSink();
                    if(isLogLevelFilter) {
                        toPrint = results.stream() .filter(line -> line.getMssg().getLogLevel().getStringValue().equals(logLevelString)).collect(Collectors.toList());
                    } else {
                        toPrint = results.stream() .collect(Collectors.toList());
                    }

                    if(!filter.equals(ADB_COMMANDS.SET_NO_FILTER.name())) {
                        List<LogCatDTO>   filtered  = toPrint.stream() .filter(line -> line.getMssg().getMessage().contains(filter)).collect(Collectors.toList());
                        return filtered;
                    }

                    return toPrint;
                }
            };
        }
    }


    private class LogStreamService extends ScheduledService<ByteBuffer> {
        ByteBuffer buffer;
        SocketChannel sock;

        public final void setBuffer(ByteBuffer value) {
            buffer = value;
        }
        public final void clearBuffer() {
            buffer.clear();
        }
        public final void setSocketChannel(SocketChannel value) {
            sock = value;
        }

        protected Task<ByteBuffer> createTask() {
            return new Task<ByteBuffer>() {
                protected ByteBuffer call() {
                    try {
                        buffer.clear();
                        sock.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return buffer;
                }
            };
        }
    }
    //</editor-fold>


    @Override
    public String setDevice(boolean isDefault, String deviceId) {
        StringJoiner logger = new StringJoiner("");
        byte[] setDeviceRequest = null;
        if(isDefault) {
            setDeviceRequest = adbProducer.getRequestAsProtocolBytes(ADB_COMMANDS.setDevice(device));
        } else {
            setDeviceRequest = adbProducer.getRequestAsProtocolBytes(ADB_COMMANDS.setDevice(deviceId));
        }

        ByteBuffer buf = ByteBuffer.wrap(setDeviceRequest, 0, setDeviceRequest.length);
        while (buf.position() != buf.limit()) {

            try {
               sock.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] data = new byte[2048];
        ByteBuffer buf1 = ByteBuffer.wrap(data);
        try {
                sock.read(buf1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.add(TAG).add(String.format(" setDevice result %s%n", new String(data)));

        return logger.toString();
    }

    public List<UsbDevice> getDevices()  {
        StringJoiner logger = new StringJoiner("");
        byte[] setDeviceRequest = adbProducer.getRequestAsProtocolBytes(ADB_COMMANDS.LIST_DEVICES_CMD.getCommand());
        ByteBuffer buf = ByteBuffer.wrap(setDeviceRequest, 0, setDeviceRequest.length);
        int count = 0;
        while (buf.position() != buf.limit()) {

            try {
                count = sock.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // http://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
        logger.add(TAG).add(String.format("setDevice first count %s%n", count));

        count = 0;
        byte[] data = new byte[16384];
        ByteBuffer buf1 = ByteBuffer.wrap(data);
        try {
            count = sock.read(buf1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body =  new String(data);
        body= body.substring(8);
        logger.add(TAG).add(String.format("setDevice second count %s%n", count));
        logger.add(TAG).add(String.format("setDevice result %s%n", body));

        return parseDevices(body);
    }

    @Override
    public  void setIsLogLevelFilter(boolean isLogLevelFiter) {
       this.isLogLevelFilter = isLogLevelFiter;
    }

    @Override
    public  void setLogLevelString(String logLevelString) {
        this.logLevelString = logLevelString;
    }


    //== private methods
    private List<UsbDevice> parseDevices(String body) {
        String[] lines = body.split("\n");
        ArrayList<UsbDevice> devices = new ArrayList<UsbDevice>(lines.length);
        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts.length > 1) {
                devices.add(new UsbDevice(parts[0]));
            }
        }
        return devices;
    }

    private void runProcessCmd(String cmd) {

        try {
            Process p = Runtime.getRuntime().exec(new String[]{adb_home, cmd});
            p.waitFor();
            int exitValue = p.exitValue();
            if (exitValue != 0) throw new IOException("error start adb with code: " + exitValue);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String android_home() { return System.getenv("ANDROID_HOME"); }





}
