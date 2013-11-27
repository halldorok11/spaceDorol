
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.Gdx;

/**
 * The player class
 * Holds all information needed for a new player
 */
public class Player
{
    //Position
	Point3D eye;
	Vector3D u;
	Vector3D v;
	Vector3D n;
    float x_angle = 0;
    float y_angle = 0;
    float z_angle = 0;
    float angle = 0;
    float x_rotation = 0;
    float y_rotation = 0;
    float z_rotation = 0;
    Sector currentSector;

    //Speed related:
    Vector3D speed;
    int maxspeed = 2;

    //model
    Spaceship ship;

    public int team = 0;
    public boolean shot = false;

	public Player(Point3D pEye, Point3D pLookat, Vector3D up) {
		eye = pEye;
		n = Vector3D.difference(pEye, pLookat);
		n.normalize();
		u = Vector3D.cross(up, n);
		u.normalize();
		v = Vector3D.cross(n, u);

        speed = new Vector3D(0,0,0);
	}
	
	public void setModelViewMatrix() {
		Vector3D minusEye = Vector3D.difference(new Point3D(0,0,0), eye);
		
		float[] matrix = new float[16];
		matrix[0] = u.x;	matrix[4] = u.y;	matrix[8] = u.z;	matrix[12] = Vector3D.dot(minusEye, u);
		matrix[1] = v.x;	matrix[5] = v.y;	matrix[9] = v.z;	matrix[13] = Vector3D.dot(minusEye, v);
		matrix[2] = n.x;	matrix[6] = n.y;	matrix[10] = n.z;	matrix[14] = Vector3D.dot(minusEye, n);
		matrix[3] = 0;		matrix[7] = 0;		matrix[11] = 0;		matrix[15] = 1;
		
		Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
		Gdx.gl11.glLoadMatrixf(matrix, 0);
	}
	
	public void slide(float delU, float delV, float delN) {
		speed = Vector3D.sum(Vector3D.sum(Vector3D.mult(delU, u), Vector3D.sum(Vector3D.mult(delV, v), Vector3D.mult(delN, n))),speed);

        if (speed.length() > maxspeed){
            speed.normalize();
            speed = Vector3D.mult(maxspeed,speed);
        }
	}

	public void yaw(float angle) {
		float c = (float) Math.cos(angle*Math.PI/180.0f);
		float s = (float) Math.sin(angle*Math.PI/180.0f);
		Vector3D t = u;
		u = Vector3D.sum(Vector3D.mult(c, t), Vector3D.mult(s, n));
		n = Vector3D.sum(Vector3D.mult(-s, t), Vector3D.mult(c, n));
	}

	public void roll(float angle) {
		float c = (float) Math.cos(angle*Math.PI/180.0f);
		float s = (float) Math.sin(angle*Math.PI/180.0f);
		Vector3D t = u;
		u = Vector3D.sum(Vector3D.mult(c, t), Vector3D.mult(s, v));
		v = Vector3D.sum(Vector3D.mult(-s, t), Vector3D.mult(c, v));
	}

	public void pitch(float angle) {
		float c = (float) Math.cos(angle*Math.PI/180.0f);
		float s = (float) Math.sin(angle*Math.PI/180.0f);
		Vector3D t = v;
		v = Vector3D.sum(Vector3D.mult(c, t), Vector3D.mult(s, n));
		n = Vector3D.sum(Vector3D.mult(-s, t), Vector3D.mult(c, n));
	}

    public void update(){
        eye.add(speed);
        angle = x_angle + y_angle + z_angle;
        x_rotation = angle/x_angle;
        y_rotation = angle/y_angle;
        z_rotation = angle/z_angle;
    }

    public void draw(){
        Gdx.gl10.glPushMatrix();
        //Gdx.gl10.glTranslatef(eye.x, eye.y ,eye.z);
        Gdx.gl10.glTranslatef(50000, 50000 ,50000);
        Gdx.gl10.glRotatef(angle,x_rotation,y_rotation,z_rotation);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
        ship.tex.bind();
        ship.model.render();
        Gdx.gl10.glPopMatrix();
    }
}
