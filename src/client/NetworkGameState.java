import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NetworkGameState {

    private static NetworkGameState instance;
    private HashMap<String, NetworkPlayer> players;
    public String clientNickName;

    public static NetworkGameState instance(){
        if(instance == null)
            instance = new NetworkGameState();
        return instance;
    }

    private NetworkGameState() {
        this.players = new HashMap<String, NetworkPlayer>();
    }

    public synchronized void removePlayer(String name){
        this.players.remove(name);
    }

    public synchronized void addPlayer(String name) {
        this.players.put(name, new NetworkPlayer());
    }

    public void updatePlayer(String name, float x, float y, float z) {
        if(this.players.containsKey(name))
            this.players.get(name).update(x, y, z);
    }

    public synchronized List<NetworkPlayer> getPlayers(){
        LinkedList<NetworkPlayer> players = new LinkedList<NetworkPlayer>();
        for(NetworkPlayer p : this.players.values())
            players.add(p);
        return players;
    }
}