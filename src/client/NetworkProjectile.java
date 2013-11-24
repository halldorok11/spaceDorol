public class NetworkProjectile {
    public Point3D position;
    public Vector3D speed;
    public int team;
    private Point3D originalposition;


    public NetworkProjectile(Point3D position, Vector3D speed, int team) {
        this.originalposition = position;
        this.position = position;
        this.speed = speed;
        this.team = team;
    }

    public boolean update(){
        this.position.x += speed.x;
        this.position.y += speed.y;
        this.position.z += speed.z;

        if (Vector3D.difference(position,originalposition).length() > 2000) //if it has traveled more than then the length of two sectors.
        {
            return false; //indicating that it should be removed
        }
        return true;
    }

}