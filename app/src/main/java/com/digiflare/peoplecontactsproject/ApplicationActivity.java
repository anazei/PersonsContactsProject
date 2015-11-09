package com.digiflare.peoplecontactsproject;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.Cast.ApplicationConnectionResult;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.cast.RemoteMediaPlayer.MediaChannelResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.cast.MediaMetadata;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;

import com.digiflare.peoplecontactsproject.interfaces.FragmentListener;
import com.digiflare.peoplecontactsproject.model.DBModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class ApplicationActivity extends AppCompatActivity implements FragmentListener {

    private String SHARED_PREFERENCE_KEY = "sharedKey";

    private Fragment1_port fragment1;
    private Fragment2_port fragment2;
    private FragmentListener fragment2Listener;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //chromecast specific properties
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MyMediaRouterCallback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private RemoteMediaPlayer mRemoteMediaPlayer;
    private Cast.Listener mCastListener;
    private boolean waitingForReconnect = false;
    private boolean applicationStarted = false;
    private boolean videoIsLoaded;
    private boolean isPlaying;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        fragment2 = new Fragment2_port();
        fragment1 = new Fragment1_port();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        //DBModel check database to try to repopulate the DBModel's ArrayList<MasterRecord>

        //check sharedPreferences to see if there was previously anything in the first and last name fields to repopulate currentMasterRecord
        String jsonString = sharedPreferences.getString(SHARED_PREFERENCE_KEY, "");

        if(!TextUtils.isEmpty(jsonString)) {

            Log.d("kevin", "recreated activity, json string to be filled in is: " + jsonString);

            Bundle bundle = new Bundle();
            bundle.putString("hello", jsonString);
            fragment1.setArguments(bundle);

        }

        //place both fragments into the correct ID linear layout
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.bottomPanelUi, fragment2, Fragment2_port.NAME)
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.topPanelUi, fragment1, Fragment1_port.NAME)
                .commit();

        //provide an application context for DBModel
        DBModel.setContext(getApplicationContext());


        // init and configure ChromeCast device discovery - unique chromecast appID is provided here
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                        .addControlCategory(CastMediaControlIntent.categoryForCast(getResources().getString(R.string.app_id)))
                        .build();
        mMediaRouterCallback = new MyMediaRouterCallback();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        Log.d("kevin", "oncreate options menu");

        getMenuInflater().inflate(R.menu.menu_application, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        menuItem = menu.findItem(R.id.play_video);
        menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {

                //begin video playback
                //mRemoteMediaPlayer.play(mApiClient);
                startVideo();

                return false;
            }
        });



        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start media router discovery
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
        Log.d("kevin", "onstart");
    }

    public void showNoteField(){
        fragment2Listener.showNoteField();
    }

    public void updateRecyclerView(){
        fragment2Listener.updateRecyclerView();
    }

    public void clearRecyclerViewAndHideAddNoteSection(){
        fragment2Listener.clearRecyclerViewAndHideAddNoteSection();
    }

    /**
     * Register a new listener for fragment2
     */
    public void registerNewListener(FragmentListener fragment){
        fragment2Listener = fragment;
    }

    /**
     * onStop during screen orientation, retrieve all the user profiles from DBModel and convert into JSON string
     *
     * if there is no master record, then it will save NO_DATA,
     * else JSON string of master record will be saved into shared preferences
     */
    @Override
    protected void onStop() {
        super.onStop();

        //Log.d("kevin", "current master record: " + DBModel.getCurrentMasterRecord());

        //String jsonString = DBModel.getCurrentMasterRecord();
        String jsonString = fragment1.getFirstAndLastNameFields();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();

        if(jsonString != null){

            editor.putString(SHARED_PREFERENCE_KEY, jsonString);
            //Log.d("kevin", "master record exists");

        } else {
            editor.putString("NO_DATA", "");
            //Log.d("kevin", "master record does not exist");
        }

        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fragment2Listener = null;

        //take all of DBModel's ArrayList<MasterRecord> and serialize into json string
        String jsonString = DBModel.convertToJSONArrayString();

        //save the string into database when leaving app


    }

    ////////////////////////////////////////// Chrome cast specific //////////////////////////////////////////

    //cast listener
    private void initCastListener(){
        mCastListener = new Cast.Listener(){
            @Override
            public void onApplicationStatusChanged() {
                super.onApplicationStatusChanged();
            }

            @Override
            public void onVolumeChanged() {
                super.onVolumeChanged();
            }

            @Override
            public void onApplicationDisconnected(final int statusCode) {
                super.onApplicationDisconnected(statusCode);
            }
        };
    }

    //remote media player - this will control the receiver app
    private void initRemoteMediaPlayer(){
        mRemoteMediaPlayer = new RemoteMediaPlayer();
        mRemoteMediaPlayer.setOnStatusUpdatedListener(new RemoteMediaPlayer.OnStatusUpdatedListener(){

            @Override
            public void onStatusUpdated() {
                MediaStatus mediaStatus = mRemoteMediaPlayer.getMediaStatus();
                isPlaying = mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING;
            }
        });

        mRemoteMediaPlayer.setOnMetadataUpdatedListener(new RemoteMediaPlayer.OnMetadataUpdatedListener(){

            @Override
            public void onMetadataUpdated() {

            }
        });

    }

    //launch receiver app
    private void launchReceiver(){
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(mSelectedDevice, mCastListener);

        ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks();
        ConnectionFailedListener mConnectionFailedListener = new ConnectionFailedListener();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();

        mApiClient.connect();

    }

    private void startVideo(){
        MediaMetadata mediaMetadata = new MediaMetadata( MediaMetadata.MEDIA_TYPE_MOVIE );
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, "Kevin's ChromeCast Video Stream");

        MediaInfo mediaInfo = new MediaInfo.Builder("https://ia700408.us.archive.org/26/items/BigBuckBunny_328/BigBuckBunny_512kb.mp4")
                .setContentType("video/mp4")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .build();

        try{

            mRemoteMediaPlayer.load(mApiClient, mediaInfo, true)
                    .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(final MediaChannelResult mediaChannelResult) {
                            if(mediaChannelResult.getStatus().isSuccess()){
                                Log.d("kevin", "success if true");
                            }
                        }
                    });

        } catch(Exception exception){

        }
    }

    /**
     * Callback for MediaRouter events
     */
    private class MyMediaRouterCallback extends MediaRouter.Callback {

        //device pairing has been chosen
        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo info) {

            Log.d("kevin", "onRouteSelected");

            initCastListener();
            initRemoteMediaPlayer();

            // Handle the user route selection.
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            launchReceiver();
        }

        //device pairing has been disconnected
        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo info) {
            Log.d("kevin", "onRouteUnselected: info=" + info);
            //teardown(false);
            mSelectedDevice = null;
        }
    }

    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(final Bundle bundle) {

            //AppID is what unique receiver HTML app end point it is being pointed to,
            //kind of like www.google.com except the ID is based on an unique integer value
            Cast.CastApi.launchApplication(mApiClient, getString(R.string.app_id), false)
                    .setResultCallback(
                            new ResultCallback<ApplicationConnectionResult>() {
                                @Override
                                public void onResult(final ApplicationConnectionResult applicationConnectionResult) {
                                    Status status = applicationConnectionResult.getStatus();
                                    if( status.isSuccess() ) {
                                        Log.d("kevin", "connection call backs success");
                                    }
                                }
                            }
                    );
        }

        @Override
        public void onConnectionSuspended(final int i) {

        }
    }

    private class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener{
        @Override
        public void onConnectionFailed(final ConnectionResult connectionResult) {

        }
    }

}