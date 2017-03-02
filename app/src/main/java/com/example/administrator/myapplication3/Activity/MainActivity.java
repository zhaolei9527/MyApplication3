package com.example.administrator.myapplication3.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myapplication3.R;
import com.example.administrator.myapplication3.View.MyView;
import com.example.administrator.myapplication3.config.Constant;
import com.example.administrator.myapplication3.pojo.drawxy;
import com.example.administrator.myapplication3.service.StepDcretor;
import com.example.administrator.myapplication3.service.StepService;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Handler.Callback,
        View.OnClickListener, SensorEventListener {
    private long TIME_INTERVAL = 500;
    private TextView text_step;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    private static int STEPS = 16;
    private Button btn_clear;
    private ImageView img_1;
    private TextView tv_1;
    private LinearLayout ll_1;
    private float currentDegree = 0f;
    private MyView my_view;
    private List<drawxy> list;
    private int width;
    private int height;
    private float degree;
    private float startDegree = 0;
    private TextView tv_2;
    private float o1;
    private double y0;
    private double y1;
    private double y2;
    private float o0;
    private float o2;
    private double x0;
    private double x1;
    private double x2;
    private double z0;
    private double z1;
    private double z2;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SensorManager sm;
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                // TODO: 2017/2/17
                chagePath(msg);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }
    private static boolean isreverse = false;
    private void chagePath(Message msg) {
        if (!text_step.getText().equals(msg.getData().getInt("step") + "")) {
            drawxy drawxy1 = list.get(list.size() - 1);
            if (text_step.getText().equals("0")) {
                Log.i("日志输出", "初始化角度");
                drawxy drawxy = new drawxy();
                drawxy.setX((int) drawxy1.getX());
                drawxy.setY((int) (drawxy1.getY() - STEPS));
                list.add(drawxy);
                startDegree = degree;
                my_view.requestLayout();
                Log.i("日志输出", "角度为：" + -(startDegree - degree) + "::startDegree=" +
                        startDegree + "degree=" + degree);
            } else {
                Log.i("日志输出", "角度为：" + -(startDegree - degree) + "::startDegree=" +
                        startDegree + "degree=" + degree);
                // TODO: 2017/2/18 前行抖动过滤
                if (Math.abs(degree - startDegree) > 100) {
                    //  Log.i("日志输出", "+++反向前行");
                    isreverse = true;
                }
                if ((degree - startDegree) < 10 || (degree - startDegree) > 350) {
                    //  Log.i("日志输出", "+++正向前行");
                    isreverse = false;
                }
                if (isreverse) {

                    if (Math.abs(degree - startDegree) > 170) {

                        if (Math.abs(degree - startDegree) < 210) {
                            Log.i("日志输出", "反向直行");
                            drawxy drawxy = new drawxy();
                            drawxy.setX((int) drawxy1.getX());
                            drawxy.setY((int) (drawxy1.getY() + STEPS));
                            list.add(drawxy);
                            my_view.requestLayout();
                            text_step.setText(msg.getData().getInt("step") + "");
                            return;
                        }
                    }

                } else {

                    if (Math.abs(degree - startDegree) < 10 || (degree - startDegree) > 350) {
                        Log.i("日志输出", "正向直行" + (degree - startDegree));
                        drawxy drawxy = new drawxy();
                        drawxy.setX((int) drawxy1.getX());
                        drawxy.setY((int) (drawxy1.getY() - STEPS));
                        list.add(drawxy);
                        my_view.requestLayout();
                        text_step.setText(msg.getData().getInt("step") + "");
                        return;
                    }
                }

                if (Math.abs((degree - startDegree)) > 80 && Math.abs(degree - startDegree) < 100) {
                    //判断手机左转还是右转
                    //判断startDegree的初始值，如果小于270，那么最大不超过360
                    //如果大于270，小于90，那么为右转。

                    if (startDegree > 270 && degree - startDegree < 90) {
                        // TODO: 2017/2/18 右转
                        Log.i("日志输出", "右方向前进");
                        drawxy drawxy = new drawxy();
                        drawxy.setX((int) (drawxy1.getX() + STEPS));
                        drawxy.setY((int) drawxy1.getY());
                        list.add(drawxy);
                        my_view.requestLayout();
                        text_step.setText(msg.getData().getInt("step") + "");
                        return;
                    }

                    if (startDegree < 90 && degree - startDegree > 270) {
                        // TODO: 2017/2/18 左转
                        Log.i("日志输出", "左方向前进");
                        drawxy drawxy = new drawxy();
                        drawxy.setX((int) (drawxy1.getX() - STEPS));
                        drawxy.setY((int) drawxy1.getY());
                        list.add(drawxy);
                        my_view.requestLayout();
                        text_step.setText(msg.getData().getInt("step") + "");
                        return;
                    }

                    if (startDegree < degree) {
                        // TODO: 2017/2/18 右转
                        Log.i("日志输出", "右方向前进");
                        drawxy drawxy = new drawxy();
                        drawxy.setX((int) (drawxy1.getX() + STEPS));
                        drawxy.setY((int) drawxy1.getY());
                        list.add(drawxy);
                        my_view.requestLayout();
                        text_step.setText(msg.getData().getInt("step") + "");
                        return;
                    }

                    if (startDegree > degree) {
                        // TODO: 2017/2/18 左转
                        Log.i("日志输出", "左方向前进");
                        Log.i("日志输出", "左方向前进" + (degree - startDegree));
                        drawxy drawxy = new drawxy();
                        drawxy.setX((int) (drawxy1.getX() - STEPS));
                        drawxy.setY((int) drawxy1.getY());
                        list.add(drawxy);
                        my_view.requestLayout();
                        text_step.setText(msg.getData().getInt("step") + "");
                        return;
                    }


                }
                double x = 0;
                double y = 0;
                if (degree > startDegree && degree < startDegree + 90) {
                    // TODO: 2017/2/22  第一区间 x- y-
                    Log.i("日志输出", "第一区间");
                    x = (STEPS * Math.sin(Math.abs(degree - startDegree)));
                    y = (STEPS * Math.cos(Math.abs(degree - startDegree)));
                    drawxy drawxy = new drawxy();
                    com.example.administrator.myapplication3.pojo.drawxy drawxy2 =
                            list.get(list.size() - 1);
                    drawxy.setX((int) (drawxy2.getX() + Math.abs(x)));
                    drawxy.setY((int) (drawxy2.getY() - Math.abs(y)));
                    list.add(drawxy);
                    Log.i("日志输出", "第一区间:x:" + x + "y:" + y);
                    Log.i("日志输出", "第一区间drawxy2:" + drawxy2.toString());
                    Log.i("日志输出", "第一区间drawxy:" + drawxy.toString());
                    my_view.requestLayout();
                }

                if (degree > startDegree + 90 && degree < startDegree + 180) {
                    // TODO: 2017/2/22  第二区间 y- x+
                    Log.i("日志输出", "第二区间");
                    x = (STEPS * Math.sin(Math.abs(degree - startDegree)));
                    y = (STEPS * Math.cos(Math.abs(degree - startDegree)));
                    drawxy drawxy = new drawxy();
                    com.example.administrator.myapplication3.pojo.drawxy drawxy2 =
                            list.get(list.size() - 1);
                    drawxy.setX((int) (drawxy2.getX() + Math.abs(x)));
                    drawxy.setY((int) (drawxy2.getY() + Math.abs(y)));
                    list.add(drawxy);
                    my_view.requestLayout();
                }
                if (degree > startDegree + 180 && degree < startDegree + 270) {
                    // TODO: 2017/2/22  第三区间 y+ x+
                    Log.i("日志输出", "第三区间");
                    x = (STEPS * Math.sin(Math.abs(degree - startDegree)));
                    y = (STEPS * Math.cos(Math.abs(degree - startDegree)));
                    drawxy drawxy = new drawxy();
                    com.example.administrator.myapplication3.pojo.drawxy drawxy2 =
                            list.get(list.size() - 1);
                    drawxy.setX((int) (drawxy2.getX() - Math.abs(x)));
                    drawxy.setY((int) (drawxy2.getY() + Math.abs(y)));
                    list.add(drawxy);
                    my_view.requestLayout();
                }
                int oldDegree = (int) startDegree;
                //起始角度小于九十度，那么偏转后，角度可能为负值。
                if (startDegree < 90) {
                    if (startDegree - 90 < 0) {
                        oldDegree = (int) Math.abs((360 - (startDegree - 90)));
                    }
                }
                if (degree > oldDegree || degree < startDegree) {
                    // TODO: 2017/2/22  第四区间 y+ x-
                    Log.i("日志输出", "第四区间");
                    x = (STEPS * Math.sin(Math.abs(degree - startDegree)));
                    y = (STEPS * Math.cos(Math.abs(degree - startDegree)));
                    drawxy drawxy = new drawxy();
                    com.example.administrator.myapplication3.pojo.drawxy drawxy2 =
                            list.get(list.size() - 1);
                    drawxy.setX((int) (drawxy2.getX() - Math.abs(x)));
                    drawxy.setY((int) (drawxy2.getY() - Math.abs(y)));
                    list.add(drawxy);
                    my_view.requestLayout();
                }
            }
            text_step.setText(msg.getData().getInt("step") + "");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        text_step = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        img_1 = (ImageView) findViewById(R.id.img_1);
        my_view = (MyView) findViewById(R.id.My_view);
        list = my_view.getList();
        list.clear();
        drawxy drawxy = new drawxy();
        drawxy.setX(width / 2);
        drawxy.setY(height / 2);
        list.add(drawxy);
        my_view.requestLayout();
        delayHandler = new Handler(this);
        btn_clear.setOnClickListener(this);
        // 传感器管理器
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        setupService();
    }
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 注册传感器(Sensor.TYPE_ORIENTATION(方向传感器);SENSOR_DELAY_FASTEST(0毫秒延迟);
        //  注册方向传感器
        // SENSOR_DELAY_GAME(20,000毫秒延迟)、SENSOR_DELAY_UI(60,000毫秒延迟))
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);

        //  注册加速度传感器
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                StepDcretor.CURRENT_SETP = 0;
                text_step.setText("0");
                list.clear();
                startDegree = 0;
                drawxy drawxy = new drawxy();
                drawxy.setX(width / 2);
                drawxy.setY(height / 2);
                list.add(drawxy);
                my_view.requestLayout();
                break;
        }
    }
    public static final float STANDARD_GRAVITY = 9.80665F;
    @Override
    public void onSensorChanged(SensorEvent event) {
        //方向传感器变化
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            degree = event.values[0];
            o1 = event.values[1];
            o0 = event.values[0];
            o2 = event.values[2];
            /*
            RotateAnimation类：旋转变化动画类
            参数说明:
            fromDegrees：旋转的开始角度。
            toDegrees：旋转的结束角度。
            pivotXType：X轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
            pivotXValue：X坐标的伸缩值。
            pivotYType：Y轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
            pivotYValue：Y坐标的伸缩值
            values[0]的取值范围是-180°到180°，其中±180°表示正南方向，0°表示正北方向，-90°表示正西方向，90°表示正东方向。
            */
            RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            //旋转过程持续时间
            ra.setDuration(200);
            //罗盘图片使用旋转动画
            img_1.startAnimation(ra);
            currentDegree = -degree;
            if (degree > 180) {
                DecimalFormat df = new DecimalFormat(".#");
                double d = 360 - degree;
                String st = df.format(d);
                if (!st.equals(tv_2.getText())) {
                    tv_2.setText("偏北" + st);
                }
            } else {
                if (!String.valueOf(degree).equals(tv_2.getText())) {
                    tv_2.setText("偏北" + degree);
                }
            }
            //Log.i("日志输出", "角度为：" + degree);
            //首先求出y轴的单位向量在地面参照系中的三个方向分量
            y0 = (-Math.sin(o1));
            y1 = Math.cos(o1) * Math.cos(degree);
            y2 = Math.cos(o1) * Math.sin(degree);
            double temp = Math.acos(-(Math.tan(o1) * Math.tan(o2)));
            x0 = (-Math.sin(o2));
            x1 = Math.cos(o2) * Math.cos(o0 + temp);
            x2 = Math.cos(o2) * Math.sin(o0 + temp);
            z0 = x2 * y1 - x1 * y2;
            z1 = x0 * y2 - x2 * y0;
            z2 = x1 * y0 - x0 * y1;
        }
        //加速度传感器变化
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            String accelerometer = "加速度\n" + "X：" + event.values[0] + "\n"
                    + "Y:" + event.values[1] + "\n" + "Z:" + event.values[2] + "\n";

            //     Log.i("日志输出", "加速度为：" + "Y:" + event.values[1]);
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            double a0 = ax * x0 + ay * y0 + az * z0 + SensorManager.STANDARD_GRAVITY;//(这里加上标准重力加速度以抵消默认的重力加速度)

            double a1 = ax * x1 + ay * y1 + az * z1;

            double a2 = ax * x2 + ay * y2 + az * z2;

            //Log.i("日志输出", "加速度为：" + "相对绝对速度\n" + "X：" + a0 + "\n"
            //       + "Y:" + a1 + "\n" + "Z:" + a2 + "\n");

            if (ax > STANDARD_GRAVITY) {
                // Log.i("日志输出", "重力指向设备左边");

            } else if (ax < -STANDARD_GRAVITY) {
                // Log.i("日志输出", "重力指向设备右边");

            } else if (ay > STANDARD_GRAVITY) {
                // Log.i("日志输出", "重力指向设备下边");

            } else if (ay < -STANDARD_GRAVITY) {
                // Log.i("日志输出", "重力指向设备上边");

            } else if (az > STANDARD_GRAVITY) {
                // Log.i("日志输出", "屏幕朝上");
                //屏幕朝上，静止状态z轴受到相对恒等重力加速度。
                //运动状态下考虑z轴方向上波动的加速度，作为动力标准，
                //平衡状态下，可考虑对平稳波长进行检测，以想x，y的值做观察。
                //判断是否为运动状态


            } else if (az < -STANDARD_GRAVITY) {
                // Log.i("日志输出", "屏幕朝下");

            }
        }
    }

    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
