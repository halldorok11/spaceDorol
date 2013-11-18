import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public class Star extends Sphere{
    private float x;
    private float y;
    private float z;
    private float size;
    private Texture tex;

    public Star(float x, float y, float z, float size, Texture tex){
        super(10,30);
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.tex = tex;
    }

    public void draw(){
        Gdx.gl10.glPushMatrix();
        Gdx.gl10.glTranslatef(x, y,z);
        Gdx.gl10.glScalef(size,size,size);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
        tex.bind();
        super.draw();
        Gdx.gl10.glPopMatrix();
    }
}
