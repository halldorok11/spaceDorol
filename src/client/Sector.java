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
    public int claimedBy = 0; //1 for blue , 2 for red

    public Sector(int x, int y, int z,int size,int nr_of_stars){
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        rand = new Random();
        generateStars(nr_of_stars);
    }

	public Sector(Sector anotherSector, int x, int y, int z)
	{
		this.stars = anotherSector.stars;
		this.size = anotherSector.size;
		this.x = x;
		this.y = y;
		this.z = z;
	}

    private void generateStars(int nr_of_stars){
        stars = new ArrayList<Star>();
        for (int i = 0; i < nr_of_stars; i++){
            stars.add(new Star(rand.nextFloat()*size + x, rand.nextFloat()*size + y, rand.nextFloat()*size + z, 8f));
        }
    }

    public void draw(StillModel neutralmodel,StillModel bluemodel, StillModel redmodel){
        for (Star s : stars){
            s.draw(neutralmodel,bluemodel,redmodel);
        }
    }
}
