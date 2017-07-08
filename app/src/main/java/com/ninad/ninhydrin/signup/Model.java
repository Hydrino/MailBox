package com.ninad.ninhydrin.signup;


import android.util.Log;

import java.io.IOException;
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

class Model implements MVP.PresenterToModel {

    private MVP.ModelToPresenter modelToPresenter;
    private final OkHttpClient client = new OkHttpClient();
    private final String URL = "http://mohiteninad15.000webhostapp.com/add_new_user.php";
    private final String[] params = {"id", "Name"};
    private Subscription sub;


    Model(MVP.ModelToPresenter modelToPresenter) {
        this.modelToPresenter = modelToPresenter;
    }

    @Override
    public void requestNewUser(final String Name, final String UserID) {

        RequestBody body = returnRequestBody(Name, UserID);

        final Request request = returnRequest(body);

        Observable<Response> observable =  Observable.fromCallable(new Callable<Response>() {
            @Override
            public Response call() throws IOException {

                return client.newCall(request).execute();

            }
        });

        sub = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onCompleted() {
                        unsub();
                        Log.w("tag", "unsub");
                    }

                    @Override
                    public void onError(Throwable e) {
                        modelToPresenter.signUpFailure("Network Error");
                        Log.w("tag", "error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response response) {
                        try {
                            modelToPresenter.signUpResult(response.body().string(),Name,UserID);
                            Log.w("TAG", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

       /* client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                //  modelToPresenter.signUpFailure("Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // modelToPresenter.signUpResult(response.body().string());
                Log.w("TAG", response.body().string());
            }
        }); */
    }

    private void unsub() {
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }

    private Request returnRequest(RequestBody body) {
        return new Request.Builder().url(URL)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(body).build();
    }

    private RequestBody returnRequestBody(String Name, String UserID) {
        return new MultipartBody.Builder().setType(MultipartBody.FORM).
                addFormDataPart(params[0], UserID).
                addFormDataPart(params[1], Name).
                build();
    }

    MVP.PresenterToModel getPresenterToModel() {
        return this;
    }
}
