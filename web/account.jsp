<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
</head>
<body>
<h1>User Account</h1>

<p> <%= session.getAttribute("message") %></p>
<p> Welcome <%= session.getAttribute("firstname") %> <%= session.getAttribute("lastname") %>!
    Logged in as: <%= session.getAttribute("username") %> </p>
<p> Email: <%= session.getAttribute("email") %> </p>

<h2>Lottery Numbers</h2>
<p>Input 6 numbers between 0 - 60</p>
<form action="AddUserNumbers" method="post">
    <input type="number" id="slot1" name="slot1" min="0" max="60" style="width: 45px;" required/>
    <input type="number" id="slot2" name="slot2" min="0" max="60" style="width: 45px;" required/>
    <input type="number" id="slot3" name="slot3" min="0" max="60" style="width: 45px;" required/>
    <input type="number" id="slot4" name="slot4" min="0" max="60" style="width: 45px;" required/>
    <input type="number" id="slot5" name="slot5" min="0" max="60" style="width: 45px;" required/>
    <input type="number" id="slot6" name="slot6" min="0" max="60" style="width: 45px;" required/><br><br>
    <input type="button" id="luckydip" value="Lucky Dip!" onclick="getRandomInt();" /><br><br>
    <input type="submit" value="Add User Numbers">
</form>

<form action="GetUserNumbers" method="post">
    <input type="submit" id="Get Draws" value="Get Draws">
</form>

<form action="CheckWin" method="post">
    <input type="submit" id="CheckWin" value="Check Your Pickings">
</form>

<div> <%if (session.getAttribute("win") != null) {
    out.print(session.getAttribute("winMessage")); } %> </div><br>

<form action="Logout" method="post">
    <input type="submit" value="Logout">
</form>

<div> <%if (request.getAttribute("draws") != null) {
    ArrayList<String> draws = (ArrayList<String>)request.getAttribute("draws");
    String content = "<table border='1' cellspacing='2' cellpadding='2' width='20%' align='left'>" +
            "<tr><th>Draws</th></tr>";
    for (String s : draws){
        content += "<tr><td>" + s + "</td></tr>";
    }
    content += "</table>";
    out.print(content);
} %> </div><br>

<script>
    window.onload = onLoad;
    const s1 = document.getElementById("slot1")
    const s2 = document.getElementById("slot2")
    const s3 = document.getElementById("slot3")
    const s4 = document.getElementById("slot4")
    const s5 = document.getElementById("slot5")
    const s6 = document.getElementById("slot6")

    function onLoad(){
        session.setItem("failedLogs", "0")
    }

    function duplicate(x, arr){
        for (let i = 0; i < arr.length; i++){
            if (arr[i] === x){
                return true;
            }
        } return false;
    }

    function getRandomInt(){
        // Init. array of size 6
        let addedInts = [-1, -1, -1, -1, -1, -1];
        let byteArray = new Uint8Array(1);
        window.crypto.getRandomValues(byteArray);

        const max = 61;
        for (let i = 0; i < 6; i++){
            while (byteArray[0] > max || duplicate(byteArray[0] % max, addedInts)) {
                window.crypto.getRandomValues(byteArray);
            }
            addedInts[i] = byteArray[0] % max; // int mod max to include 0
            byteArray[0] = window.crypto.getRandomValues(byteArray);
        }
        s1.value = addedInts[0];
        s2.value = addedInts[1];
        s3.value = addedInts[2];
        s4.value = addedInts[3];
        s5.value = addedInts[4];
        s6.value = addedInts[5];
    }
</script>

</body>
</html>
