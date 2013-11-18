/**
 * Sphere class from Hlynur,
 * https://github.com/hlysig/tgra-2013/blob/master/examples/SphereAndLightsInGameMode/src/com/tgra/Sphere.java
 */

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.utils.BufferUtils;

public class Sphere
{
    private int stacks;
    private int slices;
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private int vertexCount;
    private boolean drawLines = false;

    public Sphere(){
        //default constructor
    }

    public Sphere(int i_stacks, int i_slices) {
        stacks = i_stacks;
        slices = i_slices;
        vertexCount = 0;
        float[] array = new float[(stacks)*(slices+1)*6];
        float stackInterval = (float)Math.PI / (float)stacks;
        float sliceInterval = 2.0f*(float)Math.PI / (float)slices;
        float stackAngle, sliceAngle;
        for(int stackCount = 0; stackCount < stacks; stackCount++) {
            stackAngle = stackCount * stackInterval;
            for(int sliceCount = 0; sliceCount < slices+1; sliceCount++) {
                sliceAngle = sliceCount * sliceInterval;
                array[vertexCount*3] =          (float)Math.sin(stackAngle) * (float)Math.cos(sliceAngle);
                array[vertexCount*3 + 1] = (float)Math.cos(stackAngle);
                array[vertexCount*3 + 2] = (float)Math.sin(stackAngle) * (float)Math.sin(sliceAngle);

                array[vertexCount*3 + 3] = (float)Math.sin(stackAngle + stackInterval) * (float)Math.cos(sliceAngle);
                array[vertexCount*3 + 4] = (float)Math.cos(stackAngle + stackInterval);
                array[vertexCount*3 + 5] = (float)Math.sin(stackAngle + stackInterval) * (float)Math.sin(sliceAngle);

                vertexCount += 2;
            }
        }
        vertexBuffer = BufferUtils.newFloatBuffer(vertexCount*3);
        vertexBuffer.put(array);
        vertexBuffer.rewind();
        normalBuffer = BufferUtils.newFloatBuffer(vertexCount*3);
        normalBuffer.put(array);
        normalBuffer.rewind();
    }

    public void draw() {
        //Gdx.gl11.glShadeModel(GL11.GL_SMOOTH);
        Gdx.gl11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, vertexBuffer);
        Gdx.gl11.glNormalPointer(GL11.GL_FLOAT, 0, normalBuffer);
        for(int i = 0; i < vertexCount; i += (slices+1)*2) {
            if(this.drawLines)
                Gdx.gl11.glDrawArrays(GL11.GL_LINE_LOOP, i, (slices+1)*2);
            else
                Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, i, (slices+1)*2);
        }
    }

    public void toggleDrawLines(){
        this.drawLines = this.drawLines ? false : true;
    }
}