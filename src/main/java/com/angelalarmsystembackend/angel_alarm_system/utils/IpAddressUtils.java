package com.angelalarmsystembackend.angel_alarm_system.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpAddressUtils {

    public static boolean isLocalIpAddress(String ipAddressString) {
        try {
            InetAddress address = InetAddress.getByName(ipAddressString);

            // 1. Check for special local/loopback addresses
            if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
                return true;
            }

            // 2. Check if the address is assigned to any local network interface
            // This covers private IP ranges (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16)
            // and link-local addresses (169.254.0.0/16)
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress localAddress = inetAddresses.nextElement();
                    if (localAddress.equals(address)) {
                        return true;
                    }
                }
            }

            return false;

        } catch (UnknownHostException e) {
            // The provided IP address string is not a valid host
            System.err.println("Invalid IP address string: " + ipAddressString);
            return false;
        } catch (SocketException e) {
            // Error accessing network interfaces
            System.err.println("Error accessing network interfaces: " + e.getMessage());
            return false;
        }
    }
}