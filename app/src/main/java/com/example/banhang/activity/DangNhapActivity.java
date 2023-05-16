package com.example.banhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banhang.R;
import com.example.banhang.retrofit.ApiBanHang;
import com.example.banhang.retrofit.RetrofitClient;
import com.example.banhang.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.internal.Util;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki, txtresetpass;
    EditText email, pass;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        initView();
        initControl();
    }

    private void initControl() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });
        txtresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
                startActivity(intent);
            }
        });

        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();
                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_LONG).show();
                }else {
                    // luu du account
                    //lưu trữ thông tin đăng nhập của người dùng vào Paper
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);

                    dangNhap(str_email, str_pass);
                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        //khởi tạo đối tượng apiBanHang, đối tượng được sử dụng để gửi yêu cầu API đến máy chủ thông qua Retrofit.
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        txtdangki = findViewById(R.id.txtdangki);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);

        // doc data
        //Paper.book().read() để đọc dữ liệu đã lưu trữ trong Paper
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));

            // luu trang thai tien hanh dang nhap
            if (Paper.book().read("isLogin") != null) {
                boolean flag = Paper.book().read("isLogin");
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //dangNhap(Paper.book().read("email"), Paper.book().read("pass"));
                        }
                    },1000);
                }
            }
        }
        txtresetpass = findViewById(R.id.txtresetpass);
    }

    private void dangNhap(String email, String pass) {
        //Phương thức sử dụng apiBanHang.dangNhap() để gửi yêu cầu đăng nhập đến máy chủ thông qua Retrofit.
        compositeDisposable.add(apiBanHang.dangNhap(email, pass)
                //subscribeOn(Schedulers.io()) để đăng ký việc thực hiện yêu cầu API trên một luồng IO riêng biệt.
                .subscribeOn(Schedulers.io())
                //observeOn(AndroidSchedulers.mainThread()) được sử dụng để đăng ký việc xử lý kết quả
                // trả về trên luồng chính (main thread) của ứng dụng.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                // ghi nho trang thai dang nhap
                                //isLogin là một biến boolean để đại diện cho trạng thái đăng nhập của người dùng
                                // và được lưu trữ bằng cách sử dụng thư viện Paper.
                                isLogin = true;
                                Paper.book().write("isLogin", isLogin);
                                //gán luoon cái user đã đc lấy trên db về vào cho user_current
                                Utils.user_current = userModel.getResult().get(0);
                                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                //luu lai thong tin nguoi dung
                                Paper.book().write("user", userModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Email hoặc mật khẩu không chính xác", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();//super.onResume() để thực hiện các hoạt động onResume của lớp cơ sở.
        if (Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null) {
            //phương thức kiểm tra xem người dùng đã đăng nhập trước đó bằng cách kiểm tra thông
            //tin đăng nhập trong đối tượng Utils.user_current
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}