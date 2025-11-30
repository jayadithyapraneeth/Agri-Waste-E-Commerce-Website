package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.agriwastetrade.site.DatabaseConnectionPool;

/**
 * Servlet implementation class OrderTrackingServletFarmer
 */
@WebServlet("/ordertrackingservletbuyer")
public class OrderTrackingServletBuyer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public OrderTrackingServletBuyer() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("loginstatus") == null || "died".equals(session.getAttribute("loginstatus"))) {
            // Return 401 with empty array for frontend to handle
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("[]");
            return;
        }

        String buyerid = (String) session.getAttribute("userid");
        if(buyerid == null || buyerid.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("[]");
            return;
        }

        JSONArray out = new JSONArray();
        try (Connection conn = DatabaseConnectionPool.getConnectionPool();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT orderid, cropid, noofunits, toaddress, orderworth, Date, status, paymentstatus, delivereddate, paymentdate " +
                     "FROM orderhistory WHERE buyerid = ? ORDER BY Date DESC")) {

            pstmt.setString(1, buyerid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject o = new JSONObject();
                    // Map to frontend keys
                    o.put("id", rs.getString("orderid"));
                    o.put("date", rs.getDate("Date") != null ? rs.getDate("Date").toString() : JSONObject.NULL);
                    o.put("product", rs.getString("cropid"));
                    o.put("quantity", rs.getInt("noofunits"));
                    o.put("price", rs.getInt("orderworth"));
                    o.put("status", rs.getString("status"));
                    o.put("paymentstatus", rs.getString("paymentstatus"));
                    o.put("deliveryAddress", rs.getString("toaddress"));
                    // Using paymentstatus as paymentMethod display for now
                    o.put("paymentMethod", rs.getString("paymentstatus"));

                    // Optional fields (not provided currently)
                    o.put("farmer", JSONObject.NULL);
                    o.put("farmerLocation", JSONObject.NULL);
                    // trackingNumber, estimatedDelivery, tracking intentionally omitted or null

                    out.put(o);
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject err = new JSONObject();
            err.put("error", "Failed to load orders");
            response.getWriter().write(err.toString());
            return;
        }

        PrintWriter pw = response.getWriter();
        pw.write(out.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}