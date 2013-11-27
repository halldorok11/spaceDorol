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
    public int claimedBy = 0; //0 for neutral, 1 for blue , 2 for red

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

    public boolean claimCheck(){
        int initialclaim = 10;
        int half = stars.size()/2;
        int red_count = 0;
        int blue_count = 0;
        for (Star s : stars){
            if (s.team == 1) blue_count++;
            if (s.team == 2) red_count++;
        }

        if (claimedBy == 0){ //unclaimed
            if (blue_count > initialclaim){
                claimedBy = 1;
                claimallstars(1);
                return true;
            }
            if (red_count > initialclaim){
                claimedBy = 2;
                claimallstars(2);
                return true;
            }
        }
        if (claimedBy == 1){ //claimed by blue
            if (red_count > half){
                claimedBy = 2;
                claimallstars(2);
                return true;
            }
        }
        if (claimedBy == 2){  //claimed by red
            if (blue_count > half){
                claimedBy = 1;
                claimallstars(1);
                return true;
            }
        }
        return false;
    }

    private void claimallstars(int team){
        for (Star s : stars){
            s.team = team;
        }
    }

    public void draw(StillModel neutralmodel,StillModel bluemodel, StillModel redmodel){
        for (Star s : stars){
            s.draw(neutralmodel,bluemodel,redmodel);
        }
    }
}
