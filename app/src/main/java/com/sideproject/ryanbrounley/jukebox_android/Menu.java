package com.sideproject.ryanbrounley.jukebox_android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.sideproject.ryanbrounley.jukebox_android.ui.MainThreeTabActivity;
import com.sideproject.ryanbrounley.jukebox_android.Playlist.*;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import java.util.List;

/**
 * Created by ryanbrounley on 10/22/15.
 *
 * Edited by Kyler Kiminki 10/22/16
 */

public class Menu extends MainThreeTabActivity implements PlayerNotificationCallback, ConnectionStateCallback{
    Bundle args;
    private static final String CLIENT_ID = "b2e9ab519e00426cbc10567e290ea8fd";
    public  Player mPlayer;
    public Playlist playlist;
    private int position = 0;
    public boolean playing = false;
    public boolean onPlayer = false;
    public String current="No song currently playing";
    public FragmentManager fm =getSupportFragmentManager();

    //Configures the player with the access token passed in
    //from MainActivity
    @Override
    public void onCreate(Bundle savedInstanceState){
        playlist = new Playlist();
        args = getIntent().getExtras();
        Log.d("Menu", "AccessToken = "+args.getString("AccessToken"));
        Config playerConfig = new Config(this, args.getString("AccessToken"), CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer.addConnectionStateCallback(Menu.this);
                mPlayer.addPlayerNotificationCallback(Menu.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("Menu", "Could not initialize player: " + throwable.getMessage());
            }
        });
        super.onCreate(savedInstanceState);
    }

    //Helper function to play a song
    public void PlaySong(Song song){
        playlist.addSong(song);
        current = song.getArtists()+": "
                +song.getName();
        mPlayer.play(song.getUri());
        Log.d("Menu", "Calling PlaySong");
        Log.d("Menu", "URI = "+song);
        if(onPlayer)
            updatePlayer();
    }

    //Helper function to pause the player
    public void PausePlayer(){
        mPlayer.pause();
        Log.d("Menu", "Calling Pause");
    }

    //Helper function to play the next song
    public void PlayNext(){
        mPlayer.skipToNext();
        Log.d("Menu", "Calling play next");
    }

    //Helper function to play the previous song
    public void PlayPrev(){
        mPlayer.skipToPrevious();
        Log.d("Menu", "Calling play prev");
    }

    public void ResumePlayer(){
        mPlayer.resume();
    }

    //Helper function to set the repeat value on the player
    public void PlayerRepeat(boolean b){
        mPlayer.setRepeat(b);
    }

    //Helper function to put a song in the queue
    public void PlayerEnqueue(Song song){
        Log.d("Menu", "song uri = "+song.getUri());
        /*if(playlist.isEmpty()) {
            playlist.addSong(song);
            upNext=song.getArtists()+": "+song.getName();
            mPlayer.queue(playlist.popSong().getUri());
        }else{*/
        playlist.addSong(song);
        if(onPlayer)
            updatePlayer();
    }

    public void PlayerRemoveSong(Song song){
        playlist.remove(song);
    }

    //Sets the initial fragment of the menu activity
    @Override
    protected Fragment getInitialFragment() {
        Fragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(args);
        return playerFragment;
    }

    @Override
    public void onLoggedIn() {
        Log.d("Menu", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("Menu", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("Menu", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("Menu", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("Menu", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        Log.d("Menu", "Playback event received: " + eventType.name());
        switch (eventType) {
            case PLAY:
                playing=true;
                break;
            case END_OF_CONTEXT:
                playing=false;
                break;
            case TRACK_CHANGED:
                if(!playlist.isEmpty()){
                    current =  playlist.getSongAt(0).getArtists()+": "
                            +playlist.getSongAt(0).getName();
                    mPlayer.queue(playlist.popSong().getUri());
                    Log.d("Menu", "playing song at position "+position);
                    //firebase.getPlayist
                    playlist.vetoScan();
                    playlist.sort();
                    if(onPlayer)
                        updatePlayer();
                }else{
                    current = "No song currently playing";
                    if(onPlayer)
                        updatePlayer();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {
        Log.d("Menu", "Playback error received: " + errorType.name());
        switch (errorType) {

            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    public void updatePlayer(){
        fm.beginTransaction().replace(R.id.container, new PlayerFragment()).commit();
    }
}
