package com.ainpuw.tethyszero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;

public class StartScreen implements Screen {

    final Main game;
    public BitmapFont font;

    OrthographicCamera camera;

    public StartScreen(final Main game) {
        this.game = game;

        font = new BitmapFont(); // use libGDX's default Arial font

        camera = new OrthographicCamera();
        // TODO: Set to full screen. Or let people choose full screen option at menu screen.
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        font.draw(game.batch, "Tethys Z.E.R.O.", 100, 150);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
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
        font.dispose();
    }

}