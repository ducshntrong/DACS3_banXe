package com.example.banhang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banhang.R;
import com.example.banhang.model.DonHang;

import java.text.DecimalFormat;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    //sử dụng để lưu trữ và sử dụng lại các chế độ xem không còn hiển thị trên màn hình nhằm cải thiện hiệu suất.
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        DonHang donHang = listdonhang.get(position);
        holder.tongtien.setText("Tổng tiền: "+decimalFormat.format(Double.parseDouble(donHang.getTongtien())) + "đ");
        holder.ngaymua.setText("Ngày đặt: "+ donHang.getNgaymua());
        if (donHang.getTrangthai() == 0){
            holder.txtdonhang.setText("Đơn hàng: Đang giao");
        }else if (donHang.getTrangthai() == 1){
            holder.txtdonhang.setText("Đơn hàng: Đã giao");
            holder.txtdonhang.setTextColor(Color.parseColor("#036D03"));
            holder.tongtien.setTextColor(Color.parseColor("#036D03"));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(//hiển thị danh sách sản phẩm trong đơn hàng
                holder.reChiTiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        // adapter chi tiet
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context, donHang.getItem());
        holder.reChiTiet.setLayoutManager(layoutManager);
        holder.reChiTiet.setAdapter(chiTietAdapter);
        holder.reChiTiet.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtdonhang, tongtien, ngaymua;
        RecyclerView reChiTiet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            reChiTiet = itemView.findViewById(R.id.recycleview_chitiet);
            tongtien = itemView.findViewById(R.id.tongtien);
            ngaymua = itemView.findViewById(R.id.ngaymua);
        }
    }
}
