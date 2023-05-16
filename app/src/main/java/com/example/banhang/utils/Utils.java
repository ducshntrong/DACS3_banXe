package com.example.banhang.utils;

import com.example.banhang.model.GioHang;
import com.example.banhang.model.User;

import java.util.ArrayList;
import java.util.List;
//Lớp Utils được sử dụng để lưu trữ các giá trị và đối tượng toàn cục được sử dụng trong ứng dụng.
public class Utils {
    //BASE_URL là đường dẫn URL của máy chủ, sử dụng để gửi các yêu cầu API từ ứng dụng
    public static final String BASE_URL="http://10.0.2.2:8080/banxedien/";

    //manggiohang là một danh sách các đối tượng GioHang được sử dụng để lưu trữ các sản phẩm được thêm vào giỏ hàng.
    public static List<GioHang> manggiohang; //Tạo biến toàn cục
    //mangmuahang cũng là một danh sách các đối tượng GioHang, được sử dụng để lưu trữ các sản phẩm đã được mua.
    // Biến này được khởi tạo là một đối tượng ArrayList mới.
    public static  List<GioHang> mangmuahang = new ArrayList<>();

    //user_current là một đối tượng User đại diện cho người dùng hiện tại. Đối tượng này sẽ lưu trữ thông
    //tin đăng nhập của người dùng và được sử dụng để xác định quyền truy cập của người dùng cho các chức năng trong ứng dụng.
    public static User user_current = new User();
}
