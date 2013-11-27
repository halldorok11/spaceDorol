/**
 * Created by: Halldór Örn Kristjánsson
 * Date: 11/6/13
 * Time: 1:14 PM
 */
public class Projectile{
    Point3D position;

    //Speed related:
    Vector3D speed;

    //team related
    public int team;

    private long starttime;
    
    public Projectile(Point3D position, Vector3D speed, int team) {
        this.position = new Point3D(position.x,position.y,position.z);
        this.speed = new Vector3D(speed.x, speed.y, speed.z);
        this.team = team;
        starttime = System.currentTimeMillis();
    }

    public boolean update(){
        position.add(speed);
        if (System.currentTimeMillis()-starttime > 15000){ //If it has been alive for more than 15 seconds
            return false;
        }
        return true;
    }
}
