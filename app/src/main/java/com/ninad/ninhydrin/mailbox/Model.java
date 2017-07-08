package com.ninad.ninhydrin.mailbox;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

/* The Model for the MainActivity
* Networks calls are done using OkHttp
* ArrayList is maintained as a cache(UnreadLetters)
* RxJava is used to make process asynchronous
*/

public class Model implements MVP.PresenterToModel {

    private MVP.ModelToPresenter modelToPresenter;
    private final OkHttpClient client = new OkHttpClient();
    private final String FETCH_LETTERS_URL = "http://mohiteninad15.000webhostapp.com/get_letters.php";
    private final String UPDATE_STATUS_URL = "http://mohiteninad15.000webhostapp.com/update_status.php";
    private final String[] params = {"recipient", "id", "status"};
    private ArrayList<Letter> UnreadLetters;
    private ArrayList<Letter> ReadLetters;
    private ArrayList<Letter> ArchiveLetters;
    private Subscription subscription, update_status_subscription;

    Model(MVP.ModelToPresenter modelToPresenter) {
        this.modelToPresenter = modelToPresenter;
        UnreadLetters = new ArrayList<>();
        ReadLetters = new ArrayList<>();
        ArchiveLetters = new ArrayList<>();
    }

    @Override
    public void retrieveNewLettersRequest(String RecipientID) {

        // if we already loaded the letters, just pass them
        if (UnreadLetters != null && UnreadLetters.size() != 0) {
            modelToPresenter.retrieveNewLettersSuccess(UnreadLetters);
            Log.w("new", "Letters given from cache");
            return;
        }

        // load the letters from the server

        retrieveLetters(RecipientID, 0);
    }

    @Override
    public void retrieveReadLettersRequest(String RecipientID) {

        if (ReadLetters != null && !ReadLetters.isEmpty()) {
            modelToPresenter.retrieveReadLettersSuccess(ReadLetters);
            Log.w("new", "read letters given from cache");
            return;
        }

        retrieveLetters(RecipientID, 1);
    }

    @Override
    public void retrieveArchiveLettersRequest(String RecipientID) {
        if (ArchiveLetters != null && !ArchiveLetters.isEmpty()) {
            modelToPresenter.retrieveArchiveLettersSuccess(ArchiveLetters);
            Log.w("new", "archive letters given from cache");
            return;
        }

        retrieveLetters(RecipientID, 2);
    }

    private void retrieveLetters(String RecipientID, final int status) {

        RequestBody body = returnRequestBody(RecipientID, status);

        final Request request = returnRequest(FETCH_LETTERS_URL, body);

        Observable<Response> observable = Observable.fromCallable(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return client.newCall(request).execute();
            }
        });

        subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Observer<Response>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                if (status == 0)
                                    modelToPresenter.retrieveNewLettersFailed();
                                else if (status == 1)
                                    modelToPresenter.retrieveReadLettersFailed();
                                else
                                    modelToPresenter.retrieveArchiveLettersFailed();
                            }

                            @Override
                            public void onNext(Response response) {

                                if (!response.isSuccessful()) {
                                    if (status == 0)
                                        modelToPresenter.retrieveNewLettersFailed();
                                    else if (status == 1)
                                        modelToPresenter.retrieveReadLettersFailed();
                                    else
                                        modelToPresenter.retrieveArchiveLettersFailed();

                                    return;
                                }
                                try {
                                    String jsonResponse = response.body().string();

                                    JSONObject object = new JSONObject(jsonResponse);
                                    JSONArray jsonArray = object.getJSONArray("letters");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonLetter = jsonArray.getJSONObject(i);
                                        Letter letter = new Letter(jsonLetter.getString("Name"),
                                                jsonLetter.getString("sender_id"),
                                                jsonLetter.getString("body"),
                                                jsonLetter.getString("date"),
                                                jsonLetter.getInt("id"));
                                        if (status == 0)
                                            UnreadLetters.add(letter);
                                        else if (status == 1)
                                            ReadLetters.add(letter);
                                        else
                                            ArchiveLetters.add(letter);
                                    }
                                    if (status == 0)
                                        modelToPresenter.retrieveNewLettersSuccess(UnreadLetters);
                                    else if (status == 1)
                                        modelToPresenter.retrieveReadLettersSuccess(ReadLetters);
                                    else
                                        modelToPresenter.retrieveArchiveLettersSuccess(ArchiveLetters);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    private RequestBody returnRequestBody(String RecipientID, int status) {
        return new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(params[0], RecipientID)
                .addFormDataPart("status", Integer.toString(status))
                .build();
    }

    private Request returnRequest(String Url, RequestBody body) {
        return new Request.Builder().url(Url)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(body).build();
    }

    @Override
    public void unSubscribeClient() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            Log.w("new", "Unsubed");
        }
    }

    @Override
    public void markLetterAsRead(int index, int id) {
        updateStatus(index, id, 1);
    }

    @Override
    public void markLetterAsArchive(int index, int id) {
        updateStatus(index, id, 2);
    }

    private void updateStatus(final int index, int id, final int status) {

        final Letter letter = UnreadLetters.get(index);
        UnreadLetters.remove(index);

        if (status == 1)
            ReadLetters.add(0, letter);
        else if (status == 2)
            ArchiveLetters.add(0, letter);

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(params[1], Integer.toString(id))
                .addFormDataPart(params[2], Integer.toString(status)).build();

        final Request request = returnRequest(UPDATE_STATUS_URL, body);

        Observable<Response> observable = Observable.fromCallable(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return client.newCall(request).execute();
            }
        });

        update_status_subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Observer<Response>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                UnreadLetters.add(index, letter);
                                if (status == 1)
                                    ReadLetters.remove(0);
                                else if (status == 2)
                                    ArchiveLetters.remove(0);
                            }

                            @Override
                            public void onNext(Response response) {
                                try {
                                    if (!response.isSuccessful() || !response.body().string().equals("success"))
                                        UnreadLetters.add(index, letter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    MVP.PresenterToModel getPresenterToModel() {
        return this;
    }

    // the POJO letter class
    public class Letter {
        private String SenderName, SenderID, Body, Date;
        private int id;

        Letter(String senderName, String senderID, String body, String date, int id) {
            SenderName = senderName;
            SenderID = senderID;
            Body = body;
            Date = date;
            this.id = id;
        }

        public String getSenderName() {

            return SenderName;
        }

        public String getSenderID() {
            return SenderID;
        }

        public String getBody() {
            return Body;
        }

        public String getDate() {
            return Date;
        }

        public int getId() {
            return id;
        }
    }

}
