package com.example.wtr.makefriends.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.PreferencesUtil;
import com.example.wtr.makefriends.util.ServiceUtil;
import com.example.wtr.makefriends.util.ToastUtil;
import com.example.wtr.makefriends.util.XMPPConnectionManager;
import com.example.wtr.makefriends.util.XMPPUtil;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.example.wtr.makefriends.util.Const.IP;

public class NewUserActivity extends Activity implements View.OnClickListener{
    private Context myContext;

    private LinearLayout backButton;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText realNameEdit;
    private EditText occupationEdit;
    private EditText nativePlaceEdit;
    private EditText emailEdit;
    private EditText contactWayEdit;
    private EditText locationEdit;
    private TextView newUserButton;
    private RadioGroup sexRadioGroup;
    private RadioButton manRadioButton;
    private RadioButton womanRadioButton;
    private Spinner ageSpinner;
    private Spinner bloodyTypeSpinner;
    private Spinner educationSpinner;
    private Spinner constellationSpinner;

    private String name;
    private String pwd;
    private String realName;
    private String occupation;
    private String nativePlace;
    private String email;
    private String contactWay;
    private String location;
    private String age = "0";
    private String bloodyType = "o";
    private String education = "博士";
    private String constellation = "摩羯座";
    private String sex = "男";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_user);
        myContext = this;
        findView();
        init();
    }

    private void findView(){
        backButton = (LinearLayout)findViewById(R.id.new_user_go_back);
        userNameEdit = (EditText)findViewById(R.id.new_user_name);
        passwordEdit = (EditText)findViewById(R.id.new_user_password);
        realNameEdit = (EditText)findViewById(R.id.new_user_real_name);
        occupationEdit = (EditText)findViewById(R.id.new_user_occupation);
        nativePlaceEdit = (EditText)findViewById(R.id.new_user_native_place);
        emailEdit = (EditText)findViewById(R.id.new_user_e_mail);
        contactWayEdit = (EditText)findViewById(R.id.new_user_contact_way);
        locationEdit = (EditText)findViewById(R.id.new_user_location);
        newUserButton = (TextView)findViewById(R.id.new_user_button2);
        sexRadioGroup = (RadioGroup)findViewById(R.id.new_user_sex);
        manRadioButton = (RadioButton)findViewById(R.id.new_user_sex_man);
        womanRadioButton = (RadioButton)findViewById(R.id.new_user_sex_woman);
        ageSpinner = (Spinner)findViewById(R.id.new_user_age_spinner);
        bloodyTypeSpinner = (Spinner)findViewById(R.id.new_user_bloody_type_spinner);
        educationSpinner = (Spinner)findViewById(R.id.new_user_education_spinner);
        constellationSpinner = (Spinner)findViewById(R.id.new_user_constellation_spinner);
    }

    private  void  init(){
        backButton.setOnClickListener(this);
        newUserButton.setOnClickListener(this);
        manRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sex = "男";
            }
        });
        womanRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sex = "女";
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

        bloodyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodyType = getResources().getStringArray(R.array.bloody_type)[position];
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

        constellationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                constellation = getResources().getStringArray(R.array.constellation)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    ToastUtil.showLongToast(myContext, "注册失败");
                    break;
                case 1:
                    //注册信息提交后台录入数据库
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
                                            ToastUtil.showLongToast(myContext, "注册成功");
                                            PreferencesUtil.putSharedPre(myContext,"username",name);
                                            PreferencesUtil.putSharedPre(myContext, "password", pwd);
                                            //启动服务进行登录
                                            if(ServiceUtil.isServiceRunning(myContext,XMPPService.class.getName())){
                                                Intent stopIntent = new Intent(myContext, XMPPService.class);
                                                stopService(stopIntent);
                                            }
                                            Intent intent = new Intent(myContext, XMPPService.class);
                                            startService(intent);
                                            finish();
                                        }
                                        else{
                                            ToastUtil.showLongToast(myContext, "注册失败");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {// 未成功得到响应数据
                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            ToastUtil.showLongToast(myContext, "注册失败");
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("order", "newUser");
                            try {
                                // 加码，防止传输过程中中文乱码
                                String tempName = URLEncoder.encode(name, "utf-8");
                                params.put("username",tempName);
                                String tempPwd = URLEncoder.encode(pwd, "utf-8");
                                params.put("password", tempPwd);
                                String tempRealName = URLEncoder.encode(realName, "utf-8");
                                params.put("realName", tempRealName);
                                String tempOccupation = URLEncoder.encode(occupation, "utf-8");
                                params.put("occupation", tempOccupation);
                                String tempNativePlace = URLEncoder.encode(nativePlace, "utf-8");
                                params.put("nativePlace", tempNativePlace);
                                String tempEmail = URLEncoder.encode(email, "utf-8");
                                params.put("email", tempEmail);
                                String tempContactWay = URLEncoder.encode(contactWay, "utf-8");
                                params.put("contactWay", tempContactWay);
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
                case 2:
                    ToastUtil.showLongToast(myContext, "该昵称已被注册");
                    break;
                case 3:
                    ToastUtil.showLongToast(myContext, "注册失败");
                    break;
                case 4:
                    ToastUtil.showLongToast(myContext, "注册失败,请检查您的网络");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.new_user_go_back:
                finish();
                break;
            case R.id.new_user_button2:
                newUser();
                break;
        }
    }

    private void newUser(){
        name = userNameEdit.getText().toString().trim();
        pwd = passwordEdit.getText().toString().trim();
        realName = realNameEdit.getText().toString().trim();
        occupation = occupationEdit.getText().toString().trim();
        nativePlace = nativePlaceEdit.getText().toString().trim();
        email = emailEdit.getText().toString().trim();
        contactWay = contactWayEdit.getText().toString().trim();
        location = locationEdit.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            ToastUtil.showLongToast(myContext, "用户名为空");
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            ToastUtil.showLongToast(myContext, "密码为空");
            return;
        }
        if(TextUtils.isEmpty(nativePlace)){
            ToastUtil.showLongToast(myContext, "籍贯为空");
            return;
        }
        if(TextUtils.isEmpty(realName)){
            ToastUtil.showLongToast(myContext, "真实姓名为空");
            return;
        }
        if(TextUtils.isEmpty(occupation)){
            ToastUtil.showLongToast(myContext, "职业为空");
            return;
        }
        if(TextUtils.isEmpty(email)){
            ToastUtil.showLongToast(myContext, "邮箱为空");
            return;
        }
        if(TextUtils.isEmpty(contactWay)){
            ToastUtil.showLongToast(myContext, "联系方式为空");
            return;
        }
        if(TextUtils.isEmpty(location)){
            ToastUtil.showLongToast(myContext, "所在地为空");
            return;
        }

        for(int i = 0; i < contactWay.length(); i++)
        {
            if(!Character.isDigit(contactWay.charAt(i))){
                ToastUtil.showLongToast(myContext, "联系方式含有非数字字符");
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPConnection xmppConnection = XMPPConnectionManager.init();
                try {
                    xmppConnection.connect();
                    int result = XMPPUtil.register(xmppConnection, name, pwd);  //注册
                    handler.sendEmptyMessage(result);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(4);
                }
            }
        }).start();
    }
}
