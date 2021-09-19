package com.ainpuw.tethyszero;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    private GameConfig config;

    private OrthographicCamera camera;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public GameScreen(final Main game) {
        config = game.config;

        camera = new OrthographicCamera();
        camera.setToOrtho(config.ydown, config.w, config.h);
        camera.position.x = config.camStartPosX;
        camera.position.y = config.camStartPosY;

        map = new TmxMapLoader().load("background.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / (float) config.tileLen);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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
}
