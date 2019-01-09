package com.example.wtr.makefriends.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.application.MyApplication;
import com.example.wtr.makefriends.bean.PeopleItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.example.wtr.makefriends.util.Const.IP;


public class UserSettingActivity extends Activity implements View.OnClickListener{

    private EditText realNameInput;
    private EditText occupationInput;
    private Spinner ageSpinner;
    private Spinner bloodyTypeSpinner;
    private Spinner educationSpinner;
    private Spinner constellationSpinner;
    private RadioGroup sexRadioGroup;
    private RadioButton manRadioButton;
    private RadioButton womanRadioButton;
    private EditText nativePlaceInput;
    private EditText emailInput;
    private EditText locationInput;
    private EditText signInput;
    private EditText contactWayInput;
    private TextView submitButton;

    private PeopleItem peopleItem;

    private String age;
    private String bloodyType;
    private String education;
    private String constellation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_setting);
        realNameInput = (EditText)findViewById(R.id.user_setting_real_name_input);
        occupationInput = (EditText)findViewById(R.id.user_setting_occupation_input);
        ageSpinner = (Spinner) findViewById(R.id.user_setting_age_spinner);
        bloodyTypeSpinner = (Spinner)findViewById(R.id.user_setting_bloody_type_spinner);
        educationSpinner = (Spinner)findViewById(R.id.user_setting_education_spinner);
        constellationSpinner = (Spinner)findViewById(R.id.user_setting_constellation_spinner);
        sexRadioGroup = (RadioGroup)findViewById(R.id.user_setting_sex);
        manRadioButton = (RadioButton)findViewById(R.id.user_setting_sex_man);
        womanRadioButton = (RadioButton)findViewById(R.id.user_setting_sex_woman);
        nativePlaceInput = (EditText)findViewById(R.id.user_setting_native_place_input);
        emailInput = (EditText)findViewById(R.id.user_setting_email_input);
        locationInput = (EditText)findViewById(R.id.user_setting_location_input);
        contactWayInput = (EditText)findViewById(R.id.user_setting_contact_way_input);
        signInput = (EditText)findViewById(R.id.user_setting_sign_input);
        submitButton = (TextView)findViewById(R.id.user_setting_submit);
        peopleItem = MyApplication.getMyApplication().getUser();
        constellationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                constellation = getResources().getStringArray(R.array.constellation)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        educationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                education = getResources().getStringArray(R.array.education)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bloodyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodyType = getResources().getStringArray(R.array.bloody_type)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = getResources().getStringArray(R.array.age)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setSpinnerItemSelectedByValue(ageSpinner, String.valueOf(peopleItem.getAge()));
        setSpinnerItemSelectedByValue(bloodyTypeSpinner, peopleItem.getBloodyType());
        setSpinnerItemSelectedByValue(educationSpinner, peopleItem.getEducation());
        setSpinnerItemSelectedByValue(constellationSpinner, peopleItem.getConstellation());

        if(peopleItem.getSex().equals("female")){
            sexRadioGroup.check(R.id.user_setting_sex_woman);
        }
        else{
            sexRadioGroup.check(R.id.user_setting_sex_man);
        }
        realNameInput.setText(peopleItem.getRealName());
        occupationInput.setText(peopleItem.getOccupation());
        locationInput.setText(peopleItem.getLocation());
        nativePlaceInput.setText(peopleItem.getNativePlace());
        emailInput.setText(peopleItem.getEmail());
        contactWayInput.setText(peopleItem.getContactWay());
        signInput.setText(peopleItem.getIntroduce());
        submitButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_setting_submit:
                String url="http://" + IP + "/Communicate/MyServlet";
                RequestQueue queues = Volley.newRequestQueue(getApplicationContext());// Volley框架必用，实例化请求队列
                StringRequest request = new StringRequest(Request.Method.POST, url, // StringRequest请求
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String arg0) {// 成功得到响应数据
                                try {
                                    JSONObject result_jo = new JSONObject(arg0);
                                    Boolean result = result_jo.getBoolean("result");
                                    if(result){
                                        peopleItem.setRealName(realNameInput.getText().toString());
                                        peopleItem.setOccupation(occupationInput.getText().toString());
                                        peopleItem.setAge(Integer.parseInt(age));
                                        peopleItem.setBloodyType(bloodyType);
                                        peopleItem.setEducation(education);
                                        peopleItem.setConstellation(constellation);
                                        peopleItem.setEmail(emailInput.getText().toString());
                                        if(womanRadioButton.isChecked())
                                            peopleItem.setSex("female");
                                        else if(manRadioButton.isChecked())
                                            peopleItem.setSex("male");
                                        peopleItem.setNativePlace(nativePlaceInput.getText().toString());
                                        peopleItem.setContactWay(contactWayInput.getText().toString());
                                        peopleItem.setLocation(locationInput.getText().toString());
                                        peopleItem.setIntroduce(signInput.getText().toString());

                                        Intent intent = new Intent();
                                        intent.putExtra("data_return", "true");
                                        setResult(RESULT_OK,intent);
                                        Toast.makeText(UserSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(UserSettingActivity.this, "服务器连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(UserSettingActivity.this, "服务器连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {// 未成功得到响应数据
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(UserSettingActivity.this, "服务器连接失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("order", "userSetting");
                        try {
                            // 加码，防止传输过程中中文乱码
                            String tempName = URLEncoder.encode(MyApplication.getUser().getName(), "utf-8");
                            params.put("username",tempName);
                            String tempRealName = URLEncoder.encode(realNameInput.getText().toString(), "utf-8");
                            params.put("realName", tempRealName);
                            String tempOccupation = URLEncoder.encode(occupationInput.getText().toString(), "utf-8");
                            params.put("occupation", tempOccupation);
                            String tempNativePlace = URLEncoder.encode(nativePlaceInput.getText().toString(), "utf-8");
                            params.put("nativePlace", tempNativePlace);
                            String tempEmail = URLEncoder.encode(emailInput.getText().toString(), "utf-8");
                            params.put("email", tempEmail);
                            String tempContactWay = URLEncoder.encode(contactWayInput.getText().toString(), "utf-8");
                            params.put("contactWay", tempContactWay);
                            String tempLocation = URLEncoder.encode(locationInput.getText().toString(), "utf-8");
                            params.put("location", tempLocation);
                            String tempEducation = URLEncoder.encode(education, "utf-8");
                            params.put("education", tempEducation);
                            String sex = "";
                            if(womanRadioButton.isChecked())
                                sex = URLEncoder.encode("女", "utf-8");
                            else if(manRadioButton.isChecked())
                                sex = URLEncoder.encode("男", "utf-8");
                            params.put("sex", sex);
                            String tempBloodyType = URLEncoder.encode(bloodyType, "utf-8");
                            params.put("bloodyType", tempBloodyType);
                            String tempConstellation = URLEncoder.encode(constellation, "utf-8");
                            params.put("constellation", tempConstellation);
                            String sign = URLEncoder.encode(signInput.getText().toString(), "utf-8");
                            params.put("sign", sign);
                        } catch (UnsupportedEncodingException e2) {
                            e2.printStackTrace();
                        }
                        params.put("age", age);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("enctype", "multipart/form-data");
                        return headers;
                    }
                };
                request.setTag("volleyGet");// 设置请求标签Tag
                queues.add(request);// 将请求加入队列queue中处理

                break;
        }
    }
    public static void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i,true);// 默认选中项
                break;
            }
        }
    }
}
