package fm.feed.android.SampleApp.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fm.feed.android.playersdk.NavListener;
import fm.feed.android.playersdk.Player;
import fm.feed.android.playersdk.PlayerError;
import fm.feed.android.playersdk.PlayerListener;
import fm.feed.android.playersdk.SocialListener;
import fm.feed.android.playersdk.model.Play;
import fm.feed.android.playersdk.model.Station;
import fm.feed.android.playersdk.service.PlayInfo;
import fm.feed.android.playersdk.util.TimeUtils;
import fm.feed.android.SampleApp.R;

/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Feed Media, Inc
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p/>
 * Created by mharkins on 8/22/14.
 */
public class TestFragment extends Fragment {

    private final static String TAG = Fragment.class.getSimpleName();


    private Player mPlayer;

    // Views
    private Button mBtnTune;
    private ImageButton mBtnPlay;
    private ImageButton mBtnPause;
    private ImageButton mBtnSkip;
    private Button mBtnLike;
    private Button mBtnUnlike;
    private Button mBtnDislike;
    private Button mBtnHistory;

    private TextView mTxtTitle;
    private TextView mTxtArtist;
    private TextView mTxtAlbum;


    private TextView mTxtCurrentProgress;
    private TextView mTxtDuration;

    private ProgressBar mProgressBar;

    private ListView mStationsView;

    public Button mBtnToggleWifi;

    private int mSelectedStationIndex = -1;

    public TestFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayer = Player.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);

        mBtnTune = (Button) rootView.findViewById(R.id.tune);
        mBtnPlay = (ImageButton) rootView.findViewById(R.id.play);
        mBtnPause = (ImageButton) rootView.findViewById(R.id.pause);
        mBtnSkip = (ImageButton) rootView.findViewById(R.id.skip);
        mBtnLike = (Button) rootView.findViewById(R.id.like);
        mBtnUnlike = (Button) rootView.findViewById(R.id.unlike);
        mBtnDislike = (Button) rootView.findViewById(R.id.dislike);
        mBtnHistory = (Button) rootView.findViewById(R.id.history);

        mTxtTitle = (TextView) rootView.findViewById(R.id.title);
        mTxtArtist = (TextView) rootView.findViewById(R.id.artist);
        mTxtAlbum = (TextView) rootView.findViewById(R.id.album);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        mTxtCurrentProgress = (TextView) rootView.findViewById(R.id.current_progress);
        mTxtDuration = (TextView) rootView.findViewById(R.id.duration);

        mStationsView = (ListView) rootView.findViewById(R.id.stations);

        mBtnToggleWifi = (Button) rootView.findViewById(R.id.wifi);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBtnTune.setOnClickListener(tune);
        mBtnPlay.setOnClickListener(play);
        mBtnPause.setOnClickListener(pause);
        mBtnSkip.setOnClickListener(skip);
        mBtnLike.setOnClickListener(like);
        mBtnUnlike.setOnClickListener(unlike);
        mBtnDislike.setOnClickListener(dislike);
        mBtnHistory.setOnClickListener(history);

        mStationsView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mBtnToggleWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(!isConnected); // true or false to activate/deactivate wifi

                mBtnToggleWifi.setText(!isConnected ? "Wifi ON" : "Wifi OFF");
            }
        });

        resetTrackInfo();

        if (mPlayer.hasPlay()) {
            updateTrackInfo(mPlayer.getPlay());
        }
        if (mPlayer.hasStationList()) {
            updateStations(mPlayer.getStationList());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mPlayer.registerNavListener(mNavigationListener);
        mPlayer.registerPlayerListener(mPlayerListener);
        mPlayer.registerSocialListener(mSocialListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        mPlayer.unregisterNavListener(mNavigationListener);
        mPlayer.unregisterPlayerListener(mPlayerListener);
        mPlayer.unregisterSocialListener(mSocialListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private PlayerListener mPlayerListener = new PlayerListener() {
        @Override
        public void onPlayerInitialized(PlayInfo playInfo) {
            updateTitle(playInfo);

            // This is going to be called every time the Player registers itself to the service.
            // For example when the app resumes.
            // A Track may already be playing when that happens.
            if (playInfo.getPlay() != null) {
                mNavigationListener.onTrackChanged(playInfo.getPlay());
            }
        }

        @Override
        public void onPlaybackStateChanged(PlayInfo.State state) {
            /**
             * Handle each state change by updating the UI
             */
            switch (state) {
                case WAITING:
                    break;
                case READY:
                    break;
                case TUNING:
                    break;
                case TUNED:
                    break;
                case PAUSED:
                    break;
                case PLAYING:
                    break;
                case STALLED:
                    break;
                case COMPLETE:
                    break;
                case REQUESTING_SKIP:
                    break;
            }
        }

        @Override
        public void onSkipStatusChange(boolean skippable) {

        }

        @Override
        public void onError(PlayerError playerError) {
            // Display error
            Toast.makeText(getActivity(), playerError.toString(), Toast.LENGTH_LONG).show();
        }
    };


    private NavListener mNavigationListener = new NavListener() {
        @Override
        public void onStationChanged(Station station) {
            resetTrackInfo();
            mStationsView.setSelection(mSelectedStationIndex);
            Toast.makeText(getActivity(), String.format("Station set to: %s (%s)", station.getName(), station.getId()), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTrackChanged(Play play) {
            resetTrackInfo();

            updateTrackInfo(play);

        }

        @Override
        public void onSkipFailed() {
            Toast.makeText(getActivity(), "Cannot Skip Track", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onEndOfPlaylist() {
            Toast.makeText(getActivity(), "Reached end of Playlist", Toast.LENGTH_LONG).show();
            resetTrackInfo();
        }

        @Override
        public void onBufferUpdate(Play play, int percentage) {
            mProgressBar.setSecondaryProgress((percentage * play.getAudioFile().getDurationInSeconds()) / 100);
        }

        @Override
        public void onProgressUpdate(Play play, int elapsedTime, int totalTime) {
            mProgressBar.setProgress(elapsedTime);
            mTxtCurrentProgress.setText(TimeUtils.toProgressFormat(elapsedTime));
            mTxtDuration.setText(TimeUtils.toProgressFormat(totalTime));
        }
    };

    private SocialListener mSocialListener = new SocialListener() {
        @Override
        public void onLiked() {
            Toast.makeText(getActivity(), "Liked!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUnliked() {
            Toast.makeText(getActivity(), "UnLiked!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisliked() {
            Toast.makeText(getActivity(), "DisLiked!", Toast.LENGTH_LONG).show();
        }
    };

    private void resetTrackInfo() {
        int max = 0;
        int min = 0;
        mTxtCurrentProgress.setText(TimeUtils.toProgressFormat(min));
        mTxtDuration.setText(TimeUtils.toProgressFormat(max));

        mProgressBar.setProgress(min);
        mProgressBar.setSecondaryProgress(min);
        mProgressBar.setMax(min);

        mTxtTitle.setText("");
        mTxtArtist.setText("");
        mTxtAlbum.setText("");
    }

    @SuppressWarnings("unused")
    private View.OnClickListener tune = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.tune();
        }
    };

    private View.OnClickListener play = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.play();
        }
    };

    private View.OnClickListener pause = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.pause();
        }
    };

    private View.OnClickListener skip = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.skip();
        }
    };

    private View.OnClickListener like = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.like();
        }
    };

    private View.OnClickListener unlike = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.unlike();
        }
    };

    private View.OnClickListener dislike = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayer.dislike();
        }
    };

    private View.OnClickListener history = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Collection<Play> history = mPlayer.getPlayHistory();

            Log.i(TAG, "Play history (most recent first):");
            for (Play play : history) {
                Log.i(TAG, "  -  " + play.getId() + ": " + play.getAudioFile().getTrack().getTitle());
            }
        }
    };

    private void updateTitle(PlayInfo playInfo) {
        final PackageManager pm = getActivity().getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(getActivity().getPackageName(), 0);

        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        getActivity().setTitle(applicationName + " (sdk: " + playInfo.getSdkVersion() + ")");
    }

    private void updateStations(List<Station> stationList) {
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for (Station s : stationList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Id", s.getId() + "");
            map.put("Station", s.getName());
            fillMaps.add(map);
        }

        final SimpleAdapter adapter = new SimpleAdapter(
                getActivity(),
                fillMaps,
                R.layout.list_item,
                new String[]{"Id", "Station"},
                new int[]{R.id.id, R.id.name});
        mStationsView.setAdapter(adapter);
        mStationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedStationIndex = position;

                HashMap<String, Integer> item = (HashMap<String, Integer>) adapter.getItem(position);
                Integer stationId = Integer.parseInt(String.valueOf(item.get("Id")));
                mPlayer.setStationId(stationId);
            }
        });
    }

    private void updateTrackInfo(Play play) {
        mProgressBar.setMax(play.getAudioFile().getDurationInSeconds());
        mTxtDuration.setText(TimeUtils.toProgressFormat(play.getAudioFile().getDurationInSeconds()));

        mTxtTitle.setText(play.getAudioFile().getTrack().getTitle());
        mTxtArtist.setText(play.getAudioFile().getArtist().getName());
        mTxtAlbum.setText(play.getAudioFile().getRelease().getTitle());
    }

}
