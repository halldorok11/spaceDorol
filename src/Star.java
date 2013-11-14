import com.badlogic.gdx.graphics.Texture;

public class Star extends Cube{
    private float x;
    private float y;
    private float z;
    private float size;
    private Texture tex;

    public Star(float x, float y, float z, float size, Texture tex){
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.tex = tex;
    }

    public void draw(){
        draw(size,size,size,x,y,z,0,0,0,0,tex);
    }
}
