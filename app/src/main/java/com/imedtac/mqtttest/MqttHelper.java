package com.imedtac.mqtttest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MqttHelper {
    private static final String TAG = "MqttHelper";
    public static MqttClient client;
    public static String myTopic="HelloWorld_Topic";
    public static MqttConnectOptions options;
    public static String mqttHost = "tcp://192.168.0.62:1883"; //改為自己的MQTT SERVER IP
    public Context context;


    //Constructor
    public MqttHelper(Context context) {
        this.context = context;
        try {
            Date d = new Date();
            Long clientId = d.getTime();
            client = new MqttClient(mqttHost,"LinkU"+clientId,new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName("mqtt-test");
            options.setPassword("mqtt-test".toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    // 這裡可以寫重連程式
                    Log.d(TAG, "連線已中斷");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    // subscribe后得到的訊息如下
                    Log.d(TAG, "主题 : " + s+" Qos : " + mqttMessage.getQos()+" 内容 : " + new String(mqttMessage.getPayload()));
                    showNotification(s,new String(mqttMessage.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("發送完成 --" + iMqttDeliveryToken.isComplete());
                    Log.d(TAG, "發送完成 --" + iMqttDeliveryToken.isComplete());
                }
            });
            client.connect(options);//執行連線!
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "reason "+e.getReasonCode());
            Log.d(TAG, "msg "+e.getMessage());
            Log.d(TAG, "loc "+e.getLocalizedMessage());
            Log.d(TAG, "cause "+e.getCause());
            Log.d(TAG, "excep "+e);
        }
    }

    //訂閱
    public static void startSub(){
        try {
            int[] Qos = {1};
            String[] topic1 = {myTopic};
            client.subscribe(topic1, Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    //發佈
    public static void startPub(String m){
        try {
            long long_id = new Date().getTime();
            int i = (int)(long_id%100000);
            MqttTopic topic = client.getTopic(myTopic);
            MqttMessage message = new MqttMessage(m.getBytes());
            message.setId(i);
            message.setQos(2);
            message.setRetained(true);
            client.publish(myTopic, message);
            Log.d(TAG, "startPub: message Id :"+i);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //狀態列通知
    private void showNotification(String topic, String msg){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chanel = new NotificationChannel("myId", "chjanel Name", NotificationManager.IMPORTANCE_HIGH);

            Notification notification = new Notification.Builder(context, "myId")
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setTicker("Ticker: " + msg)
                    .setContentTitle("ContentTitle: " + topic)
                    .setContentText("ContentText: " + msg)
                    .setChannelId("myId")
                    .build();

            NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(chanel);
            nm.notify(0, notification);
            }
        }

}
