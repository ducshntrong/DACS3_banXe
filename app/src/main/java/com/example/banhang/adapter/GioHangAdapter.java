package com.example.banhang.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.banhang.Interface.IImageClickListenner;
import com.example.banhang.R;
import com.example.banhang.activity.ChiTietActivity;
import com.example.banhang.activity.GioHangActivity;
import com.example.banhang.model.EventBus.TinhTongEvent;
import com.example.banhang.model.GioHang;
import com.example.banhang.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

import io.paperdb.Paper;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {
    Context context;
    List<GioHang> gioHangList;
    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensp());
        holder.item_giohang_soluong.setText(gioHang.getSoluong()+ " ");
        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_giohang_image);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_giohang_gia.setText("Giá: "+decimalFormat.format(gioHang.getGiasp())+"đ");
        long gia = gioHang.getSoluong()* gioHang.getGiasp();
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia)+"đ");
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //nếu tích vào thì set = true
                    //đặt thuộc tính đã chọn của đối tượng gioHang tương ứng trong danh sách Utils.manggiohang thành đúng.
                    Utils.manggiohang.get(holder.getAdapterPosition()).setChecked(true);
                    if (!Utils.mangmuahang.contains(gioHang)){
                        //Nếu danh sách Utils.mangmuahang chưa chứa đối tượng gioHang thì nó sẽ được thêm vào danh sách.
                        Utils.mangmuahang.add(gioHang);
                    }
                    //Danh sách Utils.manggiohang được cập nhật sau đó được lưu trữ trong đối tượng Paper
                    Paper.book().write("giohang", Utils.manggiohang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else {
                    //nếu bỏ tích
                    Utils.manggiohang.get(holder.getAdapterPosition()).setChecked(false);
                    for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                        //lặp qua danh sách Utils.mangmuahang và loại bỏ đối tượng gioHang nếu nó được tìm thấy.
                        if (Utils.mangmuahang.get(i).getIdsp() == gioHang.getIdsp()) {
                            Utils.mangmuahang.remove(i);
                            Paper.book().write("giohang", Utils.manggiohang);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }
                }
            }
        });

        holder.checkBox.setChecked(gioHang.isChecked());

        holder.setListenner(new IImageClickListenner() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                if (giatri == 1) {
                    if (gioHangList.get(pos).getSoluong() > 1) {
                        //ktr số lượng sp trong giỏ có lớn hơn 1 k, nếu lơn hơn thì trừ ngược lại thì k
                        int soluongmoi = gioHangList.get(pos).getSoluong()-1;
                        gioHangList.get(pos).setSoluong(soluongmoi);//sau khi trừ xong thì set lại giá trị

                        holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong()+ " ");
                        long gia = gioHangList.get(pos).getSoluong()* gioHangList.get(pos).getGiasp();
                        holder.item_giohang_giasp2.setText(decimalFormat.format(gia)+"đ");
                        Paper.book().write("giohang", Utils.manggiohang);
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    }
                }else if (giatri == 2) {
                    if (gioHangList.get(pos).getSoluong() < 10) {
                        int soluongmoi = gioHangList.get(pos).getSoluong()+1;
                        gioHangList.get(pos).setSoluong(soluongmoi);
                    }
                    holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong()+ " ");
                    long gia = gioHangList.get(pos).getSoluong()* gioHangList.get(pos).getGiasp();
                    holder.item_giohang_giasp2.setText(decimalFormat.format(gia)+"đ");
                    Paper.book().write("giohang", Utils.manggiohang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else if (giatri == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không?");
                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Utils.mangmuahang.remove(gioHang);
                            Utils.manggiohang.remove(pos);
                            Paper.book().write("giohang", Utils.manggiohang);
                            notifyDataSetChanged();
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_giohang_image, item_giohang_tru, item_giohang_cong;
        TextView item_giohang_tensp, item_giohang_gia, item_giohang_soluong, item_giohang_giasp2;
        IImageClickListenner listenner;
        CheckBox checkBox;
        ImageView img_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
            item_giohang_tru = itemView.findViewById(R.id.item_giohang_tru);
            item_giohang_cong = itemView.findViewById(R.id.item_giohang_cong);
            checkBox = itemView.findViewById(R.id.item_giohang_check);
            img_delete = itemView.findViewById(R.id.img_delete);


            //event click
            item_giohang_tru.setOnClickListener(this);
            item_giohang_cong.setOnClickListener(this);
            img_delete.setOnClickListener(this);


        }

        public void setListenner(IImageClickListenner listenner) {
            this.listenner = listenner;
        }

        @Override
        public void onClick(View view) {
            if (view == item_giohang_tru){
                listenner.onImageClick(view, getAdapterPosition(),1);
                // 1 tru
            } else if (view == item_giohang_cong){
                // 2 cong
                listenner.onImageClick(view, getAdapterPosition(),2);
            } else if (view == img_delete){
                // 3 xoá cart
                listenner.onImageClick(view, getAdapterPosition(),3);
            }
        }
    }
}
