<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%
response.setCharacterEncoding("UTF-8"); %> <%-- <%@ page import="java.util.List"
%> <%@ page import="org.example.model.ExchangeRate" %> --%>

<h1 class="text-[25px] text-white mb-5 font-bold">
  Information des taux de change
</h1>
<div data-script="exchange-rate.js" class="flex justify-between w-full h-[90%]">
  <div class="flex flex-col gap-5 bg-[#1D1D1D] w-[75%] h-[100%] p-5 rounded-xl">
    <div class="flex justify-between">
      <div class="relative w-[20%]">
        <input
          id="searchInput"
          type="text"
          placeholder="Rechercher"
          class="w-full px-3 py-1 pr-8 rounded-xl bg-[#2A2A2A] text-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-700"
        />
        <span
          class="absolute right-1.5 top-1.5 text-gray-400 pointer-events-none"
        >
          <i class="material-icons">search</i>
        </span>
      </div>

      <button
        id="newButton"
        class="flex gap-2 text-white bg-purple-500 hover:bg-purple-600 rounded-md px-3 py-1"
      >
        <i class="material-icons">add</i>
        Nouveau
      </button>
    </div>

    <div class="overflow-hidden rounded-xl border border-gray-700 h-[90%]">
      <table class="w-full table-auto border-collapse text-white">
        <thead class="bg-[#2A2A2A]">
          <tr>
            <th class="px-4 py-2 text-center">ID</th>
            <th class="px-4 py-2 text-center">Montant de départ</th>
            <th class="px-4 py-2 text-center">Devise de départ</th>
            <th class="px-4 py-2 text-center">Montant d'arrivée</th>
            <th class="px-4 py-2 text-center">Devise d'arrivée</th>
            <th class="px-4 py-2 text-center">Dernière modification</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="exchange-rate-body" class="divide-y divide-gray-700"></tbody>
      </table>
    </div>
  </div>

  <div class="flex flex-col w-[23%] justify-between">
    <div
      class="flex flex-col justify-between bg-[#1D1D1D] w-[100%] h-[40%] p-5 rounded-xl"
    >
      <h1 class="text-white text-xl text-center">Conversion</h1>
      <div class="flex flex-col gap-3">
        <p class="text-white">Montant de départ</p>
        <div
          class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[250px]"
        >
          <input
            type="number"
            placeholder="1"
            min="1"
            id="conversionSourceAmountInput"
            class="bg-transparent text-white w-1/2 focus:outline-none"
          />
          <div class="border-l border-gray-600 h-5 mx-3"></div>
          <select
            id="conversionSourceCurrencySelect"
            class="bg-transparent text-white focus:outline-none"
          ></select>
        </div>
      </div>

      <div class="flex flex-col gap-3">
        <p class="text-white">Montant d'arrivé</p>
        <div
          class="flex items-center bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[250px]"
        >
          <input
            type="text"
            placeholder="5000"
            readonly
            id="conversionTargetAmount"
            class="bg-transparent text-white w-1/2 focus:outline-none"
          />
          <div class="border-l border-gray-600 h-5 mx-3"></div>
          <p class="text-white">MGA</p>
        </div>
      </div>
    </div>

    <div class="bg-[#1D1D1D] h-[55%] rounded-xl p-4">
      <!-- <canvas id="topCurrenciesChart" width="100%" height="100%"></canvas> -->
      <h1 class="text-white text-xl text-center">Historique des conversions</h1>
      <div id="conversionHistory" class="p-4 overflow-auto text-white text-sm">
        <p class="text-gray-400 italic">Aucune conversion récente</p>
      </div>
    </div>
  </div>
</div>

<!-- Modal -->
<jsp:include page="/modal/exchange-rate-modal.jsp" />

<%--
<script src="/js/exchange-rate.js"></script>
--%>
