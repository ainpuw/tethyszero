package com.ainpuw.tethyszero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private GameConfig config;

    private OrthographicCamera camera = new OrthographicCamera();

    private TiledMap map = new TmxMapLoader().load("map.tmx");;
    private TiledMapTileLayer playground = (TiledMapTileLayer) map.getLayers().get("playground");
    private OrthogonalTiledMapRenderer renderer;

    private Array<Block> blocks = new Array<>();;

    private boolean globalZeroSpeed = true;
    private boolean exploding = false;
    private boolean [] inputActions = new boolean[]{false, false, false, false, false, false};
    private Speed speedAction = Speed.ZERO;
    private int rotationAction = 0;
    private boolean playerRotation = false;
    private float cumulativeTime;

    public GameScreen(final Main game) {
        config = game.config;

        camera.setToOrtho(config.ydown, config.w, config.h);
        camera.position.x = config.camStartPosX;
        camera.position.y = config.camStartPosY;

        renderer = new OrthogonalTiledMapRenderer(map, 1 / (float) config.tileLen);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Take player input.
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            inputActions[0] = true;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            inputActions[1] = true;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            inputActions[2] = true;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            inputActions[3] = true;
        if (Gdx.input.isKeyPressed(Input.Keys.Z))
            inputActions[4] = true;
        if (Gdx.input.isKeyPressed(Input.Keys.X))
            inputActions[5] = true;

        // Perform a step every dt amount of real time.
        // When an explosion is happening, it steps faster.
        cumulativeTime += Gdx.graphics.getDeltaTime();
        if ((exploding && cumulativeTime >= config.explosionDT)
                || (!exploding && cumulativeTime >= config.playerDT)) {
            speedAction = resolveSpeedAction();
            rotationAction = resolveRotationAction();
            cumulativeTime = 0;
            step();
        }


        ScreenUtils.clear(config.screenBgR, config.screenBgG, config.screenBgB, config.screenBgA);
        camera.update();

        renderer.setView(camera);
        renderer.render();

        Batch batch = renderer.getBatch();

        batch.begin();
        batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    private void step() {
        // Check if there is any moving blocks.
        globalZeroSpeed = true;
        playerRotation = false;
        for (Block b : blocks) {
            if (b.speed != Speed.ZERO) {
                globalZeroSpeed = false;
            }
        }

        if (globalZeroSpeed) {
            // First, check if we can cancel anything.
            // If a region is canceled, initialize explosion phase.
            if (cancelMaxConnectedRegion()) {
                exploding = true;
            }
            // If nothing can be canceled, generate a new block.
            else {
                exploding = false;
                Block newBlock = new Block(config.spawns.get(Utils.rand(0, config.nSpawns - 1)),
                        Utils.randSpeed(), Utils.randPower(), Utils.randTile());
                for (int i = 0; i < blocks.size - 1; i++) {
                    if (newBlock.overlap(blocks.get(i)))
                        System.out.println("GAME OVER");
                }
                blocks.add(newBlock);
            }
        }
        else {
            // If in the player phase (not in the canceling/explosion), input player action.
            if (!exploding) {
                Block playerBlock = blocks.get(blocks.size - 1);
                // Player can move change the moving direction or orientation of the blocks up to a fixed number of times.
                if (playerBlock.remainingActions > 0) {
                    // One action per turn and prioritize speed action.
                    if (speedAction != Speed.ZERO && speedAction != playerBlock.speed) {
                        // The player controlled block is always the last block.
                        playerBlock.speed = speedAction;
                        playerBlock.remainingActions--;
                    }
                    else if (rotationAction != 0 && !playerBlock.centerSymmetric) {
                        playerBlock.rotation = rotationAction;
                        playerRotation = true;
                        playerBlock.remainingActions--;
                    }
                }
            }
            moveBlocks();
        }

        // Clear the playground.
        for (int i = 0; i < playground.getWidth(); i++) {
            for (int j = 0; j < playground.getHeight(); j++) {
                playground.getCell(i, j).setTile(map.getTileSets().getTile(2));
            }
        }

        // Put tiles onto the playground.
        for (int i = 0; i < blocks.size; i++) {
            Block b = blocks.get(i);
            for (int j = 0; j < b.n; j++) {
                TiledMapTileLayer.Cell cell = playground.getCell(b.shape[j * 2], b.shape[j * 2 + 1]);
                cell.setTile(map.getTileSets().getTile(b.tileId));
            }
        }
    }

    // This function is a very inefficient brute force algorithm.
    private void moveBlocks() {
        int movedCounter = 0;

        while (movedCounter < blocks.size) {
            for (int i = 0; i < blocks.size; i++) {
                Block b1 = blocks.get(i);

                if (b1.moved)
                    continue;

                if (b1.speed == Speed.ZERO) {
                    b1.moved = true;
                    movedCounter++;
                    continue;
                }

                // Collision detection.
                b1.move();
                b1.rotate();
                int hit = 0;  // 0 - hit nothing. 1 - hit wall. 2 - hit block.
                boolean hitBlockZeroSpeed = false;
                if (b1.overlap(config.wallMinX, config.wallMaxX, config.wallMinY, config.wallMaxY))
                    hit = 1;
                else {
                    for (int j = 0; j < blocks.size; j++) {
                        if (i == j)
                            continue;

                        Block b2 = blocks.get(j);

                        if (b1.overlap(b2)) {
                            hit = 2;
                            if (b2.speed == Speed.ZERO) {
                                hitBlockZeroSpeed = true;
                                break;
                            }
                        }
                    }
                }

                // Resolve collisions.
                if (hit == 1) {
                    b1.rotateBack();
                    b1.moveBack();
                    if (!playerRotation) {  // Rotation leads to an extra action.
                        b1.speed = Speed.ZERO;
                        b1.moved = true;
                        movedCounter++;
                    }
                    else
                        playerRotation = false;
                } else if (hit == 2) {
                    if (hitBlockZeroSpeed) {
                        // Hitting a stopped block is the same as hitting the wall.
                        b1.rotateBack();
                        b1.moveBack();
                        if (!playerRotation) {  // Rotation leads to an extra action.
                            b1.speed = Speed.ZERO;
                            b1.moved = true;
                            movedCounter++;
                        }
                        else
                            playerRotation = false;
                    } else {
                        // If hitting a moving block, that means that block needs to move first.
                        // Step back and wait for it to happen.
                        b1.rotateBack();
                        b1.moveBack();
                    }
                } else {  // hit == 0
                    b1.moved = true;
                    movedCounter++;
                }
            }
        }

        // Set everything to be movable again.
        for (int i = 0; i < blocks.size; i++)
            blocks.get(i).moved = false;
    }

    private boolean cancelMaxConnectedRegion() {
        // Initialize map.
        int[][] m = new int[(int) config.w][(int) config.h];
        for (int i = 0; i < (int) config.w; i++)
            for (int j = 0; j < (int) config.h; j++)
                m[i][j] = 0;
        for (Block b : blocks)
            for (int i = 0; i < b.n; i++)
                m[b.shape[2 * i]][b.shape[2 * i + 1]] = 1;

        // Max region detection.
        int [] result = Utils.maxRectangle(m);
        // System.out.printf("%d - | %d  %d | o | %d  %d |\n", result[0], result[1], result[2], result[3], result[4]);
        if (result[0] < config.cancelThreshold)
            return false;

        // Delete canceled blocks.
        Array<Block> newBlocks = new Array<>();
        for (Block b : blocks) {
            Array<Block> smallBlocks = breakDownBlock(b, result);
            for (Block sb : smallBlocks)
                newBlocks.add(sb);
        }
        blocks.clear();  // TODO: What does this do?
        blocks = newBlocks;

        // Reassign speed to all blocks.
        int minx = result[1];
        int miny = result[2];
        int maxx = result[3];
        int maxy = result[4];
        int cx = (maxx - minx) / 2 + minx;
        int cy = (maxy - miny) / 2 + miny;
        for (Block b : blocks) {
            int[] center = b.getCenter();
            int bx = center[0];
            int by = center[1];
            // Very coarse grained.
            Vector2 direction = new Vector2(bx - cx, by - cy);
            float deg = direction.angleDeg();
            if (deg >= 337.5 || deg < 22.5)
                b.speed = Speed.RIGHT;
            else if (deg >= 22.5 && deg < 67.5)
                b.speed = Speed.UPRIGHT;
            else if (deg >= 67.5 && deg < 112.5)
                b.speed = Speed.UP;
            else if (deg >= 112.5 && deg < 157.5)
                b.speed = Speed.UPLEFT;
            else if (deg >= 157.5 && deg < 202.5)
                b.speed = Speed.LEFT;
            else if (deg >= 202.5 && deg < 247.5)
                b.speed = Speed.DOWNLEFT;
            else if (deg >= 247.5 && deg < 292.5)
                b.speed = Speed.DOWN;
            else  // if (deg >= 292.5 && deg < 337.5)
                b.speed = Speed.DOWNRIGHT;

        }

        return true;
    }

    private Array<Block> breakDownBlock(Block b, int [] bounds) {
        Array<Block> smallBlocks = new Array<>();
        int minx = bounds[1];
        int miny = bounds[2];
        int maxx = bounds[3];
        int maxy = bounds[4];

        // Let's be inefficient.
        int survivingN = 0;
        int [] survivingUnits = new int[8];  // There are at most 4 units.
        for (int i = 0 ; i < b.n; i++) {
            int x = b.shape[i * 2];
            int y = b.shape[i * 2 + 1];
            if (x < minx || x > maxx || y < miny || y > maxy) {
                survivingUnits[2 * survivingN] = x;
                survivingUnits[2 * survivingN + 1] = y;
                survivingN++;
            }
        }
        if (survivingN == b.n) {
            smallBlocks.add(b);
            return smallBlocks;
        }

        Array<Array<Integer>> islands = new Array<>();
        for (int i = 0 ; i < survivingN; i++) {
            if (islands.size == 0) {
                islands.add(new Array<>());
                islands.get(0).add(survivingUnits[2 * i]);
                islands.get(0).add(survivingUnits[2 * i + 1]);
            } else {
                Array<Integer> last = islands.get(islands.size - 1);
                boolean added = false;
                for (int j = 0 ; j < last.size / 2; j++) {
                    // If neighbors.
                    if (Math.abs(last.get(2 * j) - survivingUnits[2 * i]) + Math.abs(last.get(2 * j + 1) - survivingUnits[2 * i + 1]) == 1) {
                        last.add(survivingUnits[2 * i]);
                        last.add(survivingUnits[2 * i + 1]);
                    }
                    added = true;
                    break;
                }
                // Start its own island.
                if (!added) {
                    islands.add(new Array<>());
                    islands.get(islands.size - 1).add(survivingUnits[2 * i]);
                    islands.get(islands.size - 1).add(survivingUnits[2 * i] + 1);
                }
            }
        }

        // TODO: Am I creating too many objects here?
        for (Array<Integer> island : islands) {
            int [] coords = new int[island.size];
            for (int i = 0 ; i < island.size; i++)
                coords[i] = island.get(i);
            Block islandBlock = new Block(coords, Speed.ZERO, b.power, b.tileId);
            smallBlocks.add(islandBlock);
        }

        return smallBlocks;
    }

    private Speed resolveSpeedAction() {
        // UP, DOWN, LEFT, RIGHT, ROTLEFT, ROTRIGHT.

        Speed inputDirection = Speed.ZERO;

        if (inputActions[0] && inputActions[1])
            ;
        else if (inputActions[2] && inputActions[3])
            ;
        else if (inputActions[0] && inputActions[3])
            ;
        else if (inputActions[0] && inputActions[2])
            inputDirection = Speed.UPLEFT;
        else if (inputActions[0] && inputActions[3])
            inputDirection = Speed.UPRIGHT;
        else if (inputActions[0])
            inputDirection = Speed.UP;
        else if (inputActions[1] && inputActions[2])
            inputDirection = Speed.DOWNLEFT;
        else if (inputActions[1] && inputActions[3])
            inputDirection = Speed.DOWNRIGHT;
        else if (inputActions[1])
            inputDirection = Speed.DOWN;
        else if (inputActions[2])
            inputDirection = Speed.LEFT;
        else if (inputActions[3])
            inputDirection = Speed.RIGHT;

        // Reset.
        inputActions[0] = false;
        inputActions[1] = false;
        inputActions[2] = false;
        inputActions[3] = false;

        return inputDirection;
    }

    private int resolveRotationAction() {
        // UP, DOWN, LEFT, RIGHT, ROTLEFT, ROTRIGHT.

        int rotDirection = 0;
        // Rotate left (counterclockwise).
        if (inputActions[4] && !inputActions[5])
            rotDirection = -1;
            // Rotate right (clockwise).
        else if (!inputActions[4] && inputActions[5])
            rotDirection = 1;

        // Reset.
        inputActions[4] = false;
        inputActions[5] = false;

        return rotDirection;
    }
}
