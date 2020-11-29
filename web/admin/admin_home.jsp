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

<%-- Display login info --%>
<p> Logged in as: <%= session.getAttribute("username") %> [Admin] <br>
    Welcome <%= session.getAttribute("firstname") %> <%= session.getAttribute("lastname") %>! </p>

<%-- Used to display data in a table --%>
<div> <%if (request.getAttribute("data") != null) {
    out.print(request.getAttribute("data")); } %> </div>

<h2>Admin Features</h2>
<%-- Get table of user account data --%>
<form action="GetAllData" method="post">
    <input type="submit" value="Get All Data">
</form>

<%-- Used to display winning numbers data in a table --%>
<div> <%if (request.getAttribute("winningNumbersTable") != null) {
    out.print(request.getAttribute("winningNumbersTable")); } %> </div><br>

<%-- Get table of winning numbers --%>
<form action="GetWinningNumbers" method="post">
    <input type="submit" value="Get Winning Numbers">
</form>

<%-- Roll a new set of winning numbers --%>
<form action="RollWinningNumbers" method="post">
    <input type="submit" value="Roll Lottery Numbers">
</form>

<%-- Logging removes all attributes in session and directs to index.jsp --%>
<form action="Logout" method="post">
    <input type="submit" value="Logout">
</form>

</body>
</html>
