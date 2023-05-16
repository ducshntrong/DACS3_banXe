package com.example.banhang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.banhang.R;
import com.example.banhang.adapter.loaiAdapter;
import com.example.banhang.model.SanPhamMoi;
import com.example.banhang.retrofit.ApiBanHang;
import com.example.banhang.retrofit.RetrofitClient;
import com.example.banhang.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;



public class loaiSanPhamActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page = 1;
    int loai;
    loaiAdapter adapterDt;
    List<SanPhamMoi> sanPhamMoiList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoanding = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loai);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        loai = getIntent().getIntExtra("loai", 1);

        AnhXa();
        ActionToolbar();
        getData(page);
        addEventLoad();
    }

    // addEventLoad() được sử dụng để thêm một sự kiện cuộn cho RecyclerView,
    // để tự động tải thêm dữ liệu khi người dùng cuộn đến cuối danh sách.
    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //phương thức recyclerView.addOnScrollListener() được sử dụng để đăng ký một trình nghe sự kiện cho RecyclerView.
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //Khi người dùng cuộn danh sách, phương thức onScrolled() được gọi.
                super.onScrolled(recyclerView, dx, dy);
                if (isLoanding == false) {
                    //kiểm tra xem biến isLoanding có bằng false hay không. Nếu isLoanding là false,
                    // điều này có nghĩa là không có quá trình tải dữ liệu nào đang diễn ra.
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == sanPhamMoiList.size()-1) {
                        //xác định vị trí của phần tử cuối cùng hiển thị trên màn hình.
                        // Nếu vị trí này bằng với vị trí của phần tử cuối cùng trong danh sách sanPhamMoiList,
                        // điều này có nghĩa là người dùng đã cuộn đến cuối danh sách.
                        isLoanding = true;
                        loadMore();
                        //Khi cuộn đến cuối danh sách, phương thức gọi phương thức loadMore() để tải thêm dữ liệu
                        // và đặt biến isLoanding thành true để đánh dấu rằng quá trình tải dữ liệu đang diễn ra.
                    }
                }
            }
        });
    }

    private void loadMore() {//Phương thức loadMore() được sử dụng để tải thêm dữ liệu vào danh sách sản phẩm mới.
        handler.post(new Runnable() {
            //handler.post() được sử dụng để thêm một phần tử null vào danh sách sản phẩm mới
            // và thông báo cho Adapter biết rằng có một phần tử đã được thêm vào. Điều này giúp cho
            // RecyclerView hiển thị một phần tử trống để người dùng biết rằng có quá trình tải dữ liệu đang diễn ra.
            @Override
            public void run() {
                //add null
                sanPhamMoiList.add(null);
                adapterDt.notifyItemInserted(sanPhamMoiList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {//tạm dừng việc thêm dữ liệu và thực hiện các hoạt động sau một khoảng thời gian nhất định.
            @Override
            public void run() {
                // remove null
                sanPhamMoiList.remove(sanPhamMoiList.size()-1);//xóa phần tử null đã được thêm vào trước đó
                adapterDt.notifyItemRemoved(sanPhamMoiList.size());
                page = page+1;//tăng số trang của dữ liệu cần tải lên
                getData(page);//tải thêm dữ liệu từ trang mới.
                adapterDt.notifyDataSetChanged();//thông báo cho Adapter biết rằng dữ liệu đã được thay đổi
                isLoanding = false;//đánh dấu rằng quá trình tải dữ liệu đã kết thúc.
            }
        },2000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiBanHang.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {//Khi dữ liệu được tải thành công
                                if (adapterDt == null) { //kiểm tra xem Adapter đã được khởi tạo hay chưa.
                                    //Nếu Adapter chưa được khởi tạo
                                    sanPhamMoiList = sanPhamMoiModel.getResult();
                                    //khởi tạo Adapter và gán nó cho RecyclerView.
                                    adapterDt = new loaiAdapter(getApplicationContext(), sanPhamMoiList);
                                    recyclerView.setAdapter(adapterDt);
                                }else {//Nếu Adapter đã được khởi tạo trước đó
                                    int vitri = sanPhamMoiList.size()-1;
                                    int soluongadd = sanPhamMoiModel.getResult().size();
                                    for (int i = 0; i < soluongadd; i++) {
                                        //sau khi có dữ liệu thì ta duyệt qua dữ liệu vừa tải về
                                        //add vào sanPhamMoiList
                                        sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                                    }
                                    //thong báo cho adapter là add vào vị trí nào và số lượng là bn
                                    adapterDt.notifyItemRangeInserted(vitri, soluongadd);
                                }

                            }else {
                                Toast.makeText(getApplicationContext(), "Het du lieu", Toast.LENGTH_LONG).show();
                                isLoanding = true;//đánh dấu rằng quá trình tải dữ liệu đã kết thúc.
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Khong ket noi duoc server", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ActionBar a = getSupportActionBar();
        if(loai == 1){
            a.setTitle("Xe máy điện");
        } else if (loai == 2) {
            a.setTitle("Xe đạp điện");
        }
    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toobar);
        recyclerView = findViewById(R.id.recycleview_dt);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}