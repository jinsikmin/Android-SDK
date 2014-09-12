package fm.feed.android.playersdk.service;

import android.util.Log;

import java.util.List;

import fm.feed.android.playersdk.model.Placement;
import fm.feed.android.playersdk.model.Play;
import fm.feed.android.playersdk.model.Station;

/**
 * Created by mharkins on 8/29/14.
 */
public class PlayInfo {
    private static final String TAG = PlayInfo.class.getSimpleName();

    /**
     * Possible statuses for the Player.
     *
     * <ul>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#WAITING} - Player is Waiting for Metadata from the Server</li>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#READY} - Player is ready to play music</li>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#PAUSED} - Audio playback is currently paused</li>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#PLAYING} - Audio playback is currently processing</li>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#STALLED} - Audio Playback has paused due to lack of audio data from the server</li>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#COMPLETE} - The player has run out of available music to play</li>
     * <li>{@link fm.feed.android.playersdk.service.PlayInfo.State#REQUESTING_SKIP} - The player is waiting for the server to say if the current song can be skipped</li>
     * </ul>
     */
    public static enum State {
        /**
         * Player is Waiting for Metadata from the Server
         */
        WAITING,
        /**
         * Player is ready to play music
         */
        READY,
        /**
         * Audio Feed is getting ready
         */
        TUNING,
        /**
         * Audio Feed is ready for playback
         */
        TUNED,
        /**
         * Audio playback is currently paused
         */
        PAUSED,
        /**
         * Audio playback is currently processing
         */
        PLAYING,
        /**
         * Audio Playback has paused due to lack of audio data from the server
         */
        STALLED,
        /**
         * The player has run out of available music to play
         * <p>
         * {@link fm.feed.android.playersdk.Player#tune()} or {@link fm.feed.android.playersdk.Player#play()}
         * will have to be called again to try to get more music from the server.
         * </p>
         */
        COMPLETE,
        /**
         * The player is waiting for the server to say if the current song can be skipped
         */
        REQUESTING_SKIP
    }

    private String mSdkVersion;
    private int mNotificationId;

    private String mClientId;

    private Placement mPlacement = null;
    private List<Station> mStationList;

    private State mState = null;
    private Station mStation = null;
    private Play mPlay = null;

    private boolean mSkippable;

    protected PlayInfo(String sdkVersion) {
        this.mSdkVersion = sdkVersion;

        setState(State.WAITING);
    }

    // -----------------
    // Protected Accessors
    // -----------------

    protected void setNotificationId(int notificationId) {
        this.mNotificationId = notificationId;
    }

    protected String getClientId() {
        return mClientId;
    }

    protected void setClientId(String mClientId) {
        this.mClientId = mClientId;
    }

    protected void setStationList(List<Station> mStationList) {
        this.mStationList = mStationList;
    }

    protected void setPlacement(Placement mPlacement) {
        this.mPlacement = mPlacement;
    }

    protected void setStation(Station mStation) {
        this.mStation = mStation;
    }

    protected boolean hasStationList() {
        return this.mStationList != null && this.mStationList.size() > 0;
    }

    protected void setSkippable(boolean skippable) {
        this.mSkippable = skippable;
    }

    protected void setCurrentPlay(Play currentPlay) {
        this.mPlay = currentPlay;
    }

    protected void setState(State state) {
        Log.d(TAG, String.format("PlayInfo.State changed: %s", state.name()));
        this.mState = state;
    }


    // -----------------
    // Public Accessors
    // -----------------

    /**
     * Version of this library
     *
     * @return The current version of this library. {v#.#}
     */
    public String getSdkVersion() {
        return mSdkVersion;
    }

    /**
     * The Notification Id used for persisting the Service in the background.
     * <p>
     * For more details see: <a href="http://developer.android.com/guide/components/services.html#Foreground">Running a Service in the Foreground</a>.
     * </p>
     *
     * @return
     */
    public int getNotificationId() {
        return mNotificationId;
    }

    /**
     * List of {@link fm.feed.android.playersdk.model.Station}s for the current {@link fm.feed.android.playersdk.model.Placement}
     *
     * @return The list of {@link fm.feed.android.playersdk.model.Station}s for the current {@link fm.feed.android.playersdk.model.Placement}.
     */
    public List<Station> getStationList() {
        return mStationList;
    }

    /**
     * Current {@link fm.feed.android.playersdk.model.Placement} information
     *
     * @return The current {@link fm.feed.android.playersdk.model.Placement} information.
     */
    public Placement getPlacement() {
        return mPlacement;
    }

    /**
     * Currently selected {@link fm.feed.android.playersdk.model.Station}
     *
     * @return The currently selected {@link fm.feed.android.playersdk.model.Station}.
     */
    public Station getStation() {
        return mStation;
    }

    /**
     * Current track ({@link fm.feed.android.playersdk.model.Play}) being played
     *
     * @return The current {@link fm.feed.android.playersdk.model.Play}.
     */
    public Play getPlay() {
        return mPlay;
    }

    /**
     * Can this {@link Play} be skipped
     * <p>
     * A user can only skip tracks a certain number of times. Depending on server/station rules.
     * </p>
     *
     * @return {@code true} if track can be skipped, {@code false} otherwise.
     */
    public boolean isSkippable() {
        return mSkippable;
    }

    /**
     * {@link fm.feed.android.playersdk.service.PlayInfo.State} of the Player.
     *
     * @return The current {@link fm.feed.android.playersdk.service.PlayInfo.State}.
     */
    public State getState() {
        return mState;
    }
}