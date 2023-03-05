<%@page pageEncoding="UTF-8"%><%
	/**
	 * @author	P-C Lin (a.k.a 高科技黑手)
	 */
	String location = request.getHeader("referer");
	if (location == null) {
		location = application.getContextPath();
	}
	response.sendRedirect(location);
%>