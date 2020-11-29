<%--
  Created by IntelliJ IDEA.
  User: leetaylor
  Date: 04/11/2020
  Time: 19:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
</head>
<body>
<h1>Admin</h1>

<p> Logged in as Admin-<%= session.getAttribute("username") %><br>
    Welcome <%= session.getAttribute("firstname") %> <%= session.getAttribute("lastname") %>! </p>

<div> <%if (request.getAttribute("data") != null) {
    out.print(request.getAttribute("data")); } %> </div>

<h2>User</h2>

<form action="GetAllData" method="post">
    <input type="submit" value="Get All Data">
</form>

<h2>Lottery Numbers</h2>

<div> <%if (request.getAttribute("winningNumbersTable") != null) {
    out.print(request.getAttribute("winningNumbersTable")); } %> </div><br>

<form action="GetWinningNumbers" method="post">
    <input type="submit" value="Get Winning Numbers">
</form>

<form action="RollWinningNumbers" method="post">
    <input type="submit" value="Roll Lottery Numbers">
</form>

<form action="Logout" method="post">
    <input type="submit" value="Logout">
</form>

<script>
    window.onload = onLoad;

    /**
     * Prevent path traversal attacks.
     * */
    function onLoad(){
        <% if (session.getAttribute("admin") == null ||
         !(boolean)session.getAttribute("admin")){
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("error.jsp");
            request.setAttribute("message", "You do not have permission to view this page.");
            dispatcher.forward(request, response);
        }%>
    }
</script>

</body>
</html>
