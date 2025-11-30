package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Servlet implementation class ProductDisplaySerlet
 */
@WebServlet("/productdisplayservlet")
public class ProductDisplayServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ProductDisplayServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String purpose = request.getParameter("purpose");
        if(purpose == null || purpose.equalsIgnoreCase("getinventorydetails")) {
            // Build JSON array of products
            try (Connection conn = DatabaseConnectionPool.getConnectionPool();
                 PreparedStatement invStmt = conn.prepareStatement(
                         "select cropid, categoryid, unitsavailable, unittype from inventorydetails");
                 ResultSet invRs = invStmt.executeQuery()) {

                JSONArray products = new JSONArray();

                while (invRs.next()) {
                    String cropId = invRs.getString("cropid");

                    JSONObject obj = new JSONObject();
                    obj.put("cropid", cropId);
                    obj.put("categoryid", invRs.getString("categoryid"));
                    obj.put("unitsavailable", invRs.getInt("unitsavailable"));
                    obj.put("unittype", invRs.getString("unittype"));
                    obj.put("productimage", "imagedisplayhelperservlet?purpose=exploreproductimage&cropid=" + cropId);

                    // Min/Max price
                    try (PreparedStatement priceStmt = conn.prepareStatement(
                            "select min(priceperunit) as min_price, max(priceperunit) as max_price from farmercropjunction where cropid = ?")) {
                        priceStmt.setString(1, cropId);
                        try (ResultSet priceRs = priceStmt.executeQuery()) {
                            int minPrice = 0;
                            int maxPrice = 0;
                            if(priceRs.next()) {
                                minPrice = priceRs.getInt("min_price");
                                maxPrice = priceRs.getInt("max_price");
                            }
                            obj.put("minpriceperunit", minPrice);
                            obj.put("maxpriceperunit", maxPrice);
                        }
                    }

                    // Farmer details (array)
                    JSONArray farmers = new JSONArray();
                    try (PreparedStatement farmerStmt = conn.prepareStatement(
                            "select a.farmerid as farmerid, b.fullname as farmername, a.priceperunit as priceperunit, a.noofunitsavailable as noofunitsavailable, " +
                            "b.farmeraddress as location, b.phoneno as contactnumber " +
                            "from farmercropjunction a join farmerdetails b on a.farmerid = b.farmerid where a.cropid = ?")) {
                        farmerStmt.setString(1, cropId);
                        try (ResultSet frs = farmerStmt.executeQuery()) {
                            while (frs.next()) {
                                JSONObject f = new JSONObject();
                                f.put("farmerid", frs.getString("farmerid"));
                                f.put("farmername", frs.getString("farmername"));
                                f.put("contactnumber", String.valueOf(frs.getLong("contactnumber")));
                                f.put("location", frs.getString("location"));
                                f.put("priceperunit", frs.getInt("priceperunit"));
                                f.put("noofunitsavailable", frs.getInt("noofunitsavailable"));
                                farmers.put(f);
                            }
                        }
                    }
                    obj.put("farmerdetails", farmers);

                    products.put(obj);
                }

                response.getWriter().write(products.toString());
                return;
            } catch (SQLException sqle) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Failed to load inventory\"}");
                return;
            }
        }

        // Default fall-through
        response.getWriter().write("[]");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}