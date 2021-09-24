package com.ainpuw.tethyszero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private GameConfig config;
    private Main game;

    private OrthographicCamera camera = new OrthographicCamera();

    private TiledMap map = new TmxMapLoader().load("map.tmx");;
    private TiledMapTileLayer playground = (TiledMapTileLayer) map.getLayers().get("playground");
    private OrthogonalTiledMapRenderer renderer;
    Texture destroyTexture = new Texture("destroy_tiles.png");;
    private Array<Animation<TextureRegion>> destroyAnimations = new Array<>();
    Texture digitsTexture = new Texture("digits.png");;
    TextureRegion[] digits;

    Sound explodeSound = Gdx.audio.newSound(Gdx.files.internal("retro_explosion_05.ogg"));
    Sound gameoverSound = Gdx.audio.newSound(Gdx.files.internal("synth_misc_14.ogg"));

    private Array<Block> blocks = new Array<>();

    private boolean globalZeroSpeed = true;
    private boolean exploding = false;
    private boolean [] inputActions = new boolean[]{false, false, false, false, false, false};
    private Speed speedAction = Speed.ZERO;
    private int rotationAction = 0;
    private boolean playerRotation = false;
    private float cumulativeTime;
    private float totalTime = 0;
    private float destroyTime = 0;
    private float wait;

    private int totalTilesDestroyed = 0;
    private int [] currentDestroyedCounters = new int[]{0, 0, 0, 0, 0};
    private boolean hasMajority = false;
    private Power majorityPower = Power.T;
    private int[] destroyRegion = new int[]{};
    private boolean usedSpecialLastTime = false;
    private boolean isGameOver = false;

    public GameScreen(Main game) {
        config = game.config;
        this.game = game;

        camera.setToOrtho(config.ydown, config.w, config.h);
        camera.position.x = config.camStartPosX;
        camera.position.y = config.camStartPosY;

        renderer = new OrthogonalTiledMapRenderer(map, 1 / (float) config.tileLen);

        TextureRegion[][] destroySprites = TextureRegion.split(destroyTexture, 32, 32);
        for (int i = 0; i < 6; i++)  // 6 different colors.
            destroyAnimations.add(new Animation(config.destroyAnimationSpeed,
                    destroySprites[i][0], destroySprites[i][1], destroySprites[i][2],
                    destroySprites[i][3], destroySprites[i][4]));

        digits = TextureRegion.split(digitsTexture, 18, 32)[0];

        wait = config.startingWait;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Take player input.
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            inputActions[0] = true;
            config.playerDT *= config.playerDTScaling;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            inputActions[1] = true;
            config.playerDT *= config.playerDTScaling;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            inputActions[2] = true;
            config.playerDT *= config.playerDTScaling;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            inputActions[3] = true;
            config.playerDT *= config.playerDTScaling;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            inputActions[4] = true;
            config.playerDT *= config.playerDTScaling;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            inputActions[5] = true;
            config.playerDT *= config.playerDTScaling;
        }

        // Perform a step every dt amount of real time.
        // When an explosion is happening, it steps faster.
        cumulativeTime += Gdx.graphics.getDeltaTime();
        wait -= Gdx.graphics.getDeltaTime();
        wait = Math.max(-1, wait);
        totalTime += Gdx.graphics.getDeltaTime();

        // Things go faster every N seconds.
        if (totalTime > this.config.increaseSpeedEvery) {
            totalTime -= this.config.increaseSpeedEvery;
            this.config.playerDTDefault *= this.config.playerDTDefaultScaling;
        }

        if ((exploding && cumulativeTime >= config.explosionDT)
                || (!exploding && cumulativeTime >= config.playerDT)) {
            speedAction = resolveSpeedAction();
            rotationAction = resolveRotationAction();
            cumulativeTime = 0;

            if (wait > 0)
                ;
            else {
                if (isGameOver) {
                    while (blocks.size > 0)
                        blocks.pop();
                    game.setScreen(new StartScreen(game, false));
                    dispose();
                }
                step();
            }
        }


        ScreenUtils.clear(config.gameScreenBgR, config.gameScreenBgG, config.gameScreenBgB, config.screenBgA);
        camera.update();

        renderer.setView(camera);
        renderer.render();

        Batch batch = renderer.getBatch();
        batch.begin();

        // Paint destruction if any.
        destroyTime += Gdx.graphics.getDeltaTime();
        for (Block b : blocks) {
            if (b.toDestroy) {
                TextureRegion frame;
                if (!hasMajority || b.power != majorityPower)
                    frame = destroyAnimations.get(0).getKeyFrame(destroyTime);
                else {
                    if (b.power == Power.T)
                        frame = destroyAnimations.get(1).getKeyFrame(destroyTime);
                    else if (b.power == Power.Z)
                        frame = destroyAnimations.get(2).getKeyFrame(destroyTime);
                    else if (b.power == Power.E)
                        frame = destroyAnimations.get(3).getKeyFrame(destroyTime);
                    else if (b.power == Power.R)
                        frame = destroyAnimations.get(4).getKeyFrame(destroyTime);
                    else
                        frame = destroyAnimations.get(5).getKeyFrame(destroyTime);
                }
                batch.draw(frame, b.shape[0]-0.25f, b.shape[1]-0.25f, 1.5f, 1.5f);
            }
        }

        // Draw the scores.
        game.bestScore = Math.max(game.bestScore, totalTilesDestroyed);
        drawScore(batch, totalTilesDestroyed, 17f);
        drawScore(batch, game.bestScore, 26f);

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
        // renderer.dispose();  // FIXME: Put game over somewhere else.
        destroyTexture.dispose();
        digitsTexture.dispose();
        explodeSound.dispose();
        gameoverSound.dispose();
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
            // Handle destroyed things if any.
            Array<Block> newBlocks = new Array<>();
            for (Block b : blocks)
                if (!b.toDestroy)
                    newBlocks.add(b);
            blocks.clear();
            blocks = newBlocks;

            // First, check if we can cancel anything.
            // If a region is canceled, initialize explosion phase.
            if (cancelMaxConnectedRegion()) {
                // FIXME: There is a bug with detecting the majority!!!
                int currentTotalDestroyed = 0;
                for (int i = 0; i < currentDestroyedCounters.length; i++) {
                    currentTotalDestroyed += currentDestroyedCounters[i];
                }

                for (int i = 0; i < currentDestroyedCounters.length; i++) {
                    if (currentDestroyedCounters[i] >= currentTotalDestroyed/2 + 1) {
                        if (i == 0)  majorityPower = Power.T;
                        else if (i == 1)  majorityPower = Power.Z;
                        else if (i == 2)  majorityPower = Power.E;
                        else if (i == 3)  majorityPower = Power.R;
                        else if (i == 4)  majorityPower = Power.O;
                        hasMajority = true;
                    }
                }
                exploding = true;
                destroyTime = 0;
                wait = config.waitAfterExplosion;  // Show the explosion.
            }
            // If nothing can be canceled, generate a new block.
            else {
                for (int i = 0; i < currentDestroyedCounters.length; i++)
                    currentDestroyedCounters[i] = 0;
                exploding = false;
                destroyRegion = new int[]{};

                Block newBlock;
                if (hasMajority && !usedSpecialLastTime) {  // Spawn a special power block.
                    newBlock = new Block(new int[]{15, 16, 16, 16, 16, 15, 15, 15},
                            Utils.randSpeed(), majorityPower);
                    newBlock.isSpecial = true;
                    newBlock.calculateSpecialTileId();
                    usedSpecialLastTime = true;
                } else {  // Spawn a normal block.
                    newBlock = new Block(config.spawns.get(Utils.rand(0, config.nSpawns - 1)),
                            Utils.randSpeed(), Utils.randPower());
                    usedSpecialLastTime = false;
                }
                for (int i = 0; i < blocks.size; i++) {
                    if (newBlock.overlap(blocks.get(i))) {
                        System.out.println("GAME OVER");
                        isGameOver = true;
                        game.bestScore = Math.max(game.bestScore, totalTilesDestroyed);
                        TiledMapTileLayer gameoverLayer = (TiledMapTileLayer) map.getLayers().get("gameover");
                        gameoverLayer.setVisible(true);
                        wait = config.gameoverWait;
                        gameoverSound.play();
                        game.startMusic.stop();
                        game.restartCounter += 1;
                    }
                }
                blocks.add(newBlock);
                config.playerDT = config.playerDTDefault;
                hasMajority = false;
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
                playground.getCell(i, j).setTile(map.getTileSets().getTile(config.clearTileId));
            }
        }

        // Destroy region tiles into the playground.
        if (destroyRegion.length == 5) {
            for (int i = destroyRegion[1]; i <= destroyRegion[3]; i++) {
                for (int j = destroyRegion[2]; j <= destroyRegion[4]; j++) {
                    playground.getCell(i, j).setTile(map.getTileSets().getTile(config.destroyTileId));
                }
            }
        }

        // Put normal block tiles onto the playground.
        for (int i = 0; i < blocks.size; i++) {
            Block b = blocks.get(i);
            for (int j = 0; j < b.n; j++) {
                TiledMapTileLayer.Cell cell = playground.getCell(b.shape[j * 2], b.shape[j * 2 + 1]);
                cell.setTile(map.getTileSets().getTile(b.tileId[j]));
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

        // Perform cancel region detection.
        boolean hasSpecial = false;
        // This is a dummy placeholder.
        Block specialBlock = new Block(new int[]{16, 16},  Speed.ZERO, Power.T);
        for (Block b : blocks)
            if (b.isSpecial) {
                hasSpecial = true;
                specialBlock = b;
                break;
            }
        if (hasSpecial) {
            switch (specialBlock.power) {
                case T:
                    // Choose the second more popular block. FIXME: Bad if else.
                    int [] powerCounter = new int[]{0, 0, 0, 0};
                    for (Block b : blocks) {
                        if (b.power == Power.Z)
                            powerCounter[0] += b.n;
                        else if (b.power == Power.E)
                            powerCounter[1] += b.n;
                        else if (b.power == Power.R)
                            powerCounter[2] += b.n;
                        else if (b.power == Power.O)
                            powerCounter[3] += b.n;
                    }
                    int maxIdx = 0;
                    for (int i = 0; i < 4; i++)
                        if (powerCounter[i] > powerCounter[maxIdx])
                            maxIdx = i;
                    Power newP = Power.Z;
                    if (maxIdx == 1)
                        newP = Power.E;
                    else if (maxIdx == 2)
                        newP = Power.R;
                    else if (maxIdx == 3)
                        newP = Power.O;

                    // Change T special into the new power.
                    specialBlock.isSpecial = false;
                    specialBlock.power = newP;
                    specialBlock.calculateTileId();

                    // Change all other T block into the new power.
                    for (Block b : blocks) {
                        if (b.power == Power.T) {
                            b.power = newP;
                            b.calculateTileId();
                        }
                    }

                    // Doesn't cancel anything this time.
                    return false;
                case Z:
                    destroyRegion = new int[]{0,
                            Math.max(config.wallMinX + 1, specialBlock.shape[6] - 3),
                            Math.max(config.wallMinY + 1, specialBlock.shape[7] - 3),
                            Math.min(config.wallMaxX - 1, specialBlock.shape[2] + 3),
                            Math.min(config.wallMaxY - 1, specialBlock.shape[3] + 3)};
                    break;
                case E:
                    destroyRegion = new int[]{0,
                            Math.max(config.wallMinX + 1, specialBlock.shape[6] - 3),
                            Math.max(config.wallMinY + 1, specialBlock.shape[7] - 3),
                            Math.min(config.wallMaxX - 1, specialBlock.shape[2] + 3),
                            Math.min(config.wallMaxY - 1, specialBlock.shape[3] + 3)};
                    break;
                case R:
                    destroyRegion = new int[]{0,
                            Math.max(config.wallMinX + 1, specialBlock.shape[6] - 3),
                            Math.max(config.wallMinY + 1, specialBlock.shape[7] - 3),
                            Math.min(config.wallMaxX - 1, specialBlock.shape[2] + 3),
                            Math.min(config.wallMaxY - 1, specialBlock.shape[3] + 3)};
                    break;
                case O:
                    destroyRegion = new int[]{0,
                            Math.max(config.wallMinX + 1, specialBlock.shape[6] - 3),
                            Math.max(config.wallMinY + 1, specialBlock.shape[7] - 3),
                            Math.min(config.wallMaxX - 1, specialBlock.shape[2] + 3),
                            Math.min(config.wallMaxY - 1, specialBlock.shape[3] + 3)};
                    break;
            }

            explodeSound.play();
        }
        // Max region detection.
        else {
            destroyRegion = Utils.maxRectangle(m);
            if (destroyRegion[0] < config.cancelThreshold)
                return false;
            explodeSound.play();
        }

        // Handling canceled blocks.
        Array<Block> newBlocks = new Array<>();
        for (Block b : blocks) {
            Array<Block> smallBlocks = breakDownBlock(b, destroyRegion);
            for (Block sb : smallBlocks)
                newBlocks.add(sb);
        }
        blocks.clear();  // TODO: What does this do?
        blocks = newBlocks;

        // Reassign speed to all blocks.
        int minx = destroyRegion[1];
        int miny = destroyRegion[2];
        int maxx = destroyRegion[3];
        int maxy = destroyRegion[4];
        for (Block b : blocks) {
            int[] center = b.getCenter();
            int bx = center[0];
            int by = center[1];
            if (b.toDestroy)
                b.speed = Speed.ZERO;
            else if (bx < minx) {
                if (by > maxy)
                    b.speed = Speed.UPLEFT;
                else if (by < miny)
                    b.speed = Speed.DOWNLEFT;
                else
                    b.speed = Speed.LEFT;
            }
            else if (bx > maxx) {
                if (by > maxy)
                    b.speed = Speed.UPRIGHT;
                else if (by < miny)
                    b.speed = Speed.DOWNRIGHT;
                else
                    b.speed = Speed.RIGHT;
            }
            else if (by > maxy)
                b.speed = Speed.UP;
            else
                b.speed = Speed.DOWN;
        }

        return true;
    }

    private Array<Block> breakDownBlock(Block b, int [] bounds) {
        Array<Block> smallBlocks = new Array<>();
        if (b.isSpecial)   return smallBlocks;
        int minx = bounds[1];
        int miny = bounds[2];
        int maxx = bounds[3];
        int maxy = bounds[4];

        // Let's be inefficient.
        int survivingN = 0;
        int destroyedN = 0;
        int [] survivingUnits = new int[8];  // There are at most 4 units.
        Array<Array<Integer>> destroyedUnits = new Array<>();
        for (int i = 0 ; i < b.n; i++) {
            int x = b.shape[i * 2];
            int y = b.shape[i * 2 + 1];
            if (x < minx || x > maxx || y < miny || y > maxy) {
                survivingUnits[2 * survivingN] = x;
                survivingUnits[2 * survivingN + 1] = y;
                survivingN++;
            } else {
                destroyedUnits.add(new Array<>());
                destroyedUnits.get(destroyedN).add(x);
                destroyedUnits.get(destroyedN).add(y);;
                destroyedN++;
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
            Block islandBlock = new Block(coords, Speed.ZERO, b.power);
            smallBlocks.add(islandBlock);
        }

        totalTilesDestroyed += destroyedUnits.size;
        if (b.power == Power.T)
            currentDestroyedCounters[0] += destroyedUnits.size;
        else if (b.power == Power.Z)
            currentDestroyedCounters[1] += destroyedUnits.size;
        else if (b.power == Power.E)
            currentDestroyedCounters[2] += destroyedUnits.size;
        else if (b.power == Power.R)
            currentDestroyedCounters[3] += destroyedUnits.size;
        else if (b.power == Power.O)
            currentDestroyedCounters[4] += destroyedUnits.size;

        for (Array<Integer> destroyedUnit : destroyedUnits) {
            int [] coords = new int[destroyedUnit.size];
            for (int i = 0 ; i < destroyedUnit.size; i++)
                coords[i] = destroyedUnit.get(i);
            Block destroyedBlock = new Block(coords, Speed.ZERO, b.power);
            destroyedBlock.toDestroy = true;
            destroyedBlock.tileId[0] = config.clearTileId;  // There should be only 1 unit.
            smallBlocks.add(destroyedBlock);
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

    private void drawScore(Batch batch, int score, float xOffset) {
        // Only draw the last 5 digits.
        int s = score;
        for (int i = 0; i < 5; i++) {
            int digit = s % 10;
            s /= 10;
            batch.draw(digits[digit],
                    xOffset - i * 18.0f/config.tileLen,
                    1.85f,
                    18.0f/config.tileLen,
                    1);

        }
    }

}
