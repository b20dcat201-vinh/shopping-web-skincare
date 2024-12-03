/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.DAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Cart;
import model.Product;
import model.ProductCart;

/**
 *
 * @author luong
 */
public class addProductToCartController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
        // lấy id sản phẩm thêm
        String productId = request.getParameter("Id");
        // Lấy tất cả các cookie từ request
        Cookie[] cookies = request.getCookies();
        String data = "";
        String oldData = "";
        // duyệt danh sách cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Kiểm tra nếu tên cookie là "cart"
                if (cookie.getName().equals("cart")) {
                    // lấy dữ liệu sản phẩm trong giỏ hàng
                    data = cookie.getValue();
                    // lưu lại dữ liệu sản phẩm trong giỏ hàng
                    oldData = data;
                }
            }
        }
        // kiểm tra nếu giỏ hàng có sản phẩm hay chưa
        if (data.length() > 0) {
            // nếu có id sản phẩm cần thêm
            if (productId != null) {
                // cộng thêm id sản phẩm vào biến data, biến data đang lưu list id sản phẩm của giỏ hàng
                data += "|" + productId;
            } else {
                // vì controller này chỉ điều khiển việc thêm hoặc xóa sản phẩm 
                // nếu không có id sản phẩm cần thêm nghĩa là có yêu cầu giảm số lượng 1 sản phẩm trong giỏ
                String downId = request.getParameter("downId");
                // hàm downProductCard() sẽ thực hiện giảm số lượng 1 sản phẩm trong giỏ
                data = downProductCard(data, downId);
//                System.out.println(downId);
            }
        } else {
            // nếu giỏ hàng chưa có sản phẩm nào thì thêm vào
            data = productId;
        }
        // dữ liệu data của cookie là listid sản phẩm trong giỏ ở dạng idsp1|idps2...
        // nên cần split bởi dấu | để lấy danh sách id ra
        String[] listIdProduct = data.split("\\|");
        // tạo list sản phẩm
        List<Product> list = new ArrayList<>();
        // tạo list sản phẩm trong giỏ hàng
        List<ProductCart> listCardProducts = new ArrayList<>();
        // Tạo một Map để lưu trữ số lượng xuất hiện của từng phần tử
        Map<String, Integer> demPhanTu = new HashMap<>();

        // Đếm số lượng xuất hiện của từng phần tử trong danh sách
        for (String phanTu : listIdProduct) {
            demPhanTu.put(phanTu, demPhanTu.getOrDefault(phanTu, 0) + 1);
        }
        DAO dao = new DAO();
        // kiểm xem số lượng sản phẩm trong giỏ đã lớn hơn số lượng sản phẩm trong kho (hay csdl) chưa
        if (demPhanTu.containsKey(productId) && demPhanTu.get(productId) > dao.getQuantityProductById(productId)) {
//            nếu lớn hơn thì set lại giỏ hàng theo danh sách cũ nghĩa là không chứa sản phẩm đang yêu cầu thêm
            Cookie cookie = new Cookie("cart", oldData);
            // Thiết lập thời gian sống của cookie (ví dụ: 24 giờ)
            cookie.setMaxAge(24 * 60 * 60);
            // Thêm cookie vào response
            response.addCookie(cookie);
        } else {
//            nếu nhỏ hơn thì thêm sản phẩm được yêu cầu vào cookie
            Cookie cookie = new Cookie("cart", data);
            // Thiết lập thời gian sống của cookie (ví dụ: 24 giờ)
            cookie.setMaxAge(24 * 60 * 60);
            // Thêm cookie vào response
            response.addCookie(cookie);
        }
//      yêu cầu tải lại trang hiển thị giỏ hàng
        response.sendRedirect("cart_control");
    }
    
    // hàm thực hiện xóa một id sản phẩm trong danh sách id sản phẩm của giỏ hàng
    public String downProductCard(String listId, String deleteId) {
        String[] tmp = listId.split("\\|");
        String result = "";
        ArrayList<String> danhSach = new ArrayList<>(Arrays.asList(tmp));
        danhSach.remove(deleteId);
        result = String.join("|", danhSach);
        // In danh sách sau khi xóa
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String topic = request.getParameter("topic");
        if (topic.equals("UpDownCardProduct")) {
            String productId = request.getParameter("productId");
            String upDownQuantity = request.getParameter("quantity");
//            System.out.println(productId + " " + upQuantity);
            Cookie[] cookies = request.getCookies();
            String data = "";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // Kiểm tra nếu tên cookie là "username"
                    if (cookie.getName().equals("cart")) {
                        data = cookie.getValue();
                    }
                }
            }
            ArrayList<String> listTmp = new ArrayList<>(Arrays.asList(data.split("\\|")));
            // Tạo một Map để lưu trữ số lượng xuất hiện của từng phần tử
            Map<String, Integer> demPhanTu = new HashMap<>();

            // Đếm số lượng xuất hiện của từng phần tử trong danh sách
            for (String phanTu : listTmp) {
                demPhanTu.put(phanTu, demPhanTu.getOrDefault(phanTu, 0) + 1);
            }
            if (demPhanTu.containsKey(productId)) {
                int tmp = Integer.valueOf(upDownQuantity) - demPhanTu.get(productId);
                if (tmp > 0) {
                    // thêm sản phẩm cho đủ
                    for (int i = 0; i < Math.abs(tmp); i++) {
                        data += "|" + productId;
                    }
                } else {
                    // xóa bớt sản phẩm cho đủ
                    if (tmp != 0) {
                        for (int i = 0; i < Math.abs(tmp); i++) {
                            data = downProductCard(data, productId);
                        }
                    }

                }
            }
            Cookie cookie = new Cookie("cart", data);

            // Thiết lập thời gian sống của cookie (ví dụ: 24 giờ)
            cookie.setMaxAge(24 * 60 * 60);
            // Thêm cookie vào response
            response.addCookie(cookie);

            DAO dao = new DAO();
            List<Product> list = new ArrayList<>();
            List<ProductCart> listCardProducts = new ArrayList<>();
            // Tạo một Map để lưu trữ số lượng xuất hiện của từng phần tử

            for (Map.Entry<String, Integer> i : demPhanTu.entrySet()) {
//            System.out.println("Phan tu: " + i.getKey() + ", So luong: " + i.getValue());
                if (!i.getKey().isEmpty()) {
                    Product p = dao.getProductbyId(i.getKey());
                    list.add(p);
                    ProductCart c = new ProductCart(i.getValue(), p);
                    listCardProducts.add(c);
                }
            }
            Cart cart = new Cart(listCardProducts);
            System.out.println(cart.getTotalprice());
            DecimalFormat decimalFormat = new DecimalFormat("0,000");
            response.getWriter().write(decimalFormat.format(cart.getTotalprice()));
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
