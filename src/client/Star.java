import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public class Star{
    private float x;
    private float y;
    private float z;
    public int team = 0; //0 for neutral, 1 for blue, 2 for red
    private float size;

    public Star(float x, float y, float z, float size){
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    public void draw(StillModel model){
        Gdx.gl10.glPushMatrix();
        Gdx.gl10.glTranslatef(x, y,z);
        Gdx.gl10.glScalef(size,size,size);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
        model.render();
        Gdx.gl10.glPopMatrix();
    }
}

