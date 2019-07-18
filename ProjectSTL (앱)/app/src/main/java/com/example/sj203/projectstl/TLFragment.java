package com.example.sj203.projectstl;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TLFragment extends Fragment {


    public TLFragment() {
        // Required empty public constructor
    }

    // 바꿔줘야하는 항목들 선언
    ImageView fl_image;
    TextView togreen_text;

    //타이머 선언
    private CountDownTimer countdowntimer;
    private long timeleftmilliseconds = 120000;
    private boolean timeRunning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tl, container, false);

        // 바꿔줘야하는 항목들 정의
        fl_image = (ImageView) getActivity().findViewById(R.id.fl_image);
        togreen_text = (TextView) getActivity().findViewById(R.id.togreen_text);

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }



    public void changetlinfo(String tlstat) // 블루투스 기기로부터 받아온 정보를 이용하여 화면에 정보를 표시해줌
    {

        try {
            if(getActivity() != null && isAdded()) {
                if (tlstat.contains("1")) { // 정보가 "1" 일땐 빨간불
                    fl_image.setImageDrawable(getResources().getDrawable(R.drawable.trafficlight_sample_red));
                } else if (tlstat.contains("2")) { // 정보가 "2" 일땐 노란불
                    fl_image.setImageDrawable(getResources().getDrawable(R.drawable.trafficlight_sample_yellow));
                } else if (tlstat.contains("0")) { // 정보가 "0" 일땐 초록불
                    fl_image.setImageDrawable(getResources().getDrawable(R.drawable.trafficlight_sample_green));
                }
            }
        }
        catch (NumberFormatException ex) {

        }

    }


    public void startTimer() // 타이머 시작
    {
        countdowntimer = new CountDownTimer(timeleftmilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleftmilliseconds = 1;
                updateTimer();
            }

            @Override
            public void onFinish() {
                stopTimer();
            }
        }.start();

        timeRunning = true;
    }

    public void stopTimer()
    {
        countdowntimer.cancel();
    }

    public void updateTimer()
    {
        int seconds = (int) timeleftmilliseconds / 1000;
        togreen_text.setText(Integer.toString(seconds));
    }

}
