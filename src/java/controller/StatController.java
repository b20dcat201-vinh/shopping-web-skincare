/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.DAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Product;

/**
 *
 * @author DUC TOAN
 */
@WebServlet(name = "StatController", urlPatterns = {"/stat"})
public class StatController extends HttpServlet {

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
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet StatServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StatServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        // Lấy dữ liệu thống kê từ nguồn nào đó
        DAO dao = new DAO();
        LocalDate currentDate = LocalDate.now();

        // Lấy thông tin về tháng và năm hiện tại
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        // Tạo đối tượng YearMonth từ tháng và năm hiện tại
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);

        // Lấy ngày bắt đầu và ngày kết thúc của tháng hiện tại
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        double monthlyRevenue;
        try {
            monthlyRevenue = dao.revenueStat(startDate.toString(), endDate.toString());
              request.setAttribute("monthlyRevenue", monthlyRevenue);
        } catch (Exception ex) {
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Đặt dữ liệu thống kê là thuộc tính của request
      

        int billInMonth;
        try {
            billInMonth = dao.countOder(startDate.toString(), endDate.toString());
            request.setAttribute("billinmonth", billInMonth);
        } catch (Exception ex) {
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        double dailyRevenue;
        try {
            dailyRevenue = dao.revenueStat(currentDate.toString(), currentDate.toString());
            request.setAttribute("dailyRevenue", dailyRevenue);
        } catch (Exception ex) {
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        int bill_today;
        try {
            bill_today = dao.countOder(currentDate.toString(), currentDate.toString());
             request.setAttribute("bill_today", bill_today);
        } catch (Exception ex) {
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        }
       

        List<Double> mothlyRevenueList = new ArrayList<>();
        List<String> monthList = new ArrayList<>();
        for (int i = 12; i >= 0; i--) {
            int month = currentMonth - i;
            int year = currentYear;
            if (month <= 0) {
                month += 12;
                year = year - 1;
            }
            String tmp = "'Tháng " + String.valueOf(month) + "/"
                    + String.valueOf(year) + "'";
            monthList.add(tmp);
            try {
                mothlyRevenueList.add(dao.monthlyRevenueStat(String.valueOf(month), String.valueOf(year)));
            } catch (Exception ex) {
                Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        request.setAttribute("monthList", monthList);
        request.setAttribute("monthlyRevenueList", mothlyRevenueList);

        Map<Product, Double> TKproduct;
        try {
            TKproduct = dao.getSoldProducts(startDate.toString(), endDate.toString(), 5);
             request.setAttribute("soldproducts", TKproduct);
        } catch (Exception ex) {
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        Map<String, Double> topCustomer;
        try {
            topCustomer = dao.getCustomer(startDate.toString(), endDate.toString(), 5);
             request.setAttribute("topCustomer", topCustomer);
        } catch (Exception ex) {
            Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        // Chuyển tiếp yêu cầu đến trang JSP để hiển thị kết quả
        request.getRequestDispatcher("stat.jsp").forward(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy giá trị ngày bắt đầu và ngày kết thúc từ yêu cầu
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String action = request.getParameter("action");
        StringBuilder responseText = new StringBuilder();
        DAO dao = new DAO();
        if ("1".equals(action)) {
            double revenue;
            try {
                revenue = dao.revenueStat(startDate, endDate);
                responseText.append(String.valueOf(revenue)).append("\n");
                
            } catch (Exception ex) {
                Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
            }
            int ordercounter;
            try {
                ordercounter = dao.countOder(startDate, endDate);
                 responseText.append(ordercounter).append("\n");
            } catch (Exception ex) {
                Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Map<Product, Double> TKproduct;
            try {
                TKproduct = dao.getSoldProducts(startDate, endDate, 10);
                for (Map.Entry<Product, Double> entry : TKproduct.entrySet()) {
                Product product = entry.getKey();
                Double quantity = entry.getValue();
                responseText.append(product.getId()).append(":").append(product.getName()).append(":");
                responseText.append(quantity).append("\n");
                // Thực hiện các thao tác với product và quantity
            }
                
            } catch (Exception ex) {
                Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
           
            
        }
        if ("2".equals(action)) {
            List<String> ordersList;
            try {
                ordersList = dao.getOrderByProductId(startDate, endDate, action);
                for (String order : ordersList) {
                responseText.append(order).append("\n");
            }
            } catch (Exception ex) {
                Logger.getLogger(StatController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
//        response.setHeader("revenue", revenue);

        // Gửi kết quả về client
        PrintWriter out = response.getWriter();
        out.print(responseText.toString());
        out.flush();
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
