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
import java.sql.SQLException;

@WebServlet("/cancelorderservlet")
public class CancelOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CancelOrderServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("loginstatus") == null || "died".equals(session.getAttribute("loginstatus"))) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\":\"unauthorized\"}");
			return;
		}

		String fromPage = request.getParameter("from");
		String orderId = request.getParameter("orderid");
		String reason = request.getParameter("reason");

		if (orderId == null || orderId.isEmpty() || fromPage == null || fromPage.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"missing parameters\"}");
			return;
		}

		if ("buyerpage".equalsIgnoreCase(fromPage)) {
			String buyerId = (String) session.getAttribute("userid"); // keep consistent with OrderTrackingServletBuyer
			if (buyerId == null || buyerId.trim().isEmpty()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\":\"unauthorized\"}");
				return;
			}

			try (Connection conn = DatabaseConnectionPool.getConnectionPool();
			     PreparedStatement pstmt = conn.prepareStatement(
			     "UPDATE orderhistory SET status = 'cancelled', reason = ? WHERE buyerid = ? AND orderid = ?")) {
				pstmt.setString(1, reason);
				pstmt.setString(2, buyerId);
				pstmt.setString(3, orderId);
				int updateStatus = pstmt.executeUpdate();
				if (updateStatus > 0) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write("{\"status\":\"ok\"}");
					System.out.println("Order " + orderId + " cancelled by buyer " + buyerId);
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.getWriter().write("{\"error\":\"order not found or not cancellable\"}");
					System.out.println("Failed to cancel order " + orderId + " by buyer " + buyerId);
				}
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				PrintWriter pw = response.getWriter();
				pw.write("{\"error\":\"database error\"}");
			}
			return;
		}

		// Unknown source page
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().write("{\"error\":\"unsupported source\"}");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// For simplicity, forward GET to POST (some clients may call GET accidentally)
		doPost(request, response);
	}
}