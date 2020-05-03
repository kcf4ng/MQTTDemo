package com.imedtac.mqtttest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Date;

/*
* Android Demo  from : https://my2drhapsody.blogspot.com/2018/07/mqtt-android-studio.html?m=1
*
* Set up MQTT server on CentOS  from : https://gist.github.com/fernandoaleman/fe34e83781f222dfd8533b36a52dddcc
*
* */

public class MainActivity extends AppCompatActivity {
    MqttHelper mqtt;

    public void btn1_click(View view) {
        mqtt = new MqttHelper(this);
        mqtt.startSub();//訂閱
    }

    public void btnPub_click(View view) {
        Date d = new Date();
        mqtt.startPub("HelloWorld"+d.toString());//發佈
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitialUI();
    }

    private void InitialUI() {
        btn1 = findViewById(R.id.btn1);
    }

    Button btn1;


}
