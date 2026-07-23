package com.example.bms.protocol.snmp;

import java.io.IOException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

/** SNMP4J v2c GET Adapter，显式设置 timeout/retry 并关闭 UDP transport。 */
@Component
public class SnmpV2cQueryClient implements SnmpQueryClient {
    @Override
    public SnmpGetResult get(SnmpGetRequest request) {
        long start = System.nanoTime();
        try (TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
             Snmp snmp = new Snmp(transport)) {
            transport.listen();
            CommunityTarget<UdpAddress> target = new CommunityTarget<>();
            target.setCommunity(new OctetString(request.community()));
            target.setAddress(new UdpAddress(request.host() + "/" + request.port()));
            target.setVersion(SnmpConstants.version2c);
            target.setTimeout(request.timeoutMillis());
            target.setRetries(request.retries());

            PDU pdu = new PDU();
            pdu.setType(PDU.GET);
            pdu.add(new VariableBinding(new OID(request.oid())));
            ResponseEvent<UdpAddress> response = snmp.send(pdu, target);
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            if (response == null || response.getResponse() == null) {
                return new SnmpGetResult(false, request.oid(), null, elapsed, "TIMEOUT",
                        "SNMP response was not received", request.retries());
            }
            PDU result = response.getResponse();
            if (result.getErrorStatus() != PDU.noError) {
                return new SnmpGetResult(false, request.oid(), null, elapsed, "SNMP_ERROR",
                        result.getErrorStatusText(), request.retries());
            }
            VariableBinding binding = result.get(0);
            return new SnmpGetResult(true, binding.getOid().toDottedString(), binding.getVariable().toString(),
                    elapsed, null, null, request.retries());
        } catch (IllegalArgumentException ex) {
            return failure(request, start, "INVALID_ADDRESS", ex);
        } catch (IOException ex) {
            return failure(request, start, "IO_ERROR", ex);
        }
    }

    private SnmpGetResult failure(SnmpGetRequest request, long start, String type, Exception ex) {
        return new SnmpGetResult(false, request.oid(), null, (System.nanoTime() - start) / 1_000_000,
                type, ex.getClass().getSimpleName() + ": " + ex.getMessage(), request.retries());
    }
}

