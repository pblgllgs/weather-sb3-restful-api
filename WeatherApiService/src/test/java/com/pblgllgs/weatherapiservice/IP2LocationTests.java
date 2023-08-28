package com.pblgllgs.weatherapiservice;


import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class IP2LocationTests {

    private final String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

    @Test
    void testInvalidIp() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "asd";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
        System.out.println(ipResult);
    }

    @Test
    void testInvalidIp1() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "45.79.204.110";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("Atlanta");
        System.out.println(ipResult);
    }

    @Test
    void testInvalidIp2() throws IOException {
        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(DBPath);

        String ipAddress = "102.217.236.255";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("Santiago");
        System.out.println(ipResult);
    }

}
