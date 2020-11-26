<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
    <body>
    <h1>Error Page</h1>

    <p><%= request.getAttribute("message") %></p>

    <a href="index.jsp">Home Page</a>

    <script>
        window.onload = onLoad;
        const session = sessionStorage;

        function onLoad() {
            if (session.getItem("attemptedLogin") === true) {
                let x = parseInt(session.getItem("failedLogs"))
                x = x + 1
                session.setItem("failedLogs", String(x))
            }
        }

    </script>

    <script>


    </script>

    </body>
</html>
