import com.badlogic.gdx.graphics.Texture;

/**
 * Created by: Halldór Örn Kristjánsson
 * Date: 11/6/13
 * Time: 1:14 PM
 */
public class Projectile extends Cube {
    Point3D originalPosition;
    Point3D position;

    //Speed related:
    Vector3D speed;
    
    public Projectile(Point3D position, Vector3D speed) {
        this.originalPosition = position;
        this.position = new Point3D(position.x,position.y,position.z);
        this.speed = new Vector3D(speed.x, speed.y, speed.z);
    }

    public boolean update(){
        position.add(speed);
        if (Vector3D.difference(position,originalPosition).length() > 2000){ //if it has travelled to far,
            return false;
        }
        return true;
    }
}
