import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public class Star{
    public Point3D pos;
    public int team = 0; //0 for neutral, 1 for blue, 2 for red
    private float size;

    public Star(float x, float y, float z, float size){
        pos = new Point3D(x,y,z);
        this.size = size;
    }

    public void draw(StillModel neutral, StillModel blue, StillModel red){
        Gdx.gl10.glPushMatrix();
        Gdx.gl10.glTranslatef(pos.x, pos.y, pos.z);
        Gdx.gl10.glScalef(size,size,size);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);

        switch (team){
            case 0:
                //neutral
                neutral.render();
                break;
            case 1:
                //blue
                blue.render();
                break;
            case 2:
                //red
                red.render();
                break;
        }
        Gdx.gl10.glPopMatrix();
    }

    public void drawoffset(int x, int y, int z, StillModel neutral, StillModel blue, StillModel red){
        Gdx.gl10.glPushMatrix();
        Gdx.gl10.glTranslatef(pos.x + x, pos.y + y, pos.z + z);
        Gdx.gl10.glScalef(size,size,size);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);

        switch (team){
            case 0:
                //neutral
                neutral.render();
                break;
            case 1:
                //blue
                blue.render();
                break;
            case 2:
                //red
                red.render();
                break;
        }
        Gdx.gl10.glPopMatrix();
    }
}

