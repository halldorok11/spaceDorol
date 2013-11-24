public class NetworkPlayer {
    public Point3D position;
    public int team = 0;

    public NetworkPlayer(){
        this.position = new Point3D(0,0,0);
    }

    public void update(float x, float y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

}