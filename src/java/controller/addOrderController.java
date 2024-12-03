/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.DAO;
import DAO.InformationDAO;
import DAO.OrderDAO;
import DAO.PaymentDAO;
import DAO.ShipmentDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Customer;
import model.Information;
import model.Order;
import model.Payment;
import model.Product;
import model.ProductOrder;
import model.Shipment;
import model.Voucher;

/**
 *
 * @author luong
 */
public class addOrderController extends HttpServlet {

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
        String topic = request.getParameter("topic");
        // kiểm tra nếu topic là setOrder nghĩa là yêu cầu đặt hàng gửi đến 
        if (topic != null && topic.equals("setOrder")) {
            // pay-method là lấy phương thức thanh toán
            String payMethod = request.getParameter("pay-method");
            // inforId là lấy Id của thông tin của nơi nhận hàng
            String inforId = request.getParameter("inforId");
            InformationDAO inforDAO = new InformationDAO();
            Information information = inforDAO.getInformationById(inforId);
            HttpSession session = request.getSession();
            Customer customer = (Customer) session.getAttribute("customer");
            String customerId = customer.getId();
            // lấy shipment là phương thức vận chuyển
            String shipMethod = request.getParameter("ship-method");
            // lấy ngày hiện tại 
            Date currentDate = new Date();
            java.sql.Date date = new java.sql.Date(currentDate.getTime());
            Voucher voucher = new Voucher();
//            lấy voucher từ session, nếu có voucher thì voucher được lưu ở session
            voucher = (Voucher) session.getAttribute("voucher");
            // đây là bước lấy voucher từ session để lưu csdl nên lấy xong phải xóa voucher khởi session
            session.removeAttribute("voucher");
            DAO dao = new DAO();
            Cookie[] cookies = request.getCookies();
            String data = "";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("cart")) {
                        data = cookie.getValue();
                        // lấy danh sách id sản phẩm ở trong giỏ hàng từ cookie
                    }
                }
            }
            // danh sách id ở dạng idsp1|idsp2... nên phải split bởi dấu | để lấy được listId
            String[] listIdProduct = data.split("\\|");
            // tạo một danh sách sản phẩm đặt
            List<ProductOrder> listOrdersProducts = new ArrayList<>();
            // Tạo một Map để lưu trữ số lượng xuất hiện của từng sản phẩm trong giỏ hàng
            Map<String, Integer> demPhanTu = new HashMap<>();

            // Đếm số lượng xuất hiện của từng phần tử trong danh sách
            for (String phanTu : listIdProduct) {
                demPhanTu.put(phanTu, demPhanTu.getOrDefault(phanTu, 0) + 1);
            }
            // duyệt danh sách sản phẩm để lưu vào db
            for (Map.Entry<String, Integer> i : demPhanTu.entrySet()) {
                if (!i.getKey().isEmpty()) {
//                    lấy sản phẩm theo id sản phẩm
                    Product p = dao.getProductbyId(i.getKey());
//                    khởi tạo productOrder từ product
                    ProductOrder c = new ProductOrder(i.getValue(), p);
                    // thêm vào danh sách sản phẩm đặt
                    listOrdersProducts.add(c);
                }
            }
            // khởi tạo shipment là đối tượng lưu thông tin phương thức vận chuyển
            Shipment shipment = new Shipment(
                    "",
                    shipMethod,
                    null,
                    Double.parseDouble(request.getParameter("transportfee")),
                    0,
                    information
            );
            // khởi tạo payment là đối tượng lưu thông tin phương thức thanh toán
            Payment payment = new Payment(
                    "",
                    payMethod,
                    0,
                    date,
                    0
            );
            // khởi tạo đối tượng Order là đối tượng thứ nhất thêm vào database
            Order order = new Order(
                    "",
                    payment,
                    shipment,
                    voucher,
                    customer,
                    date,
                    null,
                    Double.parseDouble(request.getParameter("discountPrice1")),
                    Double.parseDouble(request.getParameter("totalBill")),
                    Double.parseDouble(request.getParameter("totalAmount1")),
                    listOrdersProducts
            );

            // Thêm dữ liệu vào bảng order
            OrderDAO orderDAO = new OrderDAO();
            String orderId = orderDAO.addOrder(order);

            for (ProductOrder i : order.getProductorder()) {
                // thêm dữ liệu vào bảng sản phẩm đặt
                orderDAO.addProductOrder(i, orderId);
                // cập nhật số lượng còn lại của sản phẩm trong bảng tblproductdetail
                int avaiQuantity = dao.getQuantityProductById(i.getId());
                dao.updateQuantityProductById(avaiQuantity - i.getQuantity(), i.getId());
                // cập nhật số lượng đã bán trong bảng tblproduct
                dao.updateSoldProductById(i.getQuantity(), i.getId());
            }

            // lưu phương thức vận chuyển vào bảng shipment
            ShipmentDAO shipDAO = new ShipmentDAO();
            shipDAO.addShipment(shipment, orderId);

            // lưu phương thức thanh toán vào bảng payment
            PaymentDAO payDAO = new PaymentDAO();
            payDAO.addPayment(payment, orderId);
            
            // reset lại giỏ hàng sau khi đặt thành công
            Cookie cookie = new Cookie("cart", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
//        response.getWriter().println("<script>alert('Done!');</script>");
        // thông báo đặt thành công lên giao diện checkout
        request.setAttribute("alert", """
                                      <div id="popup" class="popup">
                                          <h2>Đặt hàng thành công</h2>
                                          <p>Cảm ơn bạn đã mua hàng!!!</p>
                                      </div>
                                      """);
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
//        processRequest(request, response);
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
