<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<aside class="bg-[#1D1D1D] w-64 h-screen flex flex-col p-5 gap-14">
  <div class="flex gap-2">
    <div
      class="text-white font-semibold text-xl bg-purple-500 rounded-md p-2 items-center justify-center"
    >
      NP
    </div>
    <div>
      <h1 class="text-white font-semibold text-lg">Nova Pay</h1>
      <p class="text-sm text-gray-300">Money Transfer</p>
    </div>
  </div>
  <div class="flex flex-col gap-2 text-white">
    <div
      class="menu-link flex gap-4 px-2 py-3 text-lg text-gray-300 rounded-md hover:bg-[#3A3C3B] transition-colors duration-300 cursor-pointer"
      data-page="dashboard-page.jsp"
    >
      <span class="material-icons pt-0.5">dashboard</span>
      <p>Dashboard</p>
    </div>
    <!-- <div
      class="menu-link flex gap-4 px-2 py-3 text-lg text-gray-300 rounded-md hover:bg-[#3A3C3B] transition-colors duration-300 cursor-pointer"
      data-page="monetary-unit-page.jsp"
    >
      <span class="material-icons pt-0.5">attach_money</span>
      <p>Unité</p>
    </div> -->
    <div
      class="menu-link flex gap-4 px-2 py-3 text-lg text-gray-300 rounded-md hover:bg-[#3A3C3B] transition-colors duration-300 cursor-pointer"
      data-page="exchange-rate-page.jsp"
    >
      <span class="material-icons pt-0.5">swap_horiz</span>
      <p>Taux</p>
    </div>
    <div
      class="menu-link flex gap-4 px-2 py-3 text-lg text-gray-300 rounded-md hover:bg-[#3A3C3B] transition-colors duration-300 cursor-pointer"
      data-page="transfer-fee-page.jsp"
    >
      <span class="material-icons pt-0.5">payment</span>
      <p>Frais</p>
    </div>
    <div
      class="menu-link flex gap-4 px-2 py-3 text-lg text-gray-300 rounded-md hover:bg-[#3A3C3B] transition-colors duration-300 cursor-pointer"
      data-page="client-page.jsp"
    >
      <span class="material-icons pt-0.5">person</span>
      <p>Client</p>
    </div>
    <div
      class="menu-link flex gap-4 px-2 py-3 text-lg text-gray-300 rounded-md hover:bg-[#3A3C3B] transition-colors duration-300 cursor-pointer"
      data-page="send-page.jsp"
    >
      <span class="material-icons pt-0.5">send</span>
      <p>Envoi</p>
    </div>
  </div>
</aside>
