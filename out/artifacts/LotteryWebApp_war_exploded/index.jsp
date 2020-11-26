<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Home</title>
  </head>
  <body>

  <h1>Home Page</h1>

  <h2>Login</h2>
  <form action="UserLogin" method="post" onsubmit="return loginAttempts();">
      <%-- Login username field--%>
      <label for="loginUsername">Username:</label><br>
      <input type="text" id="loginUsername" name="loginUsername" maxlength="50" required><br>
      <%-- Login password field--%>
      <label for="loginPassword">Password:</label><br>
      <input type="password" id="loginPassword" name="loginPassword" maxlength="50" required><br><br>
      <%-- Submit button--%>
      <input type="submit" id="login" value="Login"><br>
  </form>

  <h2>Register</h2>
  <form action="CreateAccount" method="post" id="register" onsubmit="return registerChecks();">
      <%-- Firstname field --%>
      <label for="firstname">First name:</label><br>
      <input type="text" id="firstname" name="firstname" maxlength="50" required><br>
      <%-- Lastname field --%>
      <label for="lastname">Last name:</label><br>
      <input type="text" id="lastname" name="lastname" maxlength="50" required><br>
      <%-- Username field --%>
      <label for="username">Username:</label><br>
      <input type="text" id="username" name="username" maxlength="50" required><br>
      <%-- Phone number field --%>
      <label for="phone">Phone Number:</label><br>
      <input type="text" id="phone" name="phone" maxlength="50" required><br>
      <%-- Email field --%>
      <label for="email">Email:</label><br>
      <input type="email" id="email" name="email" maxlength="50" required><br>
      <%-- Password field --%>
      <label for="password">Password:</label><br>
      <input type="password" id="password" name="password" maxlength="50" required><br>
      <%-- Admin field --%>
      <label for="admin">Admin:</label>
      <input type="checkbox" id="admin" name="admin"><br><br>
      <%-- Submit button --%>
      <input id="submit" type="submit" value="Register">
  </form>

  <script>
      window.onload = onLoad;
      const register = document.getElementById("register");
      const phoneNo = document.getElementById("phone");
      const pwd = document.getElementById("password");
      const submit = document.getElementById("submit");
      // WIP & USED IN DEBUG -> loginbtn
      const loginbtn = document.getElementById("login");
      const session = sessionStorage

      function onLoad(){
          if (session.getItem("failedLogs") === null){
              // alert("Set failedLogs to 0")  // Debug info
              session.setItem("failedLogs", "0")
          }
          // alert("failedLogs = " + session.getItem("failedLogs"))  // Debug info
          if (session.getItem("failedLogs") === "3"){
              alert("Maximum failed login attempts exceeded!")
              loginbtn.disabled = true;
          }
      }

      function loginAttempts() {
          if (session.getItem("failedLogs") === "3"){
              return false;
          } else {
              return true;
          }
      }

      function hasNumber(string) {
          // Returns true if string contains digits otherwise false
          return /\d/.test(string);
      }

      function hasLower(string){
          // Returns true if string contains lower case letter otherwise false
          return /[a-z]/.test(string);
      }

      function hasUpper(string){
          // Returns true if string contains upper case letter otherwise false
          return /[A-Z]/.test(string);
      }

      /**
       * Checks registration input fields. If this returns true the form data is submitted.
       * If a check fails then this returns false in which the form is not submitted.
       * */
      function registerChecks(){
          // Checks if phone number contains letters
          if (hasLower(phoneNo.value) || hasUpper(phoneNo.value)) {
              alert("Phone number should only contain numbers!")
              return false;
          } // Checks length of phone number and if phone number contains dashes in valid indexes.
          else if (phoneNo.value.length !== 15 || phoneNo.value.charAt(2) !== '-' || phoneNo.value.charAt(7) !== '-'){
              alert("Phone number should contain 13 digits!\nFormat is XX-XXXX-XXXXXXX include dashes!" +
                  "\nPhone number should only contain numbers!")
              return false;
          }
          // Password checks below checks for length between 8 & 15
          if (pwd.value.length >= 15 || pwd.value.length <= 8){
              alert("Password length must be between 8 and 15 characters!")
              return false;
          }
          // Checks password to ensure a digit, lower and uppercase character is included
          if (!(hasUpper(pwd.value) && hasLower(pwd.value) && hasNumber(pwd.value))){
              alert("Password must contain 1 digit, upper & lowercase character!")
              return false;
          }
          // If all checks pass then the form is submitted
          return true;
      }

  </script>

  </body>
</html>
