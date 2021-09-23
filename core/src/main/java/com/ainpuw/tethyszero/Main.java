package com.ainpuw.tethyszero;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Main extends Game {
    public GameConfig config;
    public int bestScore = 0;
    public Music backgroundMusic;

    public void create() {
        config = new GameConfig();
        this.setScreen(new StartScreen(this, true));
    }


    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        backgroundMusic.dispose();
    }

}