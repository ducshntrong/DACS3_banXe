package com.example.banhang.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.bumptech.glide.Glide;

import com.example.banhang.R;
import com.example.banhang.adapter.LoaiSpAdapter;
import com.example.banhang.adapter.SanPhamMoiAdapter;
import com.example.banhang.model.LoaiSp;
import com.example.banhang.model.SanPhamMoi;
import com.example.banhang.model.User;
import com.example.banhang.retrofit.ApiBanHang;
import com.example.banhang.retrofit.RetrofitClient;
import com.example.banhang.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewmanhinhchinh;
    NavigationView navigationView;
    ListView listViewmanhinhchinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    ImageView imgsearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);//kết nối tới url của db
        //Phương thức Paper.init(this) được sử dụng để khởi tạo thư viện Paper, một thư viện lưu trữ dữ liệu đơn giản.
        Paper.init(this);
        //kiểm tra xem có thông tin người dùng được lưu trữ trong Paper hay không.
        if (Paper.book().read("user") != null) {
            //Nếu có, phương thức sử dụng Paper.book().read("user") để đọc thông tin người dùng và gán nó cho biến Utils.user_current.
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }

        Anhxa();
        ActionBar();
        //Banner();

        if (isConnected(this)) {
            //Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "khong co internet, vui long thu lai", Toast.LENGTH_LONG).show();
        }

    }

    private void getEventClick() {
        listViewmanhinhchinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent xemaydien = new Intent(getApplicationContext(), loaiSanPhamActivity.class);
                        xemaydien.putExtra("loai", 1);
                        startActivity(xemaydien);
                        break;
                    case 2:
                        Intent xedapdien = new Intent(getApplicationContext(), loaiSanPhamActivity.class);
                        xedapdien.putExtra("loai", 2);
                        startActivity(xedapdien);
                        break;
                    case 3:
                        Intent xedap = new Intent(getApplicationContext(), xeDapActivity.class);
                        xedap.putExtra("loai", 3);
                        startActivity(xedap);
                        break;
                    case 4:
                        Intent lienhe = new Intent(getApplicationContext(), LienHeActivity.class);
                        lienhe.putExtra("loai", 4);
                        startActivity(lienhe);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        //xoa key user
                        Paper.book().delete("user");
                        Toast.makeText(getApplicationContext(), "Đăng xuất thành công", Toast.LENGTH_LONG).show();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }


    private void getSpMoi() {
        //lấy danh sách sản phẩm mới từ máy chủ thông qua API và hiển thị chúng trên RecyclerView trong giao diện người dùng
        compositeDisposable.add(apiBanHang.getSpMoi()
                //compositeDisposable được khởi tạo để quản lý các Disposable được tạo ra từ các Observable.
                // apiBanHang là một thể hiện của interface được tạo ra để tương tác với API.
                //phương thức getSpMoi() sử dụng phương thức subscribe() để đăng ký cho việc nhận dữ liệu từ Observable.
                // subscribeOn() được sử dụng để đặt luồng xử lý cho việc gọi API là luồng I/O và observeOn() được sử dụng
                // để đặt luồng xử lý cho việc hiển thị dữ liệu lên RecyclerView là luồng chính.
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            //Dữ liệu trả về từ API là một đối tượng sanPhamMoiModel, chứa một danh sách các đối tượng SanPhamMoi.
                            if (sanPhamMoiModel.isSuccess()) {//Nếu API trả về thành công
                                //danh sách sản phẩm mới (mangSpMoi) được cập nhật với dữ liệu trả về từ API
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewmanhinhchinh.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Khong ket noi duoc server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void ActionViewFlipper() {//chạy quảng cáo
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://ducshntrong.github.io/webcuoiki/assets/img/hedenvuive.png");
        mangquangcao.add("https://ducshntrong.github.io/webcuoiki/assets/img/thang5toi.png");
        mangquangcao.add("https://xedap.vn/wp-content/uploads/2023/05/BANNER-WEB-DAP-KHOE-SONG-CHAT-09-scaled.jpg");
        mangquangcao.add("https://xedap.vn/wp-content/uploads/2022/11/banner-xe-dia-hinh-fix-2021-scaled-1.jpg");
        mangquangcao.add("https://ducshntrong.github.io/webcuoiki/assets/img/Banner-xe-dap-dien.png");
        mangquangcao.add("https://ducshntrong.github.io/webcuoiki/assets/img/Banner-xe-may-dien-01-1.jpg");
        mangquangcao.add("https://ducshntrong.github.io/webcuoiki/assets/img/Sale.png");
        //duyệt để lấy ra từng ptu của mảng
        for (int i = 0; i<mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);

        }
        viewFlipper.setFlipInterval(7000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    //    private void Banner() {
//        viewFlipper.setFlipInterval(3000);
//        viewFlipper.setAutoStart(true);
//    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                //thêm dữ liệu từ db
                                mangloaisp = loaiSpModel.getResult();//đổ data vào mangloaisp
                                mangloaisp.add(new LoaiSp("Đăng xuất",
                                        "https://static.vecteezy.com/system/resources/previews/000/575/503/original/vector-logout-sign-icon.jpg"));
                                //khoi tao adapter
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
                                listViewmanhinhchinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }


    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerViewmanhinhchinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewmanhinhchinh.setLayoutManager(layoutManager);
        recyclerViewmanhinhchinh.setHasFixedSize(true);
        listViewmanhinhchinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerLayout);
        badge = findViewById(R.id.menu_sl);

        // khoi tao List
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        //kiểm tra xem có thông tin giỏ hàng được lưu trữ trong Paper hay không.
        if (Paper.book().read("giohang") != null){
            //Nếu có, phương thức sử dụng Paper.book().read("giohang") để đọc thông tin
            // giỏ hàng và gán nó cho biến Utils.manggiohang.
            Utils.manggiohang = Paper.book().read("giohang");
        }
        if (Utils.manggiohang == null) {//nếu = nul thì khởi tạo
            //Nếu biến Utils.manggiohang có giá trị null, phương thức khởi tạo một ArrayList mới.
            Utils.manggiohang = new ArrayList<>();
        }else {
            //Nếu không, phương thức sử dụng một vòng lặp để tính tổng số lượng sản phẩm trong giỏ hàng
            int totalItem = 0;
            for (int i = 0; i< Utils.manggiohang.size(); i++) {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }

        //click chuyen trang gio hang
        FrameLayout frameLayoutgiohang = findViewById(R.id.framegiohang);
        frameLayoutgiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        //Phương thức onResume() được sử dụng để cập nhật số lượng sản phẩm trong giỏ hàng trên biểu tượng "badge"
        // sau khi người dùng thêm sản phẩm vào giỏ hàng. Phương thức này được gọi mỗi khi ứng dụng quay lại trạng thái hoạt động.
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i< Utils.manggiohang.size(); i++) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    //ktra thiết bị có kết nối internet hay k trước khi thực hiện các hoạt động liên quan đến
    // mạng như truy cập API hoặc tải xuống dữ liệu.
    private boolean isConnected (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifi != null && wifi.isConnected()) ||(mobile != null && mobile.isConnected()) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        //Phương thức này được gọi khi hoạt động (Activity) đang bị hủy và được sử dụng để giải
        // phóng bộ nhớ và các tài nguyên khác được sử dụng trong hoạt động.
        compositeDisposable.clear();
        super.onDestroy();
    }
}