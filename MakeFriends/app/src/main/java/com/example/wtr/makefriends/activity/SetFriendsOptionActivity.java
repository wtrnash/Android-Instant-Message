package com.example.wtr.makefriends.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.example.wtr.makefriends.util.ToastUtil;
import com.example.wtr.makefriends.util.XMPPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.wtr.makefriends.util.Const.IP;


public class SetFriendsOptionActivity extends Activity {
    private CheckBox sexCheckBox;
    private CheckBox occupationCheckBox;
    private CheckBox ageCheckBox;
    private CheckBox educationCheckBox;
    private CheckBox bloodyTypeCheckBox;
    private CheckBox constellationCheckBox;
    private CheckBox nativePlaceCheckBox;
    private CheckBox locationCheckBox;
    private RadioButton manRadioButton;
    private RadioButton womanRadioButton;
    private EditText occupationEditText;
    private EditText minAgeEditText;
    private EditText maxAgeEditText;
    private Spinner bloodyTypeSpinner;
    private Spinner educationSpinner;
    private Spinner constellationSpinner;
    private EditText nativePlaceEditText;
    private EditText locationEditText;
    private TextView submitButton;
    private Context myContext;
    private boolean isChooseSex;
    private boolean isChooseOccupation;
    private boolean isChooseAge;
    private boolean isChooseBloodyType;
    private boolean isChooseEducation;
    private boolean isChooseConstellation;
    private boolean isChooseNativePlace;
    private boolean isChooseLocation;
    private String sex = "男";
    private String occupation = "";
    private String minAge = "0";
    private String maxAge = "0";
    private String bloodyType = "O";
    private String education = "博士";
    private String constellation = "摩羯座";
    private String nativePlace = "";
    private String location = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_friends_option);
        myContext = this;
        initView();
}

    private void initView(){
        sexCheckBox = (CheckBox)findViewById(R.id.set_friends_sex_check_box);
        occupationCheckBox = (CheckBox)findViewById(R.id.set_friends_occupation_check_box);
        ageCheckBox = (CheckBox)findViewById(R.id.set_friends_age_check_box);
        educationCheckBox = (CheckBox)findViewById(R.id.set_friends_education_check_box);
        bloodyTypeCheckBox = (CheckBox)findViewById(R.id.set_friends_bloody_type_check_box);
        constellationCheckBox = (CheckBox)findViewById(R.id.set_friends_constellation_check_box);
        nativePlaceCheckBox = (CheckBox)findViewById(R.id.set_friends_native_place_check_box);
        locationCheckBox = (CheckBox)findViewById(R.id.set_friends_location_check_box);
        manRadioButton = (RadioButton)findViewById(R.id.set_friends_sex_man);
        womanRadioButton = (RadioButton)findViewById(R.id.set_friends_sex_woman);
        occupationEditText = (EditText)findViewById(R.id.set_friends_occupation);
        minAgeEditText = (EditText)findViewById(R.id.set_friends_min_age);
        maxAgeEditText = (EditText)findViewById(R.id.set_friends_max_age);
        bloodyTypeSpinner = (Spinner)findViewById(R.id.set_friends_bloody_type_spinner);
        bloodyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodyType = getResources().getStringArray(R.array.bloody_type)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        educationSpinner = (Spinner)findViewById(R.id.set_friends_education_spinner);
        educationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                education = getResources().getStringArray(R.array.education)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        constellationSpinner = (Spinner)findViewById(R.id.set_friends_constellation_spinner);
        constellationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                constellation = getResources().getStringArray(R.array.constellation)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nativePlaceEditText = (EditText)findViewById(R.id.set_friends_native_place);
        locationEditText = (EditText)findViewById(R.id.set_friends_location);
        submitButton = (TextView)findViewById(R.id.set_friends_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sexCheckBox.isChecked() && !occupationCheckBox.isChecked() && !ageCheckBox.isChecked()
                        && !educationCheckBox.isChecked() && !bloodyTypeCheckBox.isChecked() && !constellationCheckBox.isChecked()
                        && !nativePlaceCheckBox.isChecked() && !locationCheckBox.isChecked()){
                    ToastUtil.showShortToast(myContext, "请至少选择一个条件");
                    return;
                }

                if(sexCheckBox.isChecked()){
                    isChooseSex = true;
                    if(manRadioButton.isChecked()){
                        sex = "男";
                    }else{
                        sex = "女";
                    }
                }else
                    isChooseSex = false;

                if(occupationCheckBox.isChecked()){
                    if(occupationEditText.getText().toString().isEmpty()){
                        ToastUtil.showShortToast(myContext, "请输入想匹配的朋友的职业");
                        return;
                    }
                    isChooseOccupation = true;
                    occupation = occupationEditText.getText().toString().trim();
                }else
                    isChooseOccupation = false;

                if(ageCheckBox.isChecked()){
                    if(minAgeEditText.getText().toString().isEmpty()){
                        ToastUtil.showShortToast(myContext, "请输入想匹配的朋友的最小年龄");
                        return;
                    }else if(maxAgeEditText.getText().toString().isEmpty()){
                        ToastUtil.showShortToast(myContext, "请输入想匹配的朋友的最大年龄");
                        return;
                    }
                    minAge = minAgeEditText.getText().toString().trim();
                    maxAge = maxAgeEditText.getText().toString().trim();
                    if(!isNumeric(minAge)){
                        ToastUtil.showShortToast(myContext, "最小年龄请输入数字");
                        return;
                    }
                    if(!isNumeric(maxAge)){
                        ToastUtil.showShortToast(myContext, "最大年龄请输入数字");
                        return;
                    }
                    if(Integer.parseInt(minAge) > Integer.parseInt(maxAge)){
                        ToastUtil.showShortToast(myContext, "最小年龄需要小于等于最大年龄");
                        return;
                    }
                    isChooseAge = true;
                }else
                    isChooseAge = false;

                isChooseBloodyType = bloodyTypeCheckBox.isChecked();
                isChooseEducation = educationCheckBox.isChecked();
                isChooseConstellation = constellationCheckBox.isChecked();
                if(nativePlaceCheckBox.isChecked()){
                    if(nativePlaceEditText.getText().toString().isEmpty()){
                        ToastUtil.showShortToast(myContext, "请输入想匹配的朋友的籍贯");
                        return;
                    }
                    isChooseNativePlace = true;
                    nativePlace = nativePlaceEditText.getText().toString().trim();
                }else
                    isChooseNativePlace = false;

                if(locationCheckBox.isChecked()){
                    if(locationEditText.getText().toString().isEmpty()){
                        ToastUtil.showShortToast(myContext, "请输入想匹配的朋友的所在地");
                        return;
                    }
                    isChooseLocation = true;
                    location = locationEditText.getText().toString().trim();
                }else
                    isChooseLocation = false;

                String url="http://" + IP + "/Communicate/MyServlet";
                RequestQueue queues = Volley.newRequestQueue(getApplicationContext());// Volley框架必用，实例化请求队列
                StringRequest request = new StringRequest(Request.Method.POST, url, // StringRequest请求
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String arg0) {// 成功得到响应数据
                                Gson gson = new Gson();
                                List<PeopleItem> peopleItemList = gson.fromJson(arg0, new TypeToken<List<PeopleItem>>(){}.getType());
                                for(int i = peopleItemList.size() - 1; i >= 0; i--) {
                                    //除去已经是好友的
                                   if(XMPPUtil.isFriend(MyApplication.xmppConnection , peopleItemList.get(i).getName())){
                                       peopleItemList.remove(i);
                                       continue;
                                   }
                                   //除去本人
                                   if(peopleItemList.get(i).getName().equals(MyApplication.getUser().getName())){
                                       peopleItemList.remove(i);
                                   }
                                }

                                for(int i = 0; i < peopleItemList.size(); i++){
                                    PeopleItem temp = peopleItemList.get(i);
                                    if(temp.getSex().equals("男")){
                                        temp.setSex("male");
                                        peopleItemList.set(i, temp);
                                    }else{
                                        temp.setSex("female");
                                        peopleItemList.set(i, temp);
                                    }
                                }

                                Intent intent = new Intent(myContext, FindNewFriendsActivity.class);
                                intent.putExtra("list",(Serializable) peopleItemList);
                                intent.putExtra("title", "匹配的潜在好友");
                                startActivity(intent);

                            }
                        }, new Response.ErrorListener() {// 未成功得到响应数据
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        ToastUtil.showShortToast(myContext, "服务器连接失败");
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("order", "setFriends");
                        try {
                            if(isChooseSex)
                                params.put("isChooseSex", "true");
                            else
                                params.put("isChooseSex", "false");
                            if(isChooseOccupation)
                                params.put("isChooseOccupation", "true");
                            else
                                params.put("isChooseOccupation", "false");
                            if(isChooseAge)
                                params.put("isChooseAge", "true");
                            else
                                params.put("isChooseAge", "false");

                            if(isChooseBloodyType)
                                params.put("isChooseBloodyType", "true");
                            else
                                params.put("isChooseBloodyType", "false");

                            if(isChooseEducation)
                                params.put("isChooseEducation", "true");
                            else
                                params.put("isChooseEducation", "false");

                            if(isChooseConstellation)
                                params.put("isChooseConstellation", "true");
                            else
                                params.put("isChooseConstellation", "false");

                            if(isChooseNativePlace)
                                params.put("isChooseNativePlace", "true");
                            else
                                params.put("isChooseNativePlace", "false");

                            if(isChooseLocation)
                                params.put("isChooseLocation", "true");
                            else
                                params.put("isChooseLocation", "false");

                            // 加码，防止传输过程中中文乱码
                            String tempOccupation = URLEncoder.encode(occupation, "utf-8");
                            params.put("occupation", tempOccupation);
                            String tempNativePlace = URLEncoder.encode(nativePlace, "utf-8");
                            params.put("nativePlace", tempNativePlace);
                            String tempLocation = URLEncoder.encode(location, "utf-8");
                            params.put("location", tempLocation);
                            String tempEducation = URLEncoder.encode(education, "utf-8");
                            params.put("education", tempEducation);
                            String tempSex = URLEncoder.encode(sex, "utf-8");
                            params.put("sex", tempSex);
                            String tempBloodyType = URLEncoder.encode(bloodyType, "utf-8");
                            params.put("bloodyType", tempBloodyType);
                            String tempConstellation = URLEncoder.encode(constellation, "utf-8");
                            params.put("constellation", tempConstellation);
                        } catch (UnsupportedEncodingException e2) {
                            e2.printStackTrace();
                        }
                        params.put("minAge", minAge);
                        params.put("maxAge", maxAge);
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

            }
        });
    }

    public static boolean isNumeric(String str){
           for (int i = str.length();--i>=0;){
                    if (!Character.isDigit(str.charAt(i))){
                            return false;
                        }
               }
            return true;
         }
}
