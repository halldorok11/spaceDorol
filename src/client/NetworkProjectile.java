public class NetworkProjectile {
    public Point3D position;
    public Vector3D speed;
    public int team;
    private long starttime;


    public NetworkProjectile(Point3D position, Vector3D speed, int team) {
        this.position = position;
        this.speed = speed;
        this.team = team;
        starttime = System.currentTimeMillis();
    }

    public boolean update(){
        this.position.x += speed.x;
        this.position.y += speed.y;
        this.position.z += speed.z;

        if (System.currentTimeMillis()-starttime > 15000){ //If it has been alive for more than 15 seconds
            return false;
        }

            return true;
    }

}