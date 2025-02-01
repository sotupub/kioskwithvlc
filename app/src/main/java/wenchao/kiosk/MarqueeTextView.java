package wenchao.kiosk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView extends AppCompatTextView {
    private Scroller mScroller;

    public MarqueeTextView(Context context) {
        super(context);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSingleLine();
        setEllipsize(null);
        setHorizontallyScrolling(true);
        mScroller = new Scroller(getContext(), new LinearInterpolator());
        setScroller(mScroller);
    }

    public void startMarquee() {
        int scrollLength = getWidth();
        int distance = scrollLength + getWidth();
        int duration = 5000; // 5 seconds, adjust this value to speed up or slow down the scroll

        mScroller.startScroll(-scrollLength, 0, distance, 0, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) {
            startMarquee();
        } else {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
