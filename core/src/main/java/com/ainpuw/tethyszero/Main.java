package com.ainpuw.tethyszero;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Main extends Game {
    public GameConfig config;
    public int bestScore = 0;
    public int restartCounter = 0;
    public Music startMusic;

    public void create() {
        config = new GameConfig();
        startMusic = Gdx.audio.newMusic(Gdx.files.internal("BadSpeechPSG3.mp3"));
        startMusic.setLooping(true);

        this.setScreen(new StartScreen(this, true));
    }


    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        startMusic.dispose();
    }

}