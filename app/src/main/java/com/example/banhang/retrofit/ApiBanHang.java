package com.example.banhang.retrofit;

import com.example.banhang.model.DonHangModel;
import com.example.banhang.model.LoaiSpModel;
import com.example.banhang.model.SanPhamMoiModel;
import com.example.banhang.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiBanHang {
    //GET data
    //đường dẫn API là "getloaisp.php". Nó được sử dụng để lấy danh sách các loại sản phẩm từ máy chủ.
    // Ngoài ra, phương thức getLoaiSp() trả về một đối tượng Observable<LoaiSpModel>.
    //LoaiSpModel là một lớp đại diện cho dữ liệu JSON được trả về từ API, được chuyển đổi thành đối
    // tượng Java bằng cách sử dụng thư viện Gson của Google.
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    //POST data
    //Phương thức @POST được sử dụng để xác định loại yêu cầu HTTP là POST và đường dẫn API mà yêu cầu sẽ được gửi đi.
    @POST("chitiet.php")
    //đường dẫn API là "chitiet.php". Nó được sử dụng để lấy danh sách các sản phẩm tương ứng với một loại sản phẩm cụ thể từ máy chủ.
    @FormUrlEncoded
    //Phương thức @FormUrlEncoded được sử dụng để xác định rằng dữ liệu yêu cầu sẽ được mã hóa dưới dạng URL-encoded form data.
    Observable<SanPhamMoiModel> getSanPham(
            //Phương thức getSanPham() có hai tham số được chuyển đến API là page và loai. Chúng được đánh dấu bằng annotation @Field
            // để xác định rằng chúng là các trường dữ liệu dạng form. Giá trị của các trường này sẽ được gửi cùng với yêu cầu POST.
            @Field("page") int page,
            @Field("loai") int loai
    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("resetpass.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );

}
