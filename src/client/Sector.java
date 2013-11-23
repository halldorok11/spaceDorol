import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sector {
    public int x;
    public int y;
    public int z;
    public List<Star> stars;
    private int size;
    private Random rand;
    private Texture tex;
    private StillModel model;

    public Sector(int x, int y, int z,int size,int nr_of_stars){
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        rand = new Random();
        generateStars(nr_of_stars);
    }

    private void generateStars(int nr_of_stars){
        stars = new ArrayList<Star>();
        for (int i = 0; i < nr_of_stars; i++){
            stars.add(new Star(rand.nextFloat()*size + x, rand.nextFloat()*size + y, rand.nextFloat()*size + z, 8f));
        }
    }

    public void draw(StillModel neutralmodel,StillModel bluemodel, StillModel redmodel){
        for (Star s : stars){
            if (s.team == 0) // neutral
                s.draw(neutralmodel);
            else if (s.team == 1) //blue
                s.draw(bluemodel);
            else if (s.team == 2) //red
                s.draw(redmodel);
        }
    }
}
