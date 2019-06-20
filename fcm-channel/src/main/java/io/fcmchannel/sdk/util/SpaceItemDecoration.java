package io.fcmchannel.sdk.util;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private enum Type {
        Horizontal,
        Vertical
    }

    private Type type;

    private int horizontalSpaceWidth;
    private int verticalSpaceHeight;

    public void setHorizontalSpaceWidth(int horizontalSpaceWidth) {
        type = Type.Horizontal;
        this.horizontalSpaceWidth = horizontalSpaceWidth;
    }

    public void setVerticalSpaceHeight(int verticalSpaceHeight) {
        type = Type.Vertical;
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state
    ) {
        switch(type) {
            case Horizontal:
                outRect.right = horizontalSpaceWidth;
                break;
            case Vertical:
                outRect.top = verticalSpaceHeight;
                if (parent.getChildAdapterPosition(view) == 0)
                    outRect.bottom = verticalSpaceHeight;
        }

    }
}
