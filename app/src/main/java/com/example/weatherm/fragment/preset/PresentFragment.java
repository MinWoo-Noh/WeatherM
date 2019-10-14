package com.example.weatherm.fragment.preset;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.data.Data;
import com.example.weatherm.MainActivity;
import com.example.weatherm.R;
import com.example.weatherm.data.ForecastData;
import com.example.weatherm.data.WeatherData;
import com.example.weatherm.WeatherManager;
import com.example.weatherm.WeatherUtil;

import java.util.ArrayList;
import java.util.List;


public class PresentFragment extends Fragment {

    private MainActivity activity;

    private RecyclerView recyclerView;
    private TextView presentWeatherTemperature;
    private TextView presentWeatherCity;
    private ImageView presentWeatherIcon;

    private PresentAdapter presentAdapter;
    private WeatherManager weatherManager;

    // 3시간별로 출력할 날씨 개수
    private final int WEATHER_COUNT = 10;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_present, null);

        presentWeatherTemperature = view.findViewById(R.id.present_weather_temperature);
        presentWeatherCity = view.findViewById(R.id.present_weather_city);
        presentWeatherIcon = view.findViewById(R.id.present_weather_icon);
        recyclerView = view.findViewById(R.id.present_recyclerview);

        // api 요청
        activity.showProgress("날씨 요청 중입니다.");

        weatherManager = new WeatherManager(activity, new WeatherManager.OnChangeWeather() {
            @Override
            public void change(WeatherData weatherData, ForecastData forecastData) {
                // api 응답 완료시 실행
                activity.hideProgress();

                loadTop(weatherData);
                loadBottom(forecastData);
            }
        });

        return view;
    }

    private void loadTop(WeatherData weatherData) {

        // 현재 도시 이름
        presentWeatherCity.setText(weatherData.getName());

        // 현재 날씨 이미지
        int weatherId = weatherData.getWeather().get(0).getId();
        int imageResource = WeatherUtil.getWeatherIcon(weatherId);
        presentWeatherIcon.setImageResource(imageResource);

        // 현재 온도
        String temp = WeatherUtil.getCelsius(weatherData.getMain().getTemp());
        presentWeatherTemperature.setText(temp + "˚");
    }

    private void loadBottom(ForecastData forecastData) {

        List<ForecastData.ListBean> forecastList = new ArrayList<>();

        for (int i = 0; i < WEATHER_COUNT; i++) {
            forecastList.add(forecastData.getList().get(i));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        presentAdapter = new PresentAdapter(forecastList);

//        presentAdapter.setData(forecastList);

        recyclerView.setAdapter(presentAdapter);
    }
}