package com.fishbeans;

import com.fishbeans.service.ADBService;
import com.sun.javafx.application.PlatformImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ubu on 8/23/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CommandLineInteface.class)
public class ADBServiceTestBase {

    @Autowired
    protected ADBService adbService;

    @Before
    public void setUpClass() throws InterruptedException {
        // initialise the javafx toolkit
        PlatformImpl.startup(() -> {});
        Thread.sleep(500);
    }
}



