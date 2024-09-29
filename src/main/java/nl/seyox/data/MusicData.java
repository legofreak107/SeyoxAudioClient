package nl.seyox.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicData {

    private String name;
    private String url;
    private double x;
    private double y;
    private double z;
    private int range = 100;

}
