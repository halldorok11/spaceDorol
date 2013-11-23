public class NetworkProjectile {
    public Point3D position;
    public Vector3D speed;
    private Point3D originalposition;

    public NetworkProjectile(Point3D position, Vector3D speed) {
        this.originalposition = position;
        this.position = position;
        this.speed = speed;
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