package com.test.rxjava;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class ReactiveFragment extends Fragment {

    public ReactiveFragment() {
    }


    EditText email;
    EditText password;
    Button login;

    Action1<Boolean> enableButton = new Action1<Boolean>() {
        //Here we subscribe to the stream of booleans
        //Every time email or password is changed, the function above is run and emits a boolean
        @Override
        public void call(final Boolean enableLogin) {
            //So all we have to do is set our login enabled to our stream from above.
            Log.e("Reactive", "SEE THIS RARELY GETS CALLED: " + enableLogin);
            login.setEnabled(enableLogin);
            login.setText("Login");
            login.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    };

    Action1<Throwable> logError = new Action1<Throwable>() {
        //An error handler is required.
        //Often I'll create a Action1<Throwable> class that simple logs the error message as a default to use app wide
        @Override
        public void call(final Throwable throwable) {
            Log.e("Reactive", "Error: " + throwable.getLocalizedMessage());
        }
    };
    //Created this Function to use to map the text event to the char sequence we need to check, since it's used twice
    Func1 textChangeCharSequence = new Func1<OnTextChangeEvent, CharSequence>() {
        @Override
        public CharSequence call(final OnTextChangeEvent onTextChangeEvent) {
            return onTextChangeEvent.text();
        }
    };

    Func1 isSequenceEmpty = new Func1<CharSequence, Boolean>() {

        @Override
        public Boolean call(final CharSequence charSequence) {
            return TextUtils.isEmpty(charSequence);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reactive, container, false);

        email = (EditText) v.findViewById(R.id.edit_email2);
        password = (EditText) v.findViewById(R.id.edit_password2);
        login = (Button) v.findViewById(R.id.btn_login2);


        //First we get our "stream" of CharSequence changes
        //Whenever email/password gets changed our textChangeCharSequence is called
        //Then emailChanged is a stream of CharSequences representing entered text
        //Then check fi the sequence is empty
        Observable<Boolean> emailChanged = WidgetObservable.text(email).map(textChangeCharSequence).map(isSequenceEmpty);
        Observable<Boolean> passwordChanged = WidgetObservable.text(password).map(textChangeCharSequence).map(isSequenceEmpty);

        //Let's create a stream of both changes
        //Then we can check those CharSequences and return true if both are non-empty
        Observable.combineLatest(emailChanged, passwordChanged, new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(final Boolean emailEmpty, final Boolean passwordEmpty) {
                return !emailEmpty && !passwordEmpty;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(enableButton, logError);

        ViewObservable.clicks(login)
                .take(1)
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.e("Reactive", "After one click, it completes");
                        login.setText("Clicked");
                        login.setEnabled(false);
                        login.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                })
                .subscribe(new Action1<OnClickEvent>() {
                    @Override
                    public void call(final OnClickEvent onClickEvent) {
                        Log.e("Reactive", "Only take one click");
                    }
                }, logError);

        return v;
    }
}
