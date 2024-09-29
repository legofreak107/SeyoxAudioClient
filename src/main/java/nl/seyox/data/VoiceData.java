package nl.seyox.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoiceData {

    private String playerId;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean muted;
    private String name;

}
