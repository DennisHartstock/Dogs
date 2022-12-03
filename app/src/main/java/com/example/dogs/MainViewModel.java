package com.example.dogs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    public static final String BASE_URL = "https://dog.ceo/api/breeds/image/random";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_STATUS = "status";

    private final MutableLiveData<DogImage> dogImageMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<DogImage> getDogImageMutableLiveData() {
        return dogImageMutableLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public void loadDogImage() {
        Disposable disposable = loadDogImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> isLoading.setValue(true))
                .doOnError(throwable -> isError.setValue(true))
                .doAfterTerminate(() -> isLoading.setValue(false))
                .subscribe(
                        dogImageMutableLiveData::setValue
                );
        compositeDisposable.add(disposable);
    }

    private Single<DogImage> loadDogImageRx() {
        return Single.fromCallable(() -> {
            URL url = new URL(BASE_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder data = new StringBuilder();
            String result;
            do {
                result = bufferedReader.readLine();
                if (result != null) {
                    data.append(result);
                }
            } while (result != null);

            JSONObject jsonObject = new JSONObject(data.toString());
            String message = jsonObject.getString(KEY_MESSAGE);
            String status = jsonObject.getString(KEY_STATUS);
            return new DogImage(message, status);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}



