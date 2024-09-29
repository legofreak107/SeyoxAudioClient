# Seyox Audio Client

This is a Java library for connecting to the Seyox Audio Server.

## Usage

### Repository

Add the repository to your pom.xml

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

### Maven

```xml
<dependency>
    <groupId>com.github.legofreak107</groupId>
    <artifactId>SeyoxAudioClient</artifactId>
    <version>79c38b1bbf</version>
</dependency>
```

### Gradle

```groovy
implementation 'nl.seyox:SeyoxAudioClient:1.0-SNAPSHOT'
```

### Example

```java
import nl.seyox.SeyoxAudioClient;
import nl.seyox.data.AudioSettings;
import nl.seyox.data.MusicData;
import nl.seyox.data.VoiceData;

public class Example {

    private SeyoxAudioClient audioClient;

    public static void main(String[] args) {
        // Create the audio settings
        AudioSettings settings = new AudioSettings("host", 8080, 8081);

        // Create the audio client
        audioClient = new SeyoxAudioClient();

        // Enable the audio client
        try {
            audioClient.enable(settings);
            audioClient.playMusic(new MusicData("Rotterdam", "https://audio.seyox.nl/assets/Rotterdam.mp3", 0, 0, 0, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the update loop
        startUpdatingPlayerPositions();
    }

    public static void registerPlayer(Player player) {
        AudioModule.getInstance().handshakeWithAudioServer(player.getUniqueId().toString(), UUID.randomUuid().toString()).thenAccept(url -> {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<color:" + ApiSettings.getColor() + "><u><click:open_url:'" + url + "'>Click to connect to the audio server!</click></u></color>"));
        });
    }

    public static void startUpdatingPlayerPositions() {
        // Create a new thread to update the player positions
        new Thread(() -> {

            // Start the update loop
            while (true) {
                try {
                    // Sleep for 50 milliseconds, update will be every tick
                    Thread.sleep(50);

                    // Update all players
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        audioClient.updatePlayerData(
                                new VoiceData(
                                        player.getUniqueId().toString(),
                                        player.getLocation().getX(),
                                        player.getLocation().getY(),
                                        player.getLocation().getZ(),
                                        player.getLocation().getYaw(),
                                        player.getLocation().getPitch(),
                                        false,
                                        player.getName())
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
```
