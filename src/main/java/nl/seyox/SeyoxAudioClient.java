package nl.seyox;

import com.google.gson.Gson;
import nl.seyox.data.AudioSettings;
import nl.seyox.data.MusicData;
import nl.seyox.data.VoiceData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.CompletableFuture;

public class SeyoxAudioClient {

    private DatagramSocket udpSocket;
    private InetAddress serverAddress;

    private AudioSettings settings;
    private Gson gson;

    /**
     * Enable the audio client
     *
     * @throws SocketException      if the socket cannot be created
     * @throws UnknownHostException if the host cannot be resolved
     */
    public void enable(AudioSettings settings) throws SocketException, UnknownHostException {
        gson = new Gson();
        this.settings = settings;
        serverAddress = InetAddress.getByName(settings.getHost());  // Rust server's IP address
        udpSocket = new DatagramSocket();
    }


    /**
     * Play music on the audio client for all connected players in range
     *
     * @param data the music data for the audio to play
     *             name: the name of the music
     *             url: the url of the music (mp3)
     *             x: the x position of the music
     *             y: the y position of the music
     *             z: the z position of the music
     *             range: the range in which the music should be played
     */
    public void playMusic(MusicData data) {
        try (Socket socket = new Socket(serverAddress, settings.getTcpPort())) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String json = String.format(gson.toJson(data));

            out.println(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handshake with the audio server
     * handshake is used to register the player with a given session key
     *
     * @param uuid       the uuid of the player
     * @param sessionKey the session key of the player can be anything. But a default uuid will work.
     * @return the url for the audio server encoded with data of the session
     */
    public CompletableFuture<String> handshakeWithAudioServer(String uuid, String sessionKey) {
        return CompletableFuture.supplyAsync(() -> {
            try (Socket socket = new Socket(serverAddress, settings.getTcpPort())) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String json = String.format("{\"playerId\": \"%s\", \"sessionKey\": \"%s\"}",
                        uuid, sessionKey);

                out.println(json);
                String response = in.readLine();
                if (response.startsWith("OK;")) {
                    // Handshake successful
                    return response.substring(3);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return "";
        });
    }

    /**
     * Update the player's position
     *
     * @param data the voice data for the player
     *             playerId: the uuid of the player
     *             x: the x position of the player
     *             y: the y position of the player
     *             z: the z position of the player
     *             yaw: the yaw of the player
     *             pitch: the pitch of the player
     *             muted: whether the player is muted or not
     *             name: the name of the player
     */
    public void updatePlayerData(VoiceData data) throws IOException {
        String message = gson.toJson(data);

        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, settings.getUdpPort());
        udpSocket.send(packet);
    }

}
