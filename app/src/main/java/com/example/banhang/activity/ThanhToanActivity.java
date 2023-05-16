package com.example.banhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banhang.R;
import com.example.banhang.model.GioHang;
import com.example.banhang.retrofit.ApiBanHang;
import com.example.banhang.retrofit.RetrofitClient;
import com.example.banhang.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.internal.Util;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txttongtien, txtsodt, txtemail;
    EditText edtdiachi;
    AppCompatButton btndathang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongtien;
    int totalItem;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        countItem();
        initControll();
    }

    private void countItem() {
        totalItem = 0;
        for (int i = 0; i< Utils.mangmuahang.size(); i++) {
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initControll() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien = getIntent().getLongExtra("tongtien", 0);
        txttongtien.setText(decimalFormat.format(tongtien)+"đ");
        //hiển thị thông tin của người dùng như email và số điện thoại.
        // Các thông tin này được lấy từ biến Utils.user_current.
        txtemail.setText(Utils.user_current.getEmail());
        txtsodt.setText(Utils.user_current.getMobile());

        btndathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tongtien == 0){
                    Toast.makeText(getApplicationContext(), "Đơn hàng có giá 0đ!. Vui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
                }else{
                    String str_diachi = edtdiachi.getText().toString().trim();
                    if (TextUtils.isEmpty(str_diachi)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_LONG).show();
                    }else {
                        //Nếu đã nhập địa chỉ, phương thức sẽ tạo một đối tượng User để lưu thông tin của người dùng
                        // và sử dụng phương thức apiBanHang.createOder() để tạo đơn hàng mới.
                        String str_email = Utils.user_current.getEmail();
                        String str_mobile = Utils.user_current.getMobile();
                        int id = Utils.user_current.getId();
                        // post data
                        Log.d("test", new Gson().toJson(Utils.mangmuahang)); // kiem tra
                        compositeDisposable.add(apiBanHang.createOder(str_email, str_mobile, String.valueOf(tongtien), id, str_diachi,
                                        totalItem, new Gson().toJson(Utils.mangmuahang))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        userModel -> {
                                            //Sau khi tạo đơn hàng thành công, phương thức sẽ xoá các sản phẩm trong giỏ hàng
                                            //mà người dùng đã mua và lưu lại trạng thái của giỏ hàng bằng cách sử dụng Paper.
                                            Toast.makeText(getApplicationContext(), "Mua hàng thành công", Toast.LENGTH_LONG).show();
                                            //clear manggiohang bằng cách chạy qua mangmuahang và clear item bị trùng
                                            for (int i=0; i<Utils.mangmuahang.size(); i++){
                                                //chạy qua mangmuahang và tìm xem phần tử trong mangmuahang này có trong
                                                //manggiohang thì đẩy nó ra khỏi manggiohang
                                                //sp napf mua rồi thì bị xoá đi
                                                GioHang gioHang = Utils.mangmuahang.get(i);
                                                if (Utils.manggiohang.contains(gioHang)){
                                                    Utils.manggiohang.remove(gioHang);
                                                }
                                            }
                                            Utils.mangmuahang.clear();
                                            //Lưu lại trạng thái của giỏ hàng
                                            Paper.book().write("giohang", Utils.manggiohang);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        },
                                        throwable -> {
                                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                ));
                    }
                }
            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobar);
        txttongtien = findViewById(R.id.txttongtien);
        txtsodt = findViewById(R.id.txtsodienthoai);
        txtemail = findViewById(R.id.txtemail);
        edtdiachi = findViewById(R.id.edtdiachi);
        btndathang = findViewById(R.id.btndathang);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}