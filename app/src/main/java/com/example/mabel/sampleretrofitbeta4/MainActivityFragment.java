package com.example.mabel.sampleretrofitbeta4;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String API_URL = "https://api.github.com";

    @Bind(R.id.textTest) TextView textTest;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btnTest)
    public void sayHi(Button button) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                                Request original = chain.request();


                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder()
                                        .header("Test", "abc")
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();

                                okhttp3.Response response = chain.proceed(request);

                                String bodyString = response.body().string();

                                System.out.println(bodyString);

                                return response;
                            }
                        })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        // Create an instance of our GitHub API interface.
        SimpleService.GitHub github = retrofit.create(SimpleService.GitHub.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<SimpleService.Contributor>> call = github.contributors("square", "retrofit");

        call.enqueue(new Callback<List<SimpleService.Contributor>>() {
            @Override
            public void onResponse(Call<List<SimpleService.Contributor>> call, Response<List<SimpleService.Contributor>> response) {

                System.out.println(response.body().toString());

                for (SimpleService.Contributor contributor : response.body()) {
                    System.out.println(contributor.login + " (" + contributor.contributions + ")");
                }
            }

            @Override
            public void onFailure(Call<List<SimpleService.Contributor>> call, Throwable t) {
                System.out.println("error");
            }
        });


    }

}
