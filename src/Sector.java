import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sector {
    public int x;
    public int y;
    public int z;
    private int size;
    private List<Star> stars;
    private Random rand;
    private Texture tex;

    public Sector(int x, int y, int z,int size,int nr_of_stars, Texture tex){
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.tex = tex;
        rand = new Random();
        generateStars(nr_of_stars);
    }

    private void generateStars(int nr_of_stars){
        stars = new ArrayList<Star>();
        for (int i = 0; i < nr_of_stars; i++){
            stars.add(new Star(rand.nextFloat()*size + x, rand.nextFloat()*size + y, rand.nextFloat()*size + z, 8f, tex));
        }
    }

    public void draw(){
        for (Star s : stars){
            s.draw();
        }
    }
}
