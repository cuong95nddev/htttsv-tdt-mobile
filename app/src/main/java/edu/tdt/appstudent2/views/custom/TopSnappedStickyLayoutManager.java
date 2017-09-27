package edu.tdt.appstudent2.views.custom;

import android.content.Context;

import com.brandongogetap.stickyheaders.StickyLayoutManager;
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler;

/**
 * Created by bichan on 4/11/2017.
 */

public class TopSnappedStickyLayoutManager extends StickyLayoutManager {

    public TopSnappedStickyLayoutManager(Context context, StickyHeaderHandler headerHandler) {
        super(context, headerHandler);
    }

    @Override public void scrollToPosition(int position) {
        super.scrollToPositionWithOffset(position, 0);
    }
}
