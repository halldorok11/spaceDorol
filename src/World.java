import java.util.List;

public class World {
    public Sector[][][] sectors;
    public List<Player> players;
    private int size;

    public World(int size){
        this.size = size;
        sectors = new Sector[size][size][size];
    }

    public void update(){

    }
}
