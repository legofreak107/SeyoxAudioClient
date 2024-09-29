package nl.seyox.data;

import lombok.Getter;

@Getter
public class AudioSettings {

    private String host;
    private int udpPort;
    private int tcpPort;

    public AudioSettings(String host, int udpPort, int tcpPort) {
        this.host = host;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
    }

}
