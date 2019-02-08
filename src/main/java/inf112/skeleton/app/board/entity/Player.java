package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.board.GameBoard;

public class Player extends Entity {


    Texture image;


    public Player(float x, float y, GameBoard board) {
        super(x, y, EntityType.PLAYER, board);
        image = new Texture("robot.png");
    }

    @Override
    public void update() {
        // Keyhandler? ???

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(image, pos.x*64, pos.y*64, getWidth(), getHeight());
        System.out.println("drawn");
    }
}
