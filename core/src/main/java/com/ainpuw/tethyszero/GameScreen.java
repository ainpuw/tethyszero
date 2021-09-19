package com.ainpuw.tethyszero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
        // Perform a step every dt amount of real time.
        // When an explosion is happening, it steps faster.
        cumulativeTime += Gdx.graphics.getDeltaTime();
        if ((exploding && cumulativeTime >= config.explosionDT)
                || cumulativeTime >= config.playerDT) {
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
        for (Block b : blocks) {
            if (b.speed != Speed.ZERO) {
                globalZeroSpeed = false;
            }
        }

        // Move blocks or spawn a block randomly.
        if (globalZeroSpeed) {
            exploding = false;
            Block newBlock = new Block(config.spawns.get(Utils.rand(0, config.nSpawns - 1)),
                    Utils.randSpeed(), Utils.randPower(), Utils.randTile());
            for (int i = 0; i < blocks.size - 1; i++) {
                if (newBlock.overlap(blocks.get(i)))
                    System.out.println("GAME OVER");
            }
            blocks.add(newBlock);
        } else {
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
                b1.step();
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
                    b1.stepBack();
                    b1.speed = Speed.ZERO;
                    b1.moved = true;
                    movedCounter++;
                } else if (hit == 2) {
                    if (hitBlockZeroSpeed) {
                        // Hitting a stopped block is the same as hitting the wall.
                        b1.stepBack();
                        b1.speed = Speed.ZERO;
                        b1.moved = true;
                        movedCounter++;
                    } else {
                        // If hitting a moving block, that means that block needs to move first.
                        // Step back and wait for it to happen.
                        b1.stepBack();
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
}
