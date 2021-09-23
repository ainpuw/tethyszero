package com.ainpuw.tethyszero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

    private Texture terazeroTexture = new Texture("tetrazero.png");;
    private Array<Animation<TextureRegion>> tetrazeroAnimations = new Array<>();
    float [] tetrazeroStateTime;
    float [] tetrazeroX;
    float [] tetrazeroY;

    Texture startTexture;
    private Animation<TextureRegion> clicktostartAnimation;

    public StartScreen(Main game, boolean isFirstTry) {
        this.game = game;
        config = this.game.config;

        camera.setToOrtho(config.ydown, config.w, config.h);
        camera.position.x = config.camStartPosX;
        camera.position.y = config.camStartPosY;
        renderer = new OrthogonalTiledMapRenderer(map, 1 / (float) config.tileLen);

        TextureRegion[][] allTetrazeroSprites = TextureRegion.split(terazeroTexture, 32, 64);
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

        startTexture = new Texture("clicktostart.png");
        TextureRegion[][] allStartSprites = TextureRegion.split(startTexture, 256, 32);
        if (isFirstTry)
            clicktostartAnimation = new Animation(config.clicktostartAnimationSpeed, allStartSprites[0][0], allStartSprites[1][0]);
        else
            clicktostartAnimation = new Animation(config.clicktostartAnimationSpeed, allStartSprites[2][0], allStartSprites[3][0]);
        clicktostartAnimation.setPlayMode(Animation.PlayMode.LOOP);

        if (game.backgroundMusic != null)
            game.backgroundMusic.dispose();
        game.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("JuhaniJunkala.wav"));
        game.backgroundMusic.setVolume(0.1f);
        game.backgroundMusic.setLooping(true);
        game.backgroundMusic.play();
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
        for (int i = 0; i < 5; i++) {
            tetrazeroStateTime[i] = Math.min(tetrazeroStateTime[i] + deltaTime, 1000000);

            TextureRegion frame = tetrazeroAnimations.get(i).getKeyFrame(tetrazeroStateTime[i]);
            batch.draw(frame, tetrazeroX[i], tetrazeroY[i], 3, 6);
        }

        // Use Tetra's time.
        TextureRegion frame = clicktostartAnimation.getKeyFrame(tetrazeroStateTime[0]);
        batch.draw(frame, 8, 11, 16, 2);

        batch.end();

        if (Gdx.input.isTouched()) {
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
        terazeroTexture.dispose();
        startTexture.dispose();
    }

}