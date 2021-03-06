package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.GameBoard;

public abstract class TileEntity {
    private TileDefinition tileType;
    private Vector2 pos;
    protected TiledMapTileLayer.Cell cell;
    private GameBoard board;

    public TileEntity(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell, GameBoard board) {
        this.tileType = TileDefinition.getTileById(tile.getId());
        this.pos = new Vector2(x, y);
        this.cell = cell;
        this.board = board;
    }


    /**
     * Is a coordinate colliding with this TileEntity
     *
     * @param coords
     * @return true if colliding
     */
    public boolean detectCollision(Vector2 coords) {
        return coords.epsilonEquals(pos);
    }

    /**
     * Get the tile type
     *
     * @return TileDefinition
     */
    public TileDefinition getTileType() {
        return tileType;
    }


    /**
     * Do an action to a player, for example harm it when walking into lasers
     *
     * @param player
     */
    public abstract void walkOn(Player player);

    /**
     * Actions to be ran every tick
     */
    public abstract void update();

    /**
     * If a player move on top of the tileEntity, should it continue walking
     *
     * @return if it can continue walking
     */
    public abstract boolean canContinueWalking();


    public abstract boolean canEnter(Direction walkingDirection);

    public abstract boolean canLeave(Direction walkingDirection);

    public Direction getDirection() {
        Direction tileDefault = tileType.getDefaultFace();
        int rotation = 4 + tileDefault.ordinal();
        switch (tileDefault) {
            case NORTH:
            case SOUTH:
                if (cell.getFlipHorizontally()) {
                    rotation +=2;
                }
                break;
            case WEST:
            case EAST:
                if (cell.getFlipVertically()) {
                    rotation +=2;
                }
                break;
        }
        rotation -= cell.getRotation();
        return Direction.values()[rotation % 4];
    }

    public Vector2 getTileInDirection(Direction direction, int steps) {
        Vector2 tile = new Vector2(pos.x, pos.y);
        switch (direction) {
            case NORTH:
                tile.add(0, steps);
                break;
            case SOUTH:
                tile.add(0, -steps);
                break;
            case WEST:
                tile.add(-steps, 0);
                break;
            case EAST:
                tile.add(steps, 0);
                break;
        }
        return tile;
    }


    public GameBoard getBoard() {
        return board;
    }

    public Vector2 getPos() {
        return pos;
    }
}
