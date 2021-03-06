package com.polurival.kudzorover;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.I18NBundle;
import com.boontaran.games.StageGame;
import com.polurival.kudzorover.levels.LevelList;
import com.polurival.kudzorover.media.Media;
import com.polurival.kudzorover.screens.Intro;
import com.polurival.kudzorover.utils.Data;

import java.util.Locale;

public class LunarRover extends Game {

    public static final int SHOW_BANNER = 1;
    public static final int HIDE_BANNER = 2;
    public static final int LOAD_INTERSTITIAL = 3;
    public static final int SHOW_INTERSTITIAL = 4;
    public static final int OPEN_MARKET = 5;
    public static final int SHARE = 6;

    public static TextureAtlas atlas;
    public static BitmapFont font40;
    public static Media media; // загружает звуки
    public static Data data;

    private boolean loadingAssets = false;
    private AssetManager assetManager;

    private I18NBundle bundle; // для выбора ресурсов в зависимости от локализации
    private String path_to_atlas;

    private GameCallback gameCallback;

    private Intro intro;
    private LevelList levelList;

    public LunarRover(GameCallback gameCallback) {
        this.gameCallback = gameCallback;
    }

    @Override
    public void create() {
        StageGame.setAppSize(800, 480);

        Gdx.input.setCatchBackKey(true);

        Locale locale = Locale.getDefault();
        bundle = I18NBundle.createBundle(Gdx.files.internal("MyBundle"), locale);
        path_to_atlas = bundle.get("path");

        loadingAssets = true;
        assetManager = new AssetManager();
        assetManager.load(path_to_atlas, TextureAtlas.class);
        assetManager.load("musics/music1.ogg", Music.class);
        assetManager.load("musics/level_failed.ogg", Music.class);
        assetManager.load("musics/level_win.ogg", Music.class);
        assetManager.load("sounds/level_completed.ogg", Sound.class);
        assetManager.load("sounds/fail.ogg", Sound.class);
        assetManager.load("sounds/click.ogg", Sound.class);
        assetManager.load("sounds/crash.ogg", Sound.class);

        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter sizeParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        sizeParams.fontFileName = "fonts/GROBOLD.ttf";
        sizeParams.fontParameters.size = 40;

        assetManager.load("font40.ttf", BitmapFont.class, sizeParams);

        media = new Media(assetManager);
        data = new Data();
    }

    @Override
    public void render() {
        if (loadingAssets) {
            if (assetManager.update()) {
                loadingAssets = false;
                onAssetLoaded();
            }
        }
        super.render();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        super.dispose();
    }

    private void onAssetLoaded() {
        atlas = assetManager.get(path_to_atlas, TextureAtlas.class);
        font40 = assetManager.get("font40.ttf", BitmapFont.class);

        showIntro();
    }

    private void exitApp() {
        Gdx.app.exit();
    }

    private void showIntro() {
        intro = new Intro();
        setScreen(intro);

        intro.setCallback(new StageGame.Callback() {
            @Override
            public void call(int code) {
                if (code == Intro.ON_PLAY) {
                    showLevelList();
                    hideIntro();
                } else if (code == Intro.ON_BACK) {
                    exitApp();
                }
            }
        });

        media.playMusic("music1.ogg", true);
    }

    private void hideIntro() {
        intro = null;
    }

    private void showLevelList() {
        levelList = new LevelList();
        setScreen(levelList);

        levelList.setCallback(new StageGame.Callback() {
            @Override
            public void call(int code) {

                if (code == LevelList.ON_BACK) {
                    showIntro();
                    hideLevelList();
                } else if (code == LevelList.ON_LEVEL_SELECTED) {
                    //showLevel();
                    hideLevelList();
                } else if(code == LevelList.ON_OPEN_MARKET) {
                    gameCallback.sendMessage(OPEN_MARKET);
                } else if (code == LevelList.ON_SHARE) {
                    gameCallback.sendMessage(SHARE);
                }
            }
        });

        gameCallback.sendMessage(SHOW_BANNER);
        media.playMusic("music1.ogg", true);
    }

    private void hideLevelList() {
        levelList = null;
        gameCallback.sendMessage(HIDE_BANNER);
    }
}
