package com.jollitycn.mobilekeybroad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.jasonhong.core.common.Callback;

public class MouseControlFragment extends Fragment {
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private Callback<TouchTrackView> callback;
    private TouchTrackView touchTrackView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 替换R.layout.fragment_mouse_control为你的布局文件ID  
        View rootView = inflater.inflate(R.layout.mouse_control_fragment, container, false);
        touchTrackView = rootView.findViewById(R.id.touch_track_view);
        if (callback != null) {
            callback.onResult(touchTrackView);
        }
        // ... 其他初始化代码 ...
        return rootView;
    }

    public TouchTrackView getTouchTrackView() {
        return touchTrackView;
    }


}