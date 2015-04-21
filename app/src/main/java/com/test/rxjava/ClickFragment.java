package com.test.rxjava;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClickFragment extends Fragment {
    TextView textClicks;

    Action1<Throwable> error = new Action1<Throwable>() {
        @Override
        public void call(final Throwable throwable) {
            Log.e("TAG", throwable.getLocalizedMessage());
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_click, container, false);

        textClicks = (TextView) v.findViewById(R.id.text_clicks);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewObservable.clicks(textClicks)
                .buffer(1, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .filter(new Func1<List<OnClickEvent>, Boolean>() {
                    @Override
                    public Boolean call(final List<OnClickEvent> onClickEvents) {
                        return onClickEvents.size() > 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<List<OnClickEvent>, Integer>() {
                    @Override
                    public Integer call(final List<OnClickEvent> onClickEvents) {
                        return onClickEvents.size();
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        if (integer == 3) {
                            textClicks.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                        }else{
                            textClicks.setBackgroundColor(getResources().getColor(android.R.color.white));
                        }
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer integer) {
                        setText(String.format("Clicked %d times", integer.intValue()));
                    }
                }, error);

    }

    void setText(String text) {
        textClicks.setText(text);
    }
}
