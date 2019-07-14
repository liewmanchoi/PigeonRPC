package com.liewmanchoi.pigeon.rpc.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author wangsheng
 * @date 2019/6/24
 */
//@Slf4j
public class IpAddressUtils {

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nextElement = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (nextElement.isLoopback() || !nextElement.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = nextElement.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // *EDIT*
                    if (addr instanceof Inet6Address) {
                        continue;
                    }

                    return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
//            log.error("failed to get IP address: ", e);
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(getIpAddress());
    }
}
