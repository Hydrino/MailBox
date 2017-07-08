package com.ninad.ninhydrin.new_letter;


import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Callable;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Model implements MVP.PresenterToModel {

    private MVP.ModelToPresenter modelToPresenter;
    private final OkHttpClient client = new OkHttpClient();
    private static final String URL = "http://mohiteninad15.000webhostapp.com/new_letter.php";
    private Subscription sub;

    public Model(MVP.ModelToPresenter modelToPresenter) {
        this.modelToPresenter = modelToPresenter;
    }

    @Override
    public void sendLetterRequest(String SenderID, String ReceiverID, String message) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
        String date = format.format(calendar.getTime());

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("sender_id", SenderID)
                .addFormDataPart("receiver_id", ReceiverID)
                .addFormDataPart("message", message)
                .addFormDataPart("date", date)
                .addFormDataPart("status", Integer.toString(0)).build();


        final Request request = new Request.Builder().url(URL)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(body).build();

        Observable<Response> Ob = Observable.fromCallable(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return client.newCall(request).execute();
            }
        });

        sub = Ob.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        modelToPresenter.Failure();
                    }

                    @Override
                    public void onNext(Response response) {
                        try {
                            if (response.isSuccessful() && response.body().string().equals("success")) {
                                modelToPresenter.Success();
                                Log.w("letter","success");
                            } else
                                modelToPresenter.Failure();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void unSub() {
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }

    MVP.PresenterToModel getPresenterToModel() {
        return this;
    }
}
