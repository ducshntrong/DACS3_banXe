package com.example.banhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banhang.R;
import com.example.banhang.adapter.GioHangAdapter;
import com.example.banhang.model.EventBus.TinhTongEvent;
import com.example.banhang.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

public class GioHangActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnmuahang;
    GioHangAdapter adapter;
    long tongtiensp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        initControl();

        if (Utils.mangmuahang != null) {//kiểm tra danh sách mua hàng của người dùng đã được khởi tạo hay chưa.
            //Nếu danh sách đã được khởi tạo, phương thức sử dụng Utils.mangmuahang.clear() để xóa tất cả các sản phẩm trong danh sách.
            Utils.mangmuahang.clear();
        }
        tinhTongTien();//tính toán tổng giá trị các sản phẩm trong giỏ hàng.
    }

    private void tinhTongTien() {
        tongtiensp = 0;
        for (int i = 0; i< Utils.mangmuahang.size(); i++) {
            tongtiensp = tongtiensp + Utils.mangmuahang.get(i).getGiasp() * Utils.mangmuahang.get(i).getSoluong();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien.setText(decimalFormat.format(tongtiensp)+"đ");
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (Utils.manggiohang.size() == 0) {
            giohangtrong.setVisibility(View.VISIBLE);
        }else {
            adapter = new GioHangAdapter(getApplicationContext(), Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }

        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.manggiohang.size() > 0) {//kiểm tra xem giỏ hàng của người dùng có chứa sản phẩm nào không.
                    Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                    //truyền giá trị của biến tongtiensp qua Activity ThanhToanActivity bằng cách sử dụng putExtra()
                    intent.putExtra("tongtien", tongtiensp);//truyền dữ liệu tongtien qua
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Không có sản phẩm nào để mua hàng", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initView() {
        giohangtrong = findViewById(R.id.txtgiohangtrong);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleviewgiohang);
        btnmuahang = findViewById(R.id.btnmuahang);
    }

    //Phương thức onStart() được gọi khi Activity bắt đầu được hiển thị cho người dùng.
    @Override
    protected void onStart() {
        super.onStart();//thực hiện các hoạt động onStart của lớp cơ sở.
        //trong hàm sẽ đky sự kiện evenbus
        EventBus.getDefault().register(this);//để đăng ký lắng nghe sự kiện từ EventBus.
        //Đối tượng this trong phương thức này là một tham chiếu đến Activity hiện tại,
        // đại diện cho lớp sẽ lắng nghe các sự kiện từ EventBus.
    }

    //Phương thức onStop() được gọi khi Activity không còn được hiển thị cho người dùng.
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);//được sử dụng để hủy đăng ký lắng nghe sự kiện từ EventBus.
        //Đối tượng this trong phương thức này là một tham chiếu đến Activity hiện tại,
        // đại diện cho lớp không còn lắng nghe các sự kiện từ EventBus.
        super.onStop();//thực hiện các hoạt động onStop của lớp cơ sở.
    }

    //Phương thức eventTinhTien() là một phương thức xử lý sự kiện được đăng ký với EventBus để lắng nghe sự kiện TinhTongEvent.
    //Thuộc tính sticky được đặt là true để đảm bảo rằng phương thức này nhận được sự kiện ngay cả khi sự kiện đã được gửi trước đó
    //Thuộc tính threadMode được đặt là ThreadMode.MAIN để đảm bảo rằng phương thức này được gọi
    // trên luồng chính (UI thread) của ứng dụng
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event){
        if (event != null) {//bên kia gửi qua 1 sk và ktr nó khác null thì sẽ tinhtongtien lại
            //Nếu sự kiện khác null, phương thức gọi phương thức tinhTongTien() để tính toán tổng giá trị các sản phẩm trong giỏ hàng.
            tinhTongTien();
        }
    }
}