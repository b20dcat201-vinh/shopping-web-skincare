/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author luong
 */
public class deleteProductInCartController extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");

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
        // kiểm tra nếu có topic là lệnh gửi yêu cầu xóa một sản phẩm khỏi giỏ hàng
        // topic thường đc đặt trong input ẩn
        String topic = request.getParameter("topic");
        System.out.println(topic);
        if (topic.equals("deleteCardProduct")) {
            // nếu là lệnh xóa thì thực hiện
            // lấy id sản phẩm cần xóa
            String productId = request.getParameter("productId");
//            lấy danh sách id sản phẩm từ giỏ hàng trong cookie
            Cookie[] cookies = request.getCookies();
            String cookieData = "";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("cart")) {
                        // cookiedata chứa danh sách id sản phẩm trong giỏ ở cookie
                        cookieData = cookie.getValue();
                        break;
                    }
                }
            }
            // thực hiện xóa id sản phẩm cần xóa khỏi danh sách id trong cookie
            String newCookieData = deleteIdProductInCart(cookieData, productId);
            if (!newCookieData.isEmpty()){
                // nếu giỏ hàng còn sản phẩm thì set lại cookie
                Cookie cookie = new Cookie("cart", newCookieData);
                cookie.setMaxAge(24*60*60);
                response.addCookie(cookie);
            } else {
                // nếu giỏ hàng hết sản phẩm thì set thời gian sống cookie là 0
                Cookie cookie = new Cookie("cart", newCookieData);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        response.sendRedirect("cart_control");
//        processRequest(request, response);
    }
    // hàm xóa
    protected String deleteIdProductInCart(String listId, String Id){
        String result = "";
        String[] tmp = listId.split("\\|");
        for (String o:tmp){
            if (!o.isEmpty() && !o.equals(Id)){
                result += "|" + o;
            }
        }
        try {
            return result.substring(1);
        } catch (Exception e) {
            return result;
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
