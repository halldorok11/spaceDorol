import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NetworkGameState {

    private static NetworkGameState instance;
    private HashMap<String, NetworkPlayer> players;
    private List<NetworkProjectile> projectiles;
    public String clientNickName;

    public static NetworkGameState instance(){
        if(instance == null)
            instance = new NetworkGameState();
        return instance;
    }

    private NetworkGameState() {
        this.players = new HashMap<String, NetworkPlayer>();
        this.projectiles = new ArrayList<NetworkProjectile>();
    }

    public synchronized void removePlayer(String name){
        this.players.remove(name);
    }

    public synchronized void removeProjectile(String id){
        this.projectiles.remove(id);
    }

    public synchronized void addPlayer(String name) {
        this.players.put(name, new NetworkPlayer());
    }

    public synchronized void addProjectile(Point3D pos, Vector3D speed) {
        this.projectiles.add(new NetworkProjectile(pos,speed));
    }

    public void updatePlayer(String name, float x, float y, float z) {
        if(this.players.containsKey(name))
            this.players.get(name).update(x, y, z);
    }

    public void updateProjectiles(){
        for (NetworkProjectile p : this.projectiles){
            if (!p.update()){
                //If the projectile has traveled to far.
                projectiles.remove(p);
            }
            //otherwise we will continue aftuer updateing the missile position.
        }
    }

    public synchronized List<NetworkPlayer> getPlayers(){
        LinkedList<NetworkPlayer> players = new LinkedList<NetworkPlayer>();
        for(NetworkPlayer p : this.players.values())
            players.add(p);
        return players;
    }

    public synchronized  List<NetworkProjectile> getProjectiles(){
        return projectiles;

    }
}