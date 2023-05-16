package com.example.banhang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.banhang.R;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Paper.init(this);
        //tạo ra một luồng mới bằng cách mở rộng lớp Thread và ghi đè phương thức run()
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(5000);//tạm dừng luồng trong 5 giây (5000 mili giây).
                }catch (Exception ex) {

                }finally {
                    //Sau khoảng thời gian trễ 5 giây, mã sẽ kiểm tra xem một đối tượng người dùng
                    //đã được lưu trữ trong Paper hay chưa bằng cách gọi phương thức Paper.book().read("user").
                    if (Paper.book().read("user") == null) {
                        //Nếu phương thức trả về giá trị null có nghĩa là chưa có đối tượng người dùng nào
                        //được lưu trữ nên đoạn mã này bắt đầu một hoạt động mới (DangNhapActivity) để nhắc người dùng đăng nhập.
                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        //Nếu phương thức trả về giá trị null có nghĩa là chưa có đối tượng người
                        // dùng nào được lưu trữ nên đoạn mã này bắt đầu một hoạt động mới (DangNhapActivity) để nhắc người dùng đăng nhập.
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        finish();
                    }



                }
            }
        };
        thread.start();
    }
}