package com.ainpuw.tethyszero;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public GameConfig config;

    public SpriteBatch batch;

    public void create() {
        config = new GameConfig();

        batch = new SpriteBatch();
        this.setScreen(new StartScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
    }

}