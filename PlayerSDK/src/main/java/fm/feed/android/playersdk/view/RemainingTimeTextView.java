package fm.feed.android.playersdk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import fm.feed.android.playersdk.NavListener;
import fm.feed.android.playersdk.Player;
import fm.feed.android.playersdk.R;
import fm.feed.android.playersdk.model.Play;
import fm.feed.android.playersdk.model.Station;
import fm.feed.android.playersdk.util.TimeUtils;

/**
 * This TextView extension updates itself to display how much of the current
 * song has been played.
 */

public class RemainingTimeTextView extends TextView implements NavListener {

    private static final String TAG = RemainingTimeTextView.class.getSimpleName();

    private Player mPlayer;
    private String mTextForNoTime = "";

    public RemainingTimeTextView(Context context) {
        super(context);

        init(null);
    }

    public RemainingTimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public RemainingTimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FeedFmEmptyTimeTextView);

        String textForNoTime = a.getString(R.styleable.FeedFmEmptyTimeTextView_textForNoTime);
        if (textForNoTime != null) {
            setTextForNoTime(textForNoTime);
        }

        a.recycle();

        resetProgress();

        if (isInEditMode()) {
            return;
        }

        mPlayer = Player.getInstance();
        mPlayer.registerNavListener(this);
    }

    public void setTextForNoTime(String textForNoTime) {
        mTextForNoTime = textForNoTime;

        resetProgress();
    }

    public String getTextForNoTime() {
        return mTextForNoTime;
    }

    private void resetProgress() {
        setText(mTextForNoTime);
    }

    @Override
    public void onStationChanged(Station station) {

    }

    @Override
    public void onTrackChanged(Play play) {
        resetProgress();
    }

    @Override
    public void onEndOfPlaylist() {

    }

    @Override
    public void onSkipFailed() {

    }

    @Override
    public void onBufferUpdate(Play play, int percentage) {

    }

    @Override
    public void onProgressUpdate(Play play, int elapsedTime, int totalTime) {
        setText(TimeUtils.toProgressFormat(elapsedTime - totalTime));
        setContentDescription(TimeUtils.toProgressAccessibilityFormat(elapsedTime - totalTime));
    }
}
