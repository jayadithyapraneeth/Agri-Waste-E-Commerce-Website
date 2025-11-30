package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.json.JSONObject;
import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class OrderDetailsServlet
 */
@WebServlet("/orderdetailsservlet")
public class OrderDetailsServlet extends HttpServlet {
//	Connection conn = null;
//	
//	try {
//		Connection conn = DatabaseConnectionPool.getConnectionPool();
//	}catch(SQLException sqle) {
//		sqle.printStackTrace();
//	}
	
    private static final long serialVersionUID = 1L;

    public OrderDetailsServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection conn = null;
    	try {
    		conn = DatabaseConnectionPool.getConnectionPool();
    	}catch(SQLException sqle) {
    		sqle.printStackTrace();
    	}
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        // Ensure a session exists for guest checkout flows
        HttpSession session = request.getSession(false);
        if (session == null) {
        	// if the present session is null, then you have to create a new session and send its id to the next session
        	//but a new session created at this point is useless as the userid of the user is lost along with many session attributes
        	session = request.getSession(true);//as we are not using any cookies to store and retrive the previous session data, creating a new empty session have no meaning
        }
        String purpose = request.getParameter("purpose");

        // Basic auth/session check for API usage (allow guests unless explicitly marked died)
        Object ls = session.getAttribute("loginstatus");
        if(ls != null && String.valueOf(ls).equalsIgnoreCase("died")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject err = new JsonObject();
            err.addProperty("error", "Session expired. Please login again to continue.");
            try (PrintWriter out = response.getWriter()) {
                out.print(err);
            }
            return;
        }

        Gson gson = new Gson();

        if(purpose != null && purpose.equalsIgnoreCase("getorderdetails")) {
            // Build order details payload from session-stored cart + address
            String cartItemsJson = (String) session.getAttribute("cartItemsJson");
            JSONArray itemsArr;
            if(cartItemsJson != null && !cartItemsJson.isBlank()) {
                try {
                    itemsArr = new JSONArray(cartItemsJson);
                } catch (Exception e) {
                    itemsArr = new JSONArray();
                }
            } else {
                itemsArr = new JSONArray();
            }

            // Prefer session-cached address; otherwise load from DB via userid and cache it
            JSONObject address;
            Object addrAttr = session.getAttribute("buyerAddressJson");
            try {
                if (addrAttr instanceof String && !((String) addrAttr).isBlank()) {
                    address = new JSONObject((String) addrAttr);
                } else if (addrAttr instanceof JSONObject) {
                    address = new JSONObject(((JSONObject) addrAttr).toString());
                    // normalize to String in session to avoid classloader issues
                    session.setAttribute("buyerAddressJson", address.toString());
                } else {
                    address = getOrLoadBuyerAddress(session, conn);
                }
            } catch (Exception e) {
                address = getOrLoadBuyerAddress(session, conn);
            }

            JSONObject order = new JSONObject();
            order.put("items", itemsArr);
            order.put("deliveryAddress", address);

            try (PrintWriter out = response.getWriter()) {
                out.print(order);
            }
            return;
        }

        if(purpose != null && purpose.equalsIgnoreCase("getbuyeraddress")) {
            JSONObject address = getOrLoadBuyerAddress(session,conn);
            try (PrintWriter out = response.getWriter()) {
                out.print(address);
            }
            return;
        }

        // Fallback: unknown purpose
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject err = new JsonObject();
        err.addProperty("error", "Unsupported purpose for GET.");
        try (PrintWriter out = response.getWriter()) {
            out.print(err);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection conn = null;
    	try {
    		conn = DatabaseConnectionPool.getConnectionPool();
    	}catch(SQLException sqle) {
    		
    	}
    	
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        // Ensure a session exists for guest checkout flows
        HttpSession session = request.getSession(false);
        if (session == null) {
        	// if the present session is null, then you have to create a new session and send its id to the next session
        	//but a new session created at this point is useless as the userid of the user is lost along with many session attributes
        	session = request.getSession(true);//as we are not using any cookies to store and retrive the previous session data, creating a new empty session have no meaning
        }
        String purpose = request.getParameter("purpose");

        Object ls = session.getAttribute("loginstatus");
        if(ls != null && String.valueOf(ls).equalsIgnoreCase("died")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject err = new JsonObject();
            err.addProperty("error", "Session expired. Please login again to continue.");
            try (PrintWriter out = response.getWriter()) {
                out.print(err);
            }
            return;
        }

        Gson gson = new Gson();
        String body = readBody(request);

        if(purpose != null && purpose.equalsIgnoreCase("storecartdetails")) {//storing cart details in the session when add to cart button is clicked temporarily
            // Expect a JSON body: { items: [ { product, quantity, unitPrice, sellerID?, ... }, ... ] }
            JsonObject payload = gson.fromJson(body, JsonObject.class);
            JsonArray items = payload != null && payload.has("items") && payload.get("items").isJsonArray()
                    ? payload.getAsJsonArray("items")
                    : new JsonArray();

            //okay, now we will generate a unique cardid to store the cart items in the database
            
            System.out.println("Storing cart items: " + items.toString());
            System.out.println("items size:"+items.size());
            
            // Persist cart in session
            session.setAttribute("cartItemsJson", gson.toJson(items));
            session.setAttribute("numberofproducts", items.size());

            // Store delivery address draft or load from DB if not provided
            try {
                if(payload != null && payload.has("deliveryAddress") && payload.get("deliveryAddress").isJsonObject()) {
                    // if frontend provided an address, prefer that snapshot
                    JSONObject addr = new JSONObject(payload.getAsJsonObject("deliveryAddress").toString());
                    session.setAttribute("buyerAddressJson", addr.toString());
                } else {
                    JSONObject addr = getOrLoadBuyerAddress(session,conn);
                    if(addr != null) {
                        session.setAttribute("buyerAddressJson", addr.toString());
                    }
                }
            } catch(Exception e) {
                JSONObject addr = getOrLoadBuyerAddress(session,conn);
                if(addr != null) {
                    session.setAttribute("buyerAddressJson", addr.toString());
                }
            }

            JsonObject ok = new JsonObject();
            ok.addProperty("status", "ok");
            ok.addProperty("itemsStored", items.size());//enni items store chesaro telisipotundi
            System.out.println("itemsStoredSuccessfullyInTheCart");
            try (PrintWriter out = response.getWriter()) {
                out.print(ok);
            }
            return;
        }

        if(purpose != null && purpose.equalsIgnoreCase("placeorder")) {//placeorder button click chesinappudu payload, order status, orderid ni matrame handle chesthunnam database lo update cheyyadamledu
            // Expect a JSON body with: items[], deliveryAddress{}, paymentMethod, totals{}, orderStatus
            JsonObject payload = gson.fromJson(body, JsonObject.class);
            JsonArray items = payload != null && payload.has("items") && payload.get("items").isJsonArray()
                    ? payload.getAsJsonArray("items")
                    : new JsonArray();

            
            System.out.println("items after order placed :"+items);
            //System.out.println("items string"+items.getAsString());
            //System.out.println("items.toString"+items.toString());
            //System.out.println(""+items.asList());
            
            
            
            if(items.size() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject err = new JsonObject();
                err.addProperty("error", "Cart is empty.");
                try (PrintWriter out = response.getWriter()) {
                    out.print(err);
                }
                return;
            }

            // Minimal validation for safety + numeric validation
            BigDecimal subtotal = BigDecimal.ZERO;
            String buyerid = (String) session.getAttribute("userid");
            Instant timestamp = Instant.now();
            String orderId = "";
            if(buyerid.length() > 11) {//as the manually entered rows in the database for testing can have buyerid less than 11 characters
            	orderId = buyerid.substring(0, 11)+timestamp+items.size();
            }else {
            	orderId = buyerid+timestamp+items.size();
            }
            
            System.out.println("orderid :"+orderId);
            String buyeraddress = getOrLoadBuyerAddress(session,conn).optString("street", "");//temporarily getting only street address of the buyer from the address json object as the frontend is not sending any delivery address
            System.out.println("buyeraddress :"+buyeraddress);
            if(buyeraddress.startsWith("\"") && buyeraddress.endsWith("\"")) {
				buyeraddress = buyeraddress.replaceAll("\"","\\");
				System.out.println("buyeraddress after removing double quotes:"+buyerid);
			}
            
            //String selleraddress = "";
            String sellerid = "";
            try{
            	PreparedStatement pstmt = conn.prepareStatement("select industryaddress from buyerdetails where buyerid = ?");
            	pstmt.setString(1, buyerid);
            	ResultSet rs = pstmt.executeQuery();
            	
            }catch(SQLException sqle) {
            	sqle.printStackTrace();
            }
            
            int i = 0;
            JsonArray insufficientItems = new JsonArray();
            for (JsonElement el : items) {
                if (!el.isJsonObject()) continue;
                JsonObject it = el.getAsJsonObject();
                System.out.println("item while iterating:" + it);

                JsonArray sellersArray = it.get("sellers").getAsJsonArray();
                for (JsonElement seller : sellersArray) {
                    JsonObject sellerObj = seller.getAsJsonObject();
                    String sellerId = sellerObj.get("sellerId").getAsString();
                    System.out.println("sellerId: " + sellerId);

                    try {
                        PreparedStatement pstmt1 = conn.prepareStatement("select farmeraddress from farmerdetails where farmerid = ?");
                        pstmt1.setString(1, sellerId);
                        ResultSet rs1 = pstmt1.executeQuery();
                        rs1.next();

                        PreparedStatement pstmt2 = conn.prepareStatement("select noofunitsavailable from farmercropjunction where farmerid = ? and cropid = ?");
                        pstmt2.setString(1, sellerId);
                        pstmt2.setString(2, it.get("product").getAsString());
                        ResultSet rs2 = pstmt2.executeQuery();

                        if (rs2.next() && rs2.getInt("noofunitsavailable") < sellerObj.get("quantity").getAsInt()) {
                            // Insufficient quantity, add to insufficientItems list
                            JsonObject insufficientItem = new JsonObject();
                            insufficientItem.addProperty("product", it.get("product").getAsString());
                            insufficientItem.addProperty("requestedQuantity", sellerObj.get("quantity").getAsInt());
                            insufficientItem.addProperty("availableQuantity", rs2.getInt("noofunitsavailable"));
                            insufficientItems.add(insufficientItem);
                            continue;
                        }

                        String sql = "INSERT INTO orderhistory (orderid, sellerid, buyerid, cropid, noofunits, unitprice, orderworth, fromaddress, toaddress, status, Date, paymentstatus) VALUES(?,?,?,?,?,?,?,?,?,?,now(),?)";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1, orderId);
                        ps.setString(2, sellerId);
                        ps.setString(3, buyerid);
                        ps.setString(4, it.get("product").getAsString());
                        double q = sellerObj.get("quantity").getAsDouble();
                        ps.setDouble(5, q);
                        double p = sellerObj.get("unitPrice").getAsDouble();
                        ps.setDouble(6, p);
                        ps.setDouble(7, q * p);
                        ps.setString(8, rs1.getString("farmeraddress"));
                        ps.setString(9, buyeraddress);
                        ps.setString(10, "accepted");
                        ps.setString(11, "pending");

                        ps.executeUpdate();
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                    }
                }
            }

            // Compute server-side totals to prevent tampering
            BigDecimal platformFee = subtotal.multiply(BigDecimal.valueOf(0.01));
            BigDecimal gst = subtotal.multiply(BigDecimal.valueOf(0.18));
            BigDecimal shipping = BigDecimal.valueOf(500); // flat 500 rupees
            BigDecimal total = subtotal.add(platformFee).add(gst).add(shipping);

            // Save delivery address snapshot into session for record; fallback to DB if missing
            try {
                if(payload != null && payload.has("deliveryAddress") && payload.get("deliveryAddress").isJsonObject()) {
                    session.setAttribute("buyerAddressJson", new JSONObject(payload.getAsJsonObject("deliveryAddress").toString()).toString());
                } else {
                    JSONObject addr = getOrLoadBuyerAddress(session,conn);
                    if(addr != null) {
                        session.setAttribute("buyerAddressJson", addr.toString());
                    }
                }
            } catch(Exception ignore) {
                JSONObject addr = getOrLoadBuyerAddress(session,conn);
                if(addr != null) {
                    session.setAttribute("buyerAddressJson", addr.toString());
                }
            }

            // Capture order status (if sent by client) so downstream handlers can update inventory
            String orderStatus = "";
            try {
                if(payload != null && payload.has("orderStatus")) {
                    orderStatus = String.valueOf(payload.get("orderStatus").getAsString());
                }
            } catch (Exception ignore) { orderStatus = ""; }

            // In a real app, persist order to DB
            session.setAttribute("lastOrderId", orderId);
            session.setAttribute("lastOrderItemsJson", gson.toJson(items));
            session.setAttribute("lastOrderStatus", orderStatus);
            session.setAttribute("lastOrderServerTotals", String.format("{\"subtotal\":%.2f,\"platformFee\":%.2f,\"gst\":%.2f,\"shipping\":%.2f,\"total\":%.2f}",
                    subtotal.doubleValue(), platformFee.doubleValue(), gst.doubleValue(), shipping.doubleValue(), total.doubleValue()));
            session.removeAttribute("cartItemsJson");
            session.setAttribute("numberofproducts", 0);

            JsonObject ok = new JsonObject();
            ok.addProperty("status", "success");
            ok.addProperty("orderId", orderId);
            if(orderStatus != null && !orderStatus.isBlank()) {
                ok.addProperty("orderStatus", orderStatus);
            }
            JsonObject srvTotals = new JsonObject();
            srvTotals.addProperty("subtotal", subtotal.doubleValue());
            srvTotals.addProperty("platformFee", platformFee.doubleValue());
            srvTotals.addProperty("gst", gst.doubleValue());
            srvTotals.addProperty("shipping", shipping.doubleValue());
            srvTotals.addProperty("total", total.doubleValue());
            ok.add("serverTotals", srvTotals);
            try (PrintWriter out = response.getWriter()) {
                out.print(ok);
            }
            
            //once order is placed successfully we can clear the temporary cart details from the database and move those items into the order history of the respective farmers
            
            return;
        }

        // Fallback: unknown purpose
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonObject err = new JsonObject();
        err.addProperty("error", "Unsupported purpose for POST.");
        try (PrintWriter out = response.getWriter()) {
            out.print(err);
        }
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    // Returns a delivery address JSON object, either from session cache or by loading from DB
    private JSONObject getOrLoadBuyerAddress(HttpSession session, Connection conn) {
        try {
            Object attr = session.getAttribute("buyerAddressJson");
            if (attr instanceof String && !((String) attr).isBlank()) {
                return new JSONObject((String) attr);
            } else if (attr instanceof JSONObject) {
                JSONObject obj = (JSONObject) attr;
                // normalize storage as String
                session.setAttribute("buyerAddressJson", obj.toString());
                return new JSONObject(obj.toString());
            }
        } catch (Exception ignore) { }

        String buyerId = String.valueOf(session.getAttribute("userid"));
        if(buyerId == null || buyerId.equals("null") || buyerId.isBlank()) {
            return new JSONObject();
        } else {
            JSONObject addr = new JSONObject();
            String sql = "SELECT industryname, industryaddress, contactno, emailid FROM buyerdetails WHERE buyerid = ? LIMIT 1";
            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, buyerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        addr.put("industryname", rs.getString("industryname"));
                        addr.put("phone", rs.getString("contactno"));
                        addr.put("street", rs.getString("industryaddress"));
                        addr.put("city", "");
                        addr.put("state", "");
                        addr.put("pincode", "");
                        addr.put("email", rs.getString("emailid"));
                        session.setAttribute("buyerAddressJson", addr.toString());
                    }
                }
            } catch (SQLException e) {
                // ignore; return empty address
            }
            return addr;
        }
    }

    private String safe(String s) { return s == null ? "" : s; }
}
