package com.example.bms.simulator.syslog;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RFC 3164 / RFC 5424 基础消息发送器。
 *
 * <p>参数：host port udp|tcp rfc3164|rfc5424 message。默认发送到 localhost:5514。</p>
 */
public final class SyslogSimulator {
    private SyslogSimulator() { }

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5514;
        String transport = args.length > 2 ? args[2] : "udp";
        String format = args.length > 3 ? args[3] : "rfc5424";
        // exec:java 会按空格拆分参数；合并剩余片段，确保包含空格的运维消息不被截断。
        String message = args.length > 4
                ? String.join(" ", java.util.Arrays.copyOfRange(args, 4, args.length))
                : "BMS simulator interface ge-0/0/0 changed state to down";
        String payload = format.equalsIgnoreCase("rfc3164")
                ? "<132>Jul 23 10:25:00 10.20.1.11 " + message
                : "<132>1 " + OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        + " 10.20.1.11 bms-simulator 1001 LINK - " + message;
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        if (transport.equalsIgnoreCase("tcp")) {
            try (Socket socket = new Socket(host, port);
                 OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(payload + "\n");
                writer.flush();
            }
        } else {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port));
            }
        }
        System.out.printf("sent format=%s transport=%s target=%s:%d bytes=%d%n", format, transport, host, port, bytes.length);
    }
}
