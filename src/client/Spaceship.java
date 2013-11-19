import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.Model;

public class Spaceship {
    Model model;
    Texture tex;

    public Spaceship(Model model, Texture tex) {
        this.model = model;
        this.tex = tex;
    }
}
