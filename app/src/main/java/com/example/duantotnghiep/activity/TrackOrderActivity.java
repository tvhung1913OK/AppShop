package com.example.duantotnghiep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baoyachi.stepview.VerticalStepView;
import com.example.duantotnghiep.R;

import java.util.ArrayList;
import java.util.List;

public class TrackOrderActivity extends AppCompatActivity {
VerticalStepView stepView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        stepView = findViewById(R.id.step_view);
        setStepView();

    }

    private void setStepView() {
        stepView.setStepsViewIndicatorComplectingPosition(getList().size())
                .reverseDraw(false)
                .setStepViewTexts(getList())

                .setLinePaddingProportion(.1f)
                .setStepsViewIndicatorCompletedLineColor(getColor(R.color.placeorder))
                .setStepsViewIndicatorUnCompletedLineColor(R.color.black)
                .setLinePaddingProportion(1)
                .setStepViewComplectedTextColor(getColor(R.color.black))
                .setStepViewUnComplectedTextColor(R.color.gray)
                .setStepsViewIndicatorCompleteIcon(getDrawable(R.drawable.checked))
                .setStepsViewIndicatorAttentionIcon(getDrawable(R.drawable.warning))


                .setStepsViewIndicatorDefaultIcon(getDrawable(R.drawable.remove));

        stepView.setStepsViewIndicatorComplectingPosition(2);



    }
    private List<String> getList(){
        List<String> list = new ArrayList<>();
        list.add("Order Place");
        list.add("In Progress");
        list.add("Shipped");
        list.add("Delivered");

        return list;
    }

}