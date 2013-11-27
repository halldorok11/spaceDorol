import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the "main" class that "plays" and runs the game.
 * @version 1.0
 * @author Halldór Örn Kristjánsson
 * @author Ólafur Daði Jónsson
 */
public class TheGame implements ApplicationListener, InputProcessor
{
    //networking:
    private NetworkThread network;

    //gamemode:
    GameState gameState;
    Random rand;

    //Controls
    private int sensitivity = 30;
    private int inverted = 1; //used for mouse camera controls

    //Space variables
    private int numberOfSectors = 5; //n x n x n
    private int totalSectors = numberOfSectors*numberOfSectors*numberOfSectors;
    private int starsInSector = 60; //30
    private int sectorSize = 2000;  //1000
    private int maxposition = (numberOfSectors-1)*sectorSize;
    private int minposition = 1*sectorSize;

    //Player 1
    private Player p1;
    private List<Projectile> projectiles;
    private long shottime;
    private long cooldowntime = 10000; //10.000 milliseconds to "respawn"
    private long timeleft;

    // text
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera secondCamera;

    //3D objects
    private Sector[][][] sectors;
    private StillModel shuttle;
    private StillModel neutralplanet;
    private StillModel blueplanet;
    private StillModel redplanet;
    private StillModel blueProjectileModel;
    private StillModel redProjectileModel;
    ObjLoader loader;

    //Different textures for different 3D objects
    private Texture shuttleTexture;
	private Texture backgroundTexture;

    //Game score
    private int blueScore = 0;
    private int redScore = 0;      // number of sectors the teams have claimed


    @Override
    /**
     * This function is run when the application starts
     * It does some initializing and first time settings
     */
    public void create() {
        // Kickstart the network thread
        network = new NetworkThread();
        network.start();

	    gameState = GameState.START;
        //second camera used to print text on the screen
        this.secondCamera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont();

        //handles keyboard input
        Gdx.input.setInputProcessor(this);

        //Catch the cursor
        Gdx.input.setCursorCatched(true);

        //Lights
        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
        Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);
        Gdx.gl11.glEnable(GL11.GL_LIGHT0);

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();

        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        Gdx.gl11.glEnable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        //assign images to the textures
        shuttleTexture = new Texture("graphics/cruiser/Textures/cruiser.png");
	    backgroundTexture = new Texture ("graphics/space_background.png");

	    loadModels();

        initialize();
    }

    /**
     * Loads the models to be used for drawing.
     */
    private void loadModels(){
        loader = new ObjLoader();
        FileHandle in;

        if (shuttle == null) {
            in = new FileHandle(Gdx.files.internal("graphics/cruiser/cruiser.obj").path());
            shuttle = loader.loadObj(in, true);
        }

        if (neutralplanet == null) {
            in = new FileHandle(Gdx.files.internal("graphics/Sphere/neutralmoon.obj").path());
            neutralplanet = loader.loadObj(in,true);
        }

        if (redplanet == null) {
            in = new FileHandle(Gdx.files.internal("graphics/Sphere/redmoon.obj").path());
            redplanet = loader.loadObj(in,true);
        }

        if (blueplanet == null) {
            in = new FileHandle(Gdx.files.internal("graphics/Sphere/bluemoon.obj").path());
            blueplanet = loader.loadObj(in,true);
        }

        if (redProjectileModel == null){
            in = new FileHandle(Gdx.files.internal("graphics/Sphere/redprojectile.obj").path());
            redProjectileModel = loader.loadObj(in, true);
        }

        if (blueProjectileModel == null){
            in = new FileHandle(Gdx.files.internal("graphics/Sphere/blueprojectile.obj").path());
            blueProjectileModel = loader.loadObj(in, true);
        }
    }

    /**
     * Initializes the Sectors and generates the stars
     */
    private void initialize(){
        rand = new Random(network.seed);

        p1 = new Player(new Point3D(0.0f, 0.0f, 0.0f), new Point3D(-2.0f, 0.0f, 0.0f), new Vector3D(0.0f, 1.0f, 0.0f), new Spaceship(shuttle,shuttleTexture));

        sectors = new Sector[numberOfSectors][numberOfSectors][numberOfSectors]; //n * n * n

        generateSectors();

        p1.eye.x = p1.eye.y = p1.eye.z = numberOfSectors*sectorSize/2;
        p1.speed = new Vector3D(0,0,0);
        projectiles = new ArrayList<Projectile>();
    }

    /**
     * This genereates all the sectors
     * Needs to be split in two:
     *      First we need to generate all the non-mirrors.
     *      Then generate all the mirrors.
     * If this would not be split then we would get a nullpointer exception trying to reference a mirror that doesn't exist
     */
    private void generateSectors(){
        for (int i = 1; i < numberOfSectors-1 ; i++){
            for (int j = 1; j < numberOfSectors-1 ; j++){
                for (int k = 1; k < numberOfSectors-1 ; k++){
                    makeRegularSector(i,j,k);
                }
            }
        }


        for (int i = 0; i < numberOfSectors; i++){
            for (int j = 0; j < numberOfSectors; j++){
                for (int k = 0; k < numberOfSectors; k++){
                    makeMirrorSector(i,j,k);
                }
            }
        }
    }

    /**
     * Creates a new sector in the sectors array with the indicated indices
     * @param x first index into sectors
     * @param y second index into sectors
     * @param z third index into sectors
     */
    private void makeRegularSector(int x, int y, int z){
        if (x >= 0 && y >= 0 && z >= 0){
            if (x < numberOfSectors && y < numberOfSectors && z < numberOfSectors){
                if (sectors[x][y][z] == null){
                    sectors[x][y][z] = new Sector(x*sectorSize, y*sectorSize, z*sectorSize, sectorSize, starsInSector, rand);
                }
            }
        }
    }

    /**
     * Makes a mirror sector in the sectors array with the indicated indices
     * @param x first index
     * @param y second index
     * @param z third index
     */
    private void makeMirrorSector(int x, int y, int z){
        if (x == 0 || y == 0 || z == 0 || x == numberOfSectors-1 || y == numberOfSectors-1 || z == numberOfSectors-1){
            if (sectors[x][y][z] == null){
                sectors[x][y][z] = new Sector(x*sectorSize, y*sectorSize, z*sectorSize, sectors[mirrorindex(x)][mirrorindex(y)][mirrorindex(z)]);
            }
        }
    }

    /**
     * This function changes an out of the box index to the mirror index
     * Here is the conversion table:
     * 0 -> numberofsectors-2
     * numberofserctors-1 -> 1
     *
     * @param i the index that is out of bound
     * @return the mirrored index
     */
    private int mirrorindex(int i){
        if (i <= 0) return numberOfSectors-2;
        if (i >= numberOfSectors-1) return 1;
        return i;
    }

    /**
     * Used to calculate the current sector of the player
     * @return the current sector
     */
    private Sector currentSector(){
        int x = currentXSector();
        int y = currentYSector();
        int z = currentZSector();

        if (x < 0 || x >= numberOfSectors) return null;
        if (y < 0 || y >= numberOfSectors) return null;
        if (z < 0 || z >= numberOfSectors) return null;

        return sectors[x][y][z];
    }

    /**
     * Returns at what index the current sector is
     * @return the index
     */
    private int currentSectorIndex(){
        int x = currentXSector();
        int y = currentYSector();
        int z = currentZSector();

        return x*numberOfSectors*numberOfSectors + y*numberOfSectors + z;
    }

    /**
     * Used to calculate the current x axis index of the current sector
     * @return the x axis index of the current sector
     */
    private int currentXSector(){
        return (int)p1.eye.x / sectorSize;
    }

    /**
     * Used to calculate the current y axis index of the current sector
     * @return the y axis index of the current sector
     */
    private int currentYSector(){
        return (int)p1.eye.y / sectorSize;
    }

    /**
     * Used to calculate the current y axis index of the current sector
     * @return the y axis index of the current sector
     */
    private int currentZSector(){
        return (int)p1.eye.z / sectorSize;
    }

    /**
     * Get the sector that contains the given coordinates
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return The sector that contains these coordinates
     */
    private Sector getSectorForAbsoluteCoordinates(float x, float y, float z)
    {
        int sectorX = (int)x/sectorSize;
        int sectorY = (int)y/sectorSize;
        int sectorZ = (int)z/sectorSize;

        if(sectorX >= 0 && sectorY >= 0 && sectorZ >= 0)
        {
            if(sectorX < numberOfSectors && sectorY < numberOfSectors && sectorZ < numberOfSectors)
                return sectors[sectorX][sectorY][sectorZ];
        }
        return null;
    }

    /**
     * This is called whenever the player presses the 'shoot' key.
     * Makes a new missile and sends it on it's way.
     */
    private void shoot(){
        if (p1.shot) return; //should not be able to shoot

        Vector3D forward = new Vector3D(-p1.n.x, -p1.n.y, -p1.n.z);
        forward.normalize();

        Point3D location = new Point3D(p1.eye.x + forward.x*11, p1.eye.y + forward.y*11, p1.eye.z + forward.z*11);

        Vector3D speed = Vector3D.sum(p1.speed, forward);

        projectiles.add(new Projectile(location,speed, p1.team));

        //Broadcast the new projectile
        String stringMessage = String.format("shot;%s;%s;%s,%s,%s,%s,%s", Integer.toString(p1.team), Float.toString(location.x),Float.toString(location.y),Float.toString(location.z),
                                                                       Float.toString(speed.x), Float.toString(speed.y), Float.toString(speed.z));
        this.network.sendMessage(stringMessage);
    }

    @Override
    /**
     * called when the application is closed
     */
    public void dispose() {
        shuttle.dispose();
        shuttleTexture.dispose();
        backgroundTexture.dispose();
    }

    @Override
    public void pause() {

    }

    /**
     * Updates all positions in the maze
     */
    private void update() {

        //gets the difference in time since the last update
        float deltaTime = Gdx.graphics.getDeltaTime();

        //the following functions all update the camera position depending on the key

        //turn to the left
        if(Gdx.input.isKeyPressed(Input.Keys.A))
            p1.roll(90f * deltaTime);

        //turn to the right
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            p1.roll(-90f * deltaTime);

        //slide forward
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            p1.slide(0.0f, 0.0f, -5.0f * deltaTime);
        }

        //slide backward
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            p1.slide(0.0f, 0.0f, 5.0f * deltaTime);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP))
            p1.pitch(90.0f * deltaTime * inverted);

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            p1.pitch(-90.0f * deltaTime * inverted);

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            p1.yaw(90.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            p1.yaw(-90.0f * deltaTime);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            p1.speed = new Vector3D(0,0,0);
        }
        p1.yaw(Gdx.input.getDeltaX()*deltaTime * sensitivity);
        p1.pitch(-Gdx.input.getDeltaY()*deltaTime * sensitivity * inverted);

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
            p1.eye.x = p1.eye.y = p1.eye.z = 0;
        }

        updateSector();
        updatePlayer();
        updateProjectiles();
        updateScore();
        hit();
        updateTime();

        if (p1.speed.length() > 0){
            //Broadcast the new position
            String stringMessage = String.format("move;%s;%s;%s", Float.toString(p1.eye.x),Float.toString(p1.eye.y),Float.toString(p1.eye.z));
            this.network.sendMessage(stringMessage);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.M)){
            gameState = GameState.MENU;
        }

        hit();
    }

    /**
     * Has all the sectors count their stars teams, and change the sector claim if it not same as last.
     */
    private void updateSector(){
        for (int i = 0; i < numberOfSectors; i++){
            for (int j = 0; j < numberOfSectors ; j++){
                for (int k = 0; k < numberOfSectors ; k++){
                    sectors[i][j][k].claimCheck();
                }
            }
        }
    }

    /**
     * Updates the player
     */
    private void updatePlayer(){
        p1.update();
        Playerextends();
        if (currentSector() != p1.currentSector){
            p1.currentSector = currentSector();
        }
    }

    /**
     * This function checks if the player is outside the boundary's and then ports him to the other side.
     */
    private void Playerextends(){
        extendPoint(p1.eye);
    }

    /**
     * Extends a point to be within the pre determined world
     * @param point the coordinate to be changed
     */
    private void extendPoint(Point3D point){
        if (point.x < minposition) point.x = maxposition;
        if (point.x > maxposition) point.x = minposition;
        if (point.y < minposition) point.y = maxposition;
        if (point.y > maxposition) point.y = minposition;
        if (point.z < minposition) point.z = maxposition;
        if (point.z > maxposition) point.z = minposition;
    }

    /**
     * Counts the claimed sectors for display
     */
    private void updateScore(){
        blueScore = 0;
        redScore = 0;
        for (int i = 0; i < numberOfSectors; i++){
            for (int j = 0; j < numberOfSectors ; j++){
                for (int k = 0; k < numberOfSectors ; k++){
                    if (sectors[i][j][k].claimedBy == 1){
                        blueScore += 1;
                    }
                    else if (sectors[i][j][k].claimedBy == 2){
                        redScore += 1;
                    }
                }
            }
        }
        victory();
    }

    private void victory(){
        if (blueScore >= 10 || redScore >= 10){
            network.sendMessage("newseed");
            gameState = GameState.VICTORY;
        }
    }

    /**
     * This function checks for any collision and updates the world accordingly
     */
    private void hit(){
        if (currentSector() == null || currentSector().mirror != null) return; //no collisions outside of the warzone
        //player to star collision
        collisionPlayerStar();

        //player to projectile collision
        collisionPlayerProjectile();

        //projectile to star collision
        collisionProjectileStar();
    }

    /**
     * Checks for collision between the player and a star.
     * Keeps the player at a minimum distance from a star.
     */
    private void collisionPlayerStar(){
        for (Star s : currentSector().stars){
            Vector3D diff = Vector3D.difference(p1.eye, s.pos);
            float len = diff.length();
            if (len < 15){
                diff.normalize();
                p1.eye.add(Vector3D.mult(15-len,diff));
            }
        }
    }

    /**
     * Checks for collision between a Player and a Projectile
     * Upon impact:
     *      Delete the missile and render the player unable to shoot for some time
     */
    private void collisionPlayerProjectile(){
        Sector playerSector = currentSector();
        Projectile theone = null;

        for(Projectile projectile : projectiles)
        {
            Sector projectileSector = getSectorForAbsoluteCoordinates(projectile.position.x,projectile.position.y,projectile.position.z);
            if (projectileSector == null) continue;
            if (playerSector == projectileSector){
                Vector3D diff = Vector3D.difference(projectile.position, p1.eye);
                float len = diff.length();
                if (len < 10){
                    p1.shot = true;
                    shottime = System.currentTimeMillis();
                    theone = projectile;
                }
            }
        }
        if (theone != null){
            projectiles.remove(theone);
        }

        //Network projectiles
        NetworkProjectile theotherone = null;
        for(NetworkProjectile projectile : NetworkGameState.instance().getProjectiles())
        {
            Sector projectileSector = getSectorForAbsoluteCoordinates(projectile.position.x,projectile.position.y,projectile.position.z);
            if (projectileSector == null) continue;
            if (playerSector == projectileSector){
                Vector3D diff = Vector3D.difference(projectile.position, p1.eye);
                float len = diff.length();
                if (len < 10){
                    p1.shot = true;
                    shottime = System.currentTimeMillis();
                    theotherone = projectile;
                }
            }
        }
        if (theotherone != null){
            NetworkGameState.instance().getProjectiles().remove(theone);
        }
    }

    /**
     * Checks for collision between a Projectile and a Star
     * Upon impact:
     *      Deletes the missile and paints the star with the missiles team color
     */
    private void collisionProjectileStar(){

	    List<Projectile> removeList =  new ArrayList<Projectile>();
	    List<NetworkProjectile> removeNetworkList = new ArrayList<NetworkProjectile>();

	    for(Projectile p : projectiles)
	    {
		    Sector s = getSectorForAbsoluteCoordinates(p.position.x,p.position.y,p.position.z);
            if (s == null || s.mirror != null) continue;
		    for(Star star : s.stars)
		    {
			    Vector3D diff = Vector3D.difference(p.position, star.pos);
			    float len = diff.length();
			    if (len < 9){
				    diff.normalize();
				    star.team = p.team;
				    removeList.add(p);
			    }
		    }
	    }
	    for(Projectile p : removeList)
	    {
		    projectiles.remove(p);
	    }

	   //Network projectiles

	    for(NetworkProjectile p : NetworkGameState.instance().getProjectiles())
	    {
		    Sector s = getSectorForAbsoluteCoordinates(p.position.x,p.position.y,p.position.z);
            if (s == null) continue;
		    for(Star star : s.stars)
		    {
			    Vector3D diff = Vector3D.difference(p.position, star.pos);
			    float len = diff.length();
			    if (len < 9){
				    diff.normalize();
				    star.team = p.team;
				    removeNetworkList.add(p);
			    }
		    }
	    }
	    for(NetworkProjectile p : removeNetworkList)
	    {
		    NetworkGameState.instance().getProjectiles().remove(p);
	    }
    }

    /**
     * If the player was shot, this function takes care of the countdown
     */
    private void updateTime(){
        if (!p1.shot) return;

        timeleft = cooldowntime-(System.currentTimeMillis()-shottime);

        if (timeleft < 0){
            p1.shot = false;
        }

    }


    /**
     * Parent function of all drawings
     */
    private void drawEnvironment() {
        drawSectors();
        drawProjectiles();
        drawPlayers();
    }

    /**
     * Draws a 3x3x3 cube of sectors around the player
     * for a total of 27 sectors drawn.
     */
    private void drawSectors(){
        int x = currentXSector();
        int y = currentYSector();
        int z = currentZSector();

        for (int i = x-1; i <= x+1; i++){
            for (int j = y-1; j <= y+1; j++){
                for (int k = z-1; k <= z+1; k++){
                    drawSector(i,j,k);
                }
            }
        }
    }

    /**
     * Draws a sector with the indices given
     * @param x x index of the sector
     * @param y y index of the sector
     * @param z z index of the sector
     */
    private void drawSector(int x, int y, int z){
        if (x >= 0 && y >= 0 && z >= 0){
            if (x < numberOfSectors && y < numberOfSectors && z < numberOfSectors){
                if (sectors[x][y][z] != null){
                    sectors[x][y][z].draw(neutralplanet,blueplanet,redplanet);
                }
            }
        }
    }

    /**
     * Updates the position of all missiles.
     * If they have traveled to far, they will be deleted.
     */
    private void updateProjectiles(){
        List<Integer> removables = new ArrayList<Integer>();

        for (Projectile p : projectiles){
            extendPoint(p.position);
            if (!p.update()){
                removables.add(projectiles.indexOf(p));
            }
        }
        for (int r : removables){
            projectiles.remove(r);
        }

        NetworkGameState.instance().updateProjectiles();
    }

    /**
     * Draws all missiles.
     */
    private void drawProjectiles(){
        for (Projectile p : projectiles){
            Gdx.gl10.glPushMatrix();
            Gdx.gl10.glTranslatef(p.position.x, p.position.y,p.position.z);
            Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
            if(p.team == 1) //blue
                blueProjectileModel.render();
            if(p.team == 2) //red
                redProjectileModel.render();
            Gdx.gl10.glPopMatrix();
        }

        for (NetworkProjectile p : NetworkGameState.instance().getProjectiles()){
            Gdx.gl10.glPushMatrix();
            Gdx.gl10.glTranslatef(p.position.x, p.position.y,p.position.z);
            Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
            if(p.team == 1) //blue
                blueProjectileModel.render();
            if(p.team == 2) //red
                redProjectileModel.render();
            Gdx.gl10.glPopMatrix();
        }
    }

    /**
     * This function does all the lighting and drawing of the program.
     */
    private void display() {
        Gdx.gl11.glClearColor(0f, 0f, 0f, 1.0f);
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        p1.setModelViewMatrix();

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();
        Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.3333f, 0.5f, sectorSize);

        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);

        // Configure light 0
        float[] lightDiffuse0 = {1f, 1f, 1f, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightDiffuse0, 0);

        float[] lightPosition0 = {p1.eye.x , p1.eye.y, p1.eye.z, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition0, 0);

        // Set material on all the things.
        float[] materialDiffuse;
        materialDiffuse = new float[]{1f, 1f, 1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT, materialDiffuse, 0);

        drawEnvironment();

        p1.draw();

        //Write the text on the screen:

        //lighting making a mess of the letters
        Gdx.gl11.glDisable(GL11.GL_LIGHTING);
        this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
        secondCamera.update();
        this.spriteBatch.begin();
        font.setColor(1f,1f,1f,1f);
        font.setScale(1,1);
        font.draw(this.spriteBatch, String.format("o"),-3,4);
        font.draw(this.spriteBatch, String.format("Current sector coordinates: (%d,%d,%d)", (int)p1.eye.x / sectorSize, (int)p1.eye.y / sectorSize, (int)p1.eye.z / sectorSize), -400, -300);
        font.draw(this.spriteBatch, String.format("Frames per second: %d", Gdx.graphics.getFramesPerSecond()), -400, -340);

        font.setColor(0.6f,0.6f,1f,1f);
        font.draw(this.spriteBatch, String.format("%d/10",blueScore),-40,400);
        font.setColor(1f,1f,1f,1f);
        font.draw(this.spriteBatch, String.format("::"), -1,400);
        font.setColor(1f,0.6f,0.6f,1f);
        font.draw(this.spriteBatch, String.format("%d/10", redScore), 20,400);

        if (p1.shot){
            font.setColor(1f,1f,1f,1f);
            font.draw(this.spriteBatch, String.format("You have been shot!"), -40, 100);
            font.draw(this.spriteBatch, String.format("Weapon system will be back online in %.2f seconds", (float)timeleft/1000), -120, 80);
        }

        this.spriteBatch.end();


        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Gets all the players on the network and draws them.
     */
    private void drawPlayers(){

        for (NetworkPlayer p : NetworkGameState.instance().getPlayers()){
            Gdx.gl10.glPushMatrix();
            Gdx.gl10.glTranslatef(p.position.x, p.position.y,p.position.z);
            //Gdx.gl10.glRotatef(180,0,0,0);
            Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
            //shuttleTexture.bind();
            shuttle.render();
            Gdx.gl10.glPopMatrix();
        }

    }

    @Override
    /**
     * The "loop", this is called indefinitely as the program runs.
     */
    public void render() {

	    switch(gameState)
	    {
		    case PLAYING:
			    update();
			    display();
			    break;

		    case MENU:
			    helpMenu();
			    //displayMenu();
			    break;

		    case START:
			    //update();
			    startMenu(); //..... for now
			    break;
            case VICTORY:
                VictoryScreen();
                break;
	    }
    }

    /**
     * Displays the in game help menu
     */
	public void helpMenu()
	{
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();


        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            inverted = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            inverted = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            gameState = GameState.PLAYING;
        }

        Gdx.gl11.glDisable(GL11.GL_LIGHTING);
        Gdx.gl11.glClearColor(0,0,0, 1);
	    Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		this.spriteBatch.begin();
		font.setColor(Color.WHITE);
		spriteBatch.draw(backgroundTexture, -width/2, -height/2,width, height); //the middle is at coords 0,0
        font.draw(this.spriteBatch, String.format("Press Q to continue game"), -100, 100);
        font.draw(this.spriteBatch, String.format("Keyboard controls:"), -300, 200);
        font.draw(this.spriteBatch, String.format("Forward: W"), -300, 180);
        font.draw(this.spriteBatch, String.format("Backward: S"), -300, 160);
        font.draw(this.spriteBatch, String.format("Roll right: D"), -300, 140);
        font.draw(this.spriteBatch, String.format("Roll left: A"), -300, 120);
        font.draw(this.spriteBatch, String.format("Mouse controls: "), -300, 80);
        font.draw(this.spriteBatch, String.format("Look up: Mouse up"), -300, 60);
        font.draw(this.spriteBatch, String.format("Look down: Mouse down"), -300, 40);
        font.draw(this.spriteBatch, String.format("Look right: Mouse right"), -300, 20);
        font.draw(this.spriteBatch, String.format("Look left: Mouse left"), -300, 0);

        if (inverted == 1) font.setColor(Color.RED);
        else if (inverted == -1) font.setColor(Color.GREEN);
		font.draw(this.spriteBatch, String.format("Press 1/2 to toggle on/off inverse vertical mouse control"), -100, 0);

		this.spriteBatch.end();
        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
	}

    /**
     * Displays the start menu
     */
	public void startMenu()
	{
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        Gdx.gl11.glDisable(GL11.GL_LIGHTING);
		Gdx.gl11.glClearColor(0,0,0,1);
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
        secondCamera.update();

		this.spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, -width/2, -height/2,width, height); //the middle is at coords 0,0
		font.setColor(1, 1, 1, 1f);
		font.draw(this.spriteBatch, String.format("WELCOME TO SPACE-DOROL!"), -100, 200);
        font.setColor(0.6f, 0.6f, 1, 1f);
        font.draw(this.spriteBatch, String.format("Press B to join the game as a member of the blue team"), -430, 150);
        font.setColor(1, 0.6f, 0.6f, 1f);
		font.draw(this.spriteBatch, String.format("Press R to join the game as a member of the red team"), -430, 100);
        font.setColor(1, 1, 1, 1f);
		font.draw(this.spriteBatch, String.format("Use W-A-S-D along with the mouse to navigate in space"), -430, 50);
		font.draw(this.spriteBatch, String.format("Press 'M' for in-game options!"), -430, 0);
		this.spriteBatch.end();

        if(Gdx.input.isKeyPressed(Input.Keys.B)){
            this.gameState = GameState.PLAYING;
            p1.team = 1; //blue
            String s = String.format("team;%s",Integer.toString(p1.team));
            network.sendMessage(s);
        }

		if(Gdx.input.isKeyPressed(Input.Keys.R)){
            this.gameState = GameState.PLAYING;
            p1.team = 2; //red
            String s = String.format("team;%s",Integer.toString(p1.team));
            network.sendMessage(s);
        }

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);

	}

    private void VictoryScreen(){
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        Gdx.gl11.glDisable(GL11.GL_LIGHTING);
        Gdx.gl11.glClearColor(0,0,0,1);
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        this.spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, -width/2, -height/2,width, height); //the middle is at coords 0,0

        if (blueScore >= 10)
            font.setColor(0.6f, 0.6f, 1f, 1f);
        if (redScore >= 10)
            font.setColor(1f, 0.6f, 0.6f, 1f);

        font.draw(this.spriteBatch, String.format("VICTORY!"), -100, 200);

        if (blueScore >= 10)
            font.draw(this.spriteBatch, String.format("The Blue team won the game"),-150, 0);
        if (redScore >= 10)
            font.draw(this.spriteBatch, String.format("The Red team won the game"), -150, 0);

        font.draw(this.spriteBatch, String.format("Press 'Q' to quit to main menu and play another round"), -170, -20);
        this.spriteBatch.end();

        if(Gdx.input.isKeyPressed(Input.Keys.Q)){
            initialize();
            this.gameState = GameState.START;
        }

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public void resize(int arg0, int arg1) {
    }

    @Override
    public void resume() {
    }

    @Override
    /**
     * This is called anytime some key is pressed down.
     * @param arg0 The key pressed
     */
    public boolean keyDown(int arg0) {
        if (arg0 == Input.Keys.ESCAPE){
            Gdx.app.exit();
        }

        return false;
    }

    @Override
    public boolean keyTyped(char arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int arg0, int arg1) {
        return false;
    }

    @Override
    public boolean scrolled(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
        if (gameState == GameState.PLAYING){
            this.shoot();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        return false;
    }
}