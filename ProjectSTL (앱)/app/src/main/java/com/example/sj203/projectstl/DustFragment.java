package com.example.sj203.projectstl;


import android.os.Bundle;
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
public class DustFragment extends Fragment {


    public DustFragment() {
        // Required empty public constructor
    }

    // 정보 표시에 필요한 항목들 선언
    ImageView dust_image;
    TextView dust_stat;
    TextView dust_num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dust, container, false);

        // 정보 표시에 필요한 항목들 정의
        dust_image = (ImageView) getActivity().findViewById(R.id.dust_image);
        dust_stat = (TextView) getActivity().findViewById(R.id.dust_stat);
        dust_num = (TextView) getActivity().findViewById(R.id.dust_num);

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }


    public void changedustinfo (String duststat) // 블루투스에서 받아온 정보에 따라 항목들의 값을 바꿔줌
    {

        try {
            int dustint = Integer.parseInt(duststat);

            if (dustint > 3 && dustint <= 30) { // 미세먼지 수치가 30 미만이면 좋음
                dust_image.setImageDrawable(getResources().getDrawable(R.drawable.face_green));
                dust_stat.setText("좋음");
                dust_num.setText(duststat);
            } else if (dustint > 30 && dustint <= 80) { // 미세먼지 수치가 30 이상 80 미만이면 보통
                dust_image.setImageDrawable(getResources().getDrawable(R.drawable.face_green));
                dust_stat.setText("보통");
                dust_num.setText(duststat);
            } else if (dustint > 80) { // 미세먼지 수치가 80 이상이면 나쁨
                dust_image.setImageDrawable(getResources().getDrawable(R.drawable.face_green));
                dust_stat.setText("나쁨");
                dust_num.setText(duststat);
            }

        }
        catch (NumberFormatException ex) {

        }

    }
}
