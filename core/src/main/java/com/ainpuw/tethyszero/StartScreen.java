package com.ainpuw.tethyszero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

public class StartScreen implements Screen {
    Main game;
    GameConfig config;

    private OrthographicCamera camera = new OrthographicCamera();
    private TiledMap map = new TmxMapLoader().load("startscreen.tmx");;
    private OrthogonalTiledMapRenderer renderer;

    private Texture tetrazeroTexture = new Texture("tetrazero.png");;
    private Array<Animation<TextureRegion>> tetrazeroAnimations = new Array<>();
    float [] tetrazeroStateTime;
    float [] tetrazeroX;
    float [] tetrazeroY;

    private Texture startTexture = new Texture("clicktostart.png");;
    private Texture restartTexture = new Texture("restart.png");;
    private TextureRegion gameTitleLogo;
    private TextureRegion restartTextureRegion = TextureRegion.split(restartTexture, 32, 32)[0][0];

    private Animation<TextureRegion> clicktostartAnimation;

    public StartScreen(Main game, boolean isFirstTry) {
        this.game = game;
        config = this.game.config;

        camera.setToOrtho(config.ydown, config.w, config.h);
        camera.position.x = config.camStartPosX;
        camera.position.y = config.camStartPosY;
        renderer = new OrthogonalTiledMapRenderer(map, 1 / (float) config.tileLen);

        TextureRegion[][] allTetrazeroSprites = TextureRegion.split(tetrazeroTexture, 32, 64);
        TextureRegion[] tetraRegions = allTetrazeroSprites[0];
        TextureRegion[] zRegions = allTetrazeroSprites[1];
        TextureRegion[] eRegions = allTetrazeroSprites[2];
        TextureRegion[] rRegions = allTetrazeroSprites[3];
        TextureRegion[] oRegions = allTetrazeroSprites[4];
        tetrazeroAnimations.add(new Animation(config.tetrazeroAnimationSpeed, tetraRegions[0], tetraRegions[1]));
        tetrazeroAnimations.add(new Animation(config.tetrazeroAnimationSpeed, zRegions[0], zRegions[1]));
        tetrazeroAnimations.add(new Animation(config.tetrazeroAnimationSpeed, eRegions[0], eRegions[1]));
        tetrazeroAnimations.add(new Animation(config.tetrazeroAnimationSpeed, rRegions[0], rRegions[1]));
        tetrazeroAnimations.add(new Animation(config.tetrazeroAnimationSpeed, oRegions[0], oRegions[1]));
        for (Animation a : tetrazeroAnimations)
            a.setPlayMode(Animation.PlayMode.LOOP);

        tetrazeroStateTime = new float[]{
                config.tetrazeroAnimationSpeed * 1.0f,
                config.tetrazeroAnimationSpeed * 1.6f,
                config.tetrazeroAnimationSpeed * 1.4f,
                config.tetrazeroAnimationSpeed * 1.8f,
                config.tetrazeroAnimationSpeed * 1.2f};
        // tetrazeroX = new float[]{7.5f, 11f, 14.5f, 18f, 21.5f};
        tetrazeroX = new float[]{7.5f, 11f, 14.5f, 18f, 21.5f};

        tetrazeroY = new float[]{14, 14, 14, 14, 14};

        TextureRegion[][] allStartSprites = TextureRegion.split(startTexture, 256, 32);
        clicktostartAnimation = new Animation(config.clicktostartAnimationSpeed, allStartSprites[0][0], allStartSprites[1][0]);
        clicktostartAnimation.setPlayMode(Animation.PlayMode.LOOP);
        gameTitleLogo = allStartSprites[2][0];

        game.startMusic.stop();
        game.startMusic.play();

        if (!isFirstTry) {
            TiledMapTileLayer rulesLayer = (TiledMapTileLayer) map.getLayers().get("rules");
            rulesLayer.setVisible(true);
        }
    }

    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(config.screenBgR, config.screenBgG, config.screenBgB, config.screenBgA);
        camera.update();

        renderer.setView(camera);
        renderer.render();

        Batch batch = renderer.getBatch();
        batch.begin();

        // Draw click to start, use Tetra's time.
        TextureRegion frame = clicktostartAnimation.getKeyFrame(tetrazeroStateTime[0]);
        batch.draw(frame, 7.9f, 10, 16, 2);

        // Draw restarts.
        for (int i = 0; i < game.restartCounter; i++) {
            // batch.draw(restartTextureRegion, 17 + i*0.92f, 11.5f + i*0.05f, 2.5f, 2.5f);
            batch.draw(restartTextureRegion, 17 - i*0.3f, 10.5f + i*1.2f, 2.5f, 2.5f);
        }

        // Draw logo.
        batch.draw(gameTitleLogo, 6.6f, 20, 16 * 1.1f, 2 * 1.1f);

        // Draw tetra zero.
        for (int i = 0; i < 5; i++) {
            tetrazeroStateTime[i] = Math.min(tetrazeroStateTime[i] + deltaTime, 1000000);

            frame = tetrazeroAnimations.get(i).getKeyFrame(tetrazeroStateTime[i]);
            batch.draw(frame, tetrazeroX[i], tetrazeroY[i], 3, 6);
        }

        batch.end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            // Refresh game config.
            this.game.config = new GameConfig();
            this.game.setScreen(new GameScreen(this.game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        tetrazeroTexture.dispose();
        startTexture.dispose();
        restartTexture.dispose();
    }

}