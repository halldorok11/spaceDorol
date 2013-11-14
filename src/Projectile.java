import com.badlogic.gdx.graphics.Texture;

/**
 * Created by: Halldór Örn Kristjánsson
 * Date: 11/6/13
 * Time: 1:14 PM
 */
public class Projectile extends Cube {
    Point3D eye;

    //Speed related:
    Vector3D speed;

    Texture tex;

    public Projectile(Point3D eye, Vector3D speed, Texture tex) {
        this.eye = new Point3D(eye.x,eye.y,eye.z);
        this.speed = new Vector3D(speed.x, speed.y, speed.z);
        this.tex = tex;
    }

    public void updatePosition(){
        eye.add(speed);
    }

    public void draw(){
        draw(4,4,4,eye.x,eye.y,eye.z,0,0,0,0,tex);
    }
}
