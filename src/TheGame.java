import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.io.InputStream;

/**
 * This is the "main" class that "plays" and runs the game.
 * @version 1.0
 * @author Halldór Örn Kristjánsson
 * @author Ólafur Daði Jónsson
 */
public class TheGame implements ApplicationListener, InputProcessor
{
    //gamemode:
    boolean playing = true;

    //Controls
    private int sensitivity = 30;

    //Space variables
    private int numberOfSectors = 100; //10x10x10
    private int totalSectors = numberOfSectors*numberOfSectors*numberOfSectors;
    private int starsInSector = 30;
    private int sectorSize = 1000;

    //Player 1
    private Player p1;

    private List<Projectile> projectiles;

    // text
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera secondCamera;

    //3D objects
    private Sector[][][] sectors;
    private Model shuttle;
    private Model planet;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private ModelBatch modelBatch;
	private ModelLoader loader;

    //Different textures for different 3D objects
    private Texture projectileTexture;
    private Texture starTexture;
    private Texture shuttleTexture;
    private Texture planetTexture;

	//Camera stuff
	private PerspectiveCamera cam;
	private CameraInputController cameraInputController;

    @Override
    /**
     * This function is run when the application starts
     * It does some initializing and first time settings
     */
    public void create() {
        //second camera used to print text on the screen
        this.secondCamera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        this.spriteBatch = new SpriteBatch();
	    this.modelBatch = new ModelBatch();

	    cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    cam.position.set(50000f,50000f,50000f);
	    cam.lookAt(0, 0, 0);
	    cam.near = 0.5f;
	    cam.far = sectorSize;
	    cam.update();

	    cameraInputController = new CameraInputController(cam);
	    Gdx.input.setInputProcessor(cameraInputController);


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

        //assign images to the textures
        projectileTexture = new Texture("graphics/water.jpg");
        starTexture = new Texture("graphics/white.jpg");
        shuttleTexture = new Texture("graphics/cruiser/Textures/cruiser.png");
        planetTexture = new Texture("graphics/Moon/moon.png");

        //camera
        p1 = new Player(new Point3D(0.0f, 3.0f, 2.0f), new Point3D(2.0f, 3.0f, 3.0f), new Vector3D(0.0f, 1.0f, 0.0f), new Spaceship(shuttle,shuttleTexture));

        loadModels();
        initialize();
    }

    private void loadModels(){
        loader = new ObjLoader();
        FileHandle in;

        if (shuttle == null) {
	        shuttle = loader.loadModel(Gdx.files.internal("graphics/cruiser/cruiser.obj"));
	        ModelInstance shuttleInstance = new ModelInstance(shuttle);
	        instances.add(shuttleInstance);
            //in = new FileHandle(Gdx.files.internal("graphics/cruiser/cruiser.obj").path());
            //shuttle = loader.loadObj(in, true);
        }

        if (planet == null) {
	        planet = loader.loadModel(Gdx.files.internal("graphics/Moon/blendermoon.obj"));
	        ModelInstance planetInstance = new ModelInstance(planet);
	        instances.add(planetInstance);
	        //in = new FileHandle(Gdx.files.internal("graphics/Moon/blendermoon.obj").path());
            //planet = loader.loadObj(in,true);
        }
    }

    private void initialize(){
        sectors = new Sector[numberOfSectors][numberOfSectors][numberOfSectors]; //n * n * n

        int i = numberOfSectors/2;

        sectors[i][i][i] = new Sector(i*sectorSize,i*sectorSize,i*sectorSize,sectorSize,starsInSector,planetTexture);
        generateSector(sectors[i][i][i]);

        p1.eye.x = p1.eye.y = p1.eye.z = numberOfSectors*sectorSize/2;
        p1.currentSector = sectors[i][i][i];
        projectiles = new ArrayList<Projectile>();

    }

    /**
     * This should generate all the neighbour sectors to this one, completing a cube of sectors around it.
     * @param sec the sector to be generated from
     */
    private void generateSector(Sector sec){
        if (sec == null) return;

        int x = sec.x/sectorSize;
        int y = sec.y/sectorSize;
        int z = sec.z/sectorSize;

        for (int i = x-1; i <= x+1; i++){
            for (int j = y-1; j <= y+1; j++){
                for (int k = z-1; k <= z+1; k++){
                    makeNewSector(i,j,k);
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
    private void makeNewSector(int x, int y, int z){
        if (x > 0 && y > 0 && z > 0){
            if (x < numberOfSectors-1 && y < numberOfSectors-1 && z < numberOfSectors-1){
                if (sectors[x][y][z] == null){
                    sectors[x][y][z] = new Sector(x*sectorSize, y*sectorSize, z*sectorSize, sectorSize, starsInSector, planetTexture);
                }
            }
        }
    }
    
    private Sector currentSector(){
        int x = currentXSector();
        int y = currentYSector();
        int z = currentZSector();

        return sectors[x][y][z];
    }

    private int currentSectorIndex(){
        int x = currentXSector();
        int y = currentYSector();
        int z = currentZSector();

        return x*numberOfSectors*numberOfSectors + y*numberOfSectors + z;
    }

    private int currentXSector(){
        return (int)p1.eye.x / sectorSize;
    }

    private int currentYSector(){
        return (int)p1.eye.y / sectorSize;
    }

    private int currentZSector(){
        return (int)p1.eye.z / sectorSize;
    }

    private void shoot(){
        Vector3D forward = new Vector3D(-p1.n.x, -p1.n.y, -p1.n.z);
        forward.normalize();

        Point3D location = new Point3D(p1.eye.x + forward.x*4, p1.eye.y + forward.y*4, p1.eye.z + forward.z*4);

        Vector3D speed = Vector3D.sum(p1.speed, forward);

        projectiles.add(new Projectile(location,speed,projectileTexture));
    }

    @Override
    /**
     * called when the application is closed
     */
    public void dispose() {
        projectileTexture.dispose();
        starTexture.dispose();
        shuttle.dispose();
        //shuttleTexture.dispose();
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
            p1.yaw(-120.0f * deltaTime);

        //turn to the right
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            p1.yaw(120.0f * deltaTime);

        //slide forward
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            p1.slide(0.0f, 0.0f, -10.0f * deltaTime);
        }

        //slide backward
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            p1.slide(0.0f, 0.0f, 10.0f * deltaTime);
        }

        //slide up
        if(Gdx.input.isKeyPressed(Input.Keys.R))
            p1.slide(0.0f, 10.0f * deltaTime, 0.0f);

        //slide down
        if(Gdx.input.isKeyPressed(Input.Keys.F))
           p1.slide(0.0f, -10.0f * deltaTime, 0.0f);

        if(Gdx.input.isKeyPressed(Input.Keys.UP))
            p1.pitch(-90.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            p1.pitch(90.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            p1.roll(-90.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            p1.roll(90.0f * deltaTime);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            p1.speed = new Vector3D(0,0,0);
        }

        p1.roll(-Gdx.input.getDeltaX()*deltaTime * sensitivity);
        p1.pitch(Gdx.input.getDeltaY()*deltaTime * sensitivity);

        updatePlayer();

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
            p1.eye.x = p1.eye.y = p1.eye.z = 0;
        }

        hit();
    }

    private void updatePlayer(){
        p1.update();
        if (currentSector() != p1.currentSector){
            p1.currentSector = currentSector();
            generateSector(p1.currentSector);
        }
    }

    private void hit(){
        for (Projectile p : projectiles){

        }
    }

    private void drawEnvironment() {
        drawSectors();
        drawProjectiles();
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

    private void drawSector(int x, int y, int z){
        if (x > 0 && y > 0 && z > 0){
            if (x < numberOfSectors-1 && y < numberOfSectors-1 && z < numberOfSectors-1){
                if (sectors[x][y][z] != null){
                    sectors[x][y][z].draw();
                }
            }
        }
    }

    private void drawProjectiles(){
        for (Projectile proj : projectiles){
            proj.updatePosition();
            proj.draw();
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
        //Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.3333f, 0.5f, sectorSize);

        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);

        // Configure light 0
        float[] lightDiffuse0 = {1f, 1f, 1f, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightDiffuse0, 0);

        float[] lightPosition0 = {p1.eye.x , p1.eye.y, p1.eye.z, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition0, 0);

        // Set material on the cubes.
        float[] cubeMaterialDiffuse;
        cubeMaterialDiffuse = new float[]{1f, 1f, 1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT, cubeMaterialDiffuse, 0);

        drawEnvironment();

        drawShuttles();

        //Write the text on the screen:

        //lighting making a mess of the letters
        Gdx.gl11.glDisable(GL11.GL_LIGHTING);

        this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
        secondCamera.update();

        Vector3D v = Vector3D.sum(p1.n,new Vector3D(0,0,0)); //reverse n
        v.normalize();

        this.spriteBatch.begin();
        font.setColor(1f,1f,1f,1f);
        font.setScale(1,1);
        font.draw(this.spriteBatch, String.format("o"),-3,4);
        font.draw(this.spriteBatch, String.format("Player position: (%.2f, %.2f, %.2f)",this.p1.eye.x, this.p1.eye.y, this.p1.eye.z), -400, -280);
        font.draw(this.spriteBatch, String.format("Current sector coordinates: (%d,%d,%d)", (int)p1.eye.x / sectorSize, (int)p1.eye.y / sectorSize, (int)p1.eye.z / sectorSize), -400, -300);
        font.draw(this.spriteBatch, String.format("Current sector: %d", currentSectorIndex()), -400, -320);
        font.draw(this.spriteBatch, String.format("Frames per second: %d", Gdx.graphics.getFramesPerSecond()), -400, -340);
        font.draw(this.spriteBatch, String.format("p1.n: (%f,%f,%f)", v.x,v.y,v.z), -400, -360);
        this.spriteBatch.end();


        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
    }

    private void drawShuttles(){
        //unit vectors
        Vector3D i = new Vector3D(1,0,0);
        Vector3D j = new Vector3D(0,0,1);
        Vector3D k = new Vector3D(0,1,0);
        //reference point
        Point3D pos = new Point3D(p1.eye.x, p1.eye.y, p1.eye.z);


        Gdx.gl10.glPushMatrix();
        //Gdx.gl10.glTranslatef(p1.eye.x, p1.eye.y - 1, p1.eye.z);
        Gdx.gl10.glTranslatef(50000, 50000,50000);
        Vector3D v = Vector3D.sum(p1.n, Vector3D.sum(p1.u, p1.v)); //reverse n
        v.normalize();

        //Gdx.gl10.glRotatef(180,0,0,0);
        Gdx.graphics.getGL10().glEnable(GL10.GL_TEXTURE_2D);
	    modelBatch.begin(cam);
        shuttleTexture.bind();
        modelBatch.render(instances);
	    modelBatch.end();
        Gdx.gl10.glPopMatrix();

    }

    @Override
    /**
     * The "loop", this is called indefinitely as the program runs.
     */
    public void render() {
        update();
        display();
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
        if (playing){
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