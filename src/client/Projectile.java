import com.badlogic.gdx.graphics.Texture;

/**
 * Created by: Halldór Örn Kristjánsson
 * Date: 11/6/13
 * Time: 1:14 PM
 */
public class Projectile{
    Point3D originalPosition;
    Point3D position;

    //Speed related:
    Vector3D speed;

    //team related
    public int team;
    
    public Projectile(Point3D position, Vector3D speed, int team) {
        this.originalPosition = position;
        this.position = new Point3D(position.x,position.y,position.z);
        this.speed = new Vector3D(speed.x, speed.y, speed.z);
        this.team = team;
    }

    public boolean update(){
        position.add(speed);
        if (Vector3D.difference(position,originalPosition).length() > 1000){ //if it has travelled to far,
            return false;
        }
        return true;
    }
}
