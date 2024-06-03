<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Errore</title>
</head>
<body>
    <h1>Si è verificato un errore</h1>
    <p>Ci scusiamo per il disagio, ma si è verificato un errore. Si prega di riprovare più tardi.</p>
    <p><a href="<%= request.getContextPath() %>/index.jsp">Torna alla Home</a></p>
</body>
</html>
