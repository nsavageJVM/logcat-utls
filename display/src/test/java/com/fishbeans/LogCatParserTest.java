package com.fishbeans;

import javafx.application.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * //http://andreinc.net/2013/12/05/java-7-nio-2-tutorial-file-attributes/
 * //http://winterbe.com/posts/2015/03/25/java8-examples-string-number-math-files/
 *
 * .collect(Collectors.toList());
 *
 * .filter(line -> line.contains("print"))
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CommandLineInteface.class)
public class LogCatParserTest {

    private static final String baseStoragePath = System.getProperty("user.dir");
    private static final String outFilePath = "/alogs/";
    private static Path localFilePath;


    @Before
    public void setUpClass() throws InterruptedException {
        localFilePath =    Paths.get(baseStoragePath, outFilePath );
    }

    @Test
    public void  parseOutPutForFilter() {
        try (BufferedReader reader = Files.newBufferedReader(localFilePath)) {
            List<String> toPrint = reader.lines() .filter(line -> line.contains("medvis")).collect(Collectors.toList());
            System.out.println(toPrint);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
