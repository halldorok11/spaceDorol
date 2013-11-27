import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sector {
    public int x;
    public int y;
    public int z;
    private int xOffset;
    private int yOffset;
    private int zOffset;
    public List<Star> stars;
    private int size;
    private Texture tex;
    private StillModel model;
    public int claimedBy = 0; //0 for neutral, 1 for blue , 2 for red
    public Sector mirror = null;

    public Sector(int x, int y, int z, Sector mirror){
        this.x = x;
        this.y = y;
        this.z = z;
        this.mirror = mirror;
        xOffset = this.x - mirror.x;
        yOffset = this.y - mirror.y;
        zOffset = this.z - mirror.z;
    }

    public Sector(int x, int y, int z,int size,int nr_of_stars, Random rand){
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        generateStars(nr_of_stars,rand);
    }

    private void generateStars(int nr_of_stars, Random rand){
        stars = new ArrayList<Star>();
        for (int i = 0; i < nr_of_stars; i++){
            stars.add(new Star(rand.nextFloat()*size + x, rand.nextFloat()*size + y, rand.nextFloat()*size + z, 8f));
        }
    }

    public boolean claimCheck(){
        if (mirror != null) return false; //this is a mirror sector
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
        if (mirror == null){
            for (Star s : stars){
                s.draw(neutralmodel,bluemodel,redmodel);
            }
        }
        else {
            mirror.drawoffset(xOffset,yOffset,zOffset,neutralmodel,bluemodel,redmodel);
        }
    }

    public void drawoffset(int x, int y, int z, StillModel neutralmodel,StillModel bluemodel, StillModel redmodel){
        for (Star s : stars){
            s.drawoffset(x,y,z,neutralmodel,bluemodel,redmodel);
        }
    }
}
