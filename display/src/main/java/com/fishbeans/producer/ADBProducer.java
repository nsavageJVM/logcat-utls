package com.fishbeans.producer;

import com.fishbeans.ConfigTrait;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

/**
 * Created by ubu on 8/20/16.
 */

@Component
@NoArgsConstructor
public class ADBProducer implements ConfigTrait {


    // SocketChannel is a non-blocking socket
    public SocketChannel getADBSocket() {
        SocketChannel soc = null;
        try {
            InetAddress adbINETSocket = InetAddress.getByName(DEFAULT_ADB_HOST);
            InetSocketAddress sSocketAddr = new InetSocketAddress(adbINETSocket, DEFAULT_ADB_PORT);
            soc = SocketChannel.open(sSocketAddr);
            soc.configureBlocking(false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return soc;
    }


    public byte[] getRequestAsProtocolBytes(String req) {

        String resultStr = String.format("%04X%s", req.length(), req);

        byte[] result;
        try {
            result = resultStr.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            return null;
        }
        assert result.length == req.length() + 4;
        return result;
    }

    
    
}
