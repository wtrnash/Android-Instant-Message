package com.example.wtr.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wtr.im.R;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.PeopleItem;

/**
 * Created by wtr on 2017/7/1.
 */

public class UserSettingActivity extends Activity implements View.OnClickListener{

    private EditText ageInput;
    private EditText sexInput;
    private EditText areaInput;
    private EditText signInput;
    private TextView submitButton;

    private PeopleItem peopleItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_setting);
        ageInput = (EditText)findViewById(R.id.user_setting_age_input);
        sexInput = (EditText)findViewById(R.id.user_setting_sex_input);
        areaInput = (EditText)findViewById(R.id.user_setting_area_input);
        signInput = (EditText)findViewById(R.id.user_setting_sign_input);
        submitButton = (TextView)findViewById(R.id.user_setting_submit);
        peopleItem = MyApplication.getMyApplication().getUser();
        ageInput.setText(""+peopleItem.getAge());
        sexInput.setText((peopleItem.getSex()=="female")?"女":"男");
        areaInput.setText(peopleItem.getArea());
        signInput.setText(peopleItem.getProduce());
        submitButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_setting_submit:
                peopleItem.setAge(Integer.parseInt(ageInput.getText().toString()));
                peopleItem.setSex((areaInput.getText().toString()=="女")?"female":"male");
                peopleItem.setArea(areaInput.getText().toString());
                peopleItem.setProduce(signInput.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("data_return", "true");
                setResult(RESULT_OK,intent);
                Toast.makeText(UserSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
