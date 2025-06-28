<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ page
contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Money Transfer</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link
      href="https://fonts.googleapis.com/icon?family=Material+Icons"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css"
    />
    <link rel="icon" type="image/png" href="Images/Logo.png" />
  </head>
  <body class="flex bg-[#0A0A0A]">
    <jsp:include page="includes/sidebar.jsp" />

    <main id="main-content" class="w-full mx-10 my-5">
      <%-- <jsp:include page="pages/exchange-rate-page.jsp" /> --%>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
  </body>
  <script src="js/route.js"></script>
</html>
