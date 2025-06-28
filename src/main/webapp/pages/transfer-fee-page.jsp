<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1 class="text-[25px] text-white mb-5 font-bold">Frais de transfert</h1>

<div data-script="transfer-fee.js" class="flex justify-between w-full h-[90%]">
  <div class="flex flex-col gap-5 bg-[#1D1D1D] w-full h-[100%] p-5 rounded-xl">
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
            <th class="px-4 py-2 text-center">Devise</th>
            <th class="px-4 py-2 text-center">Montant minimum</th>
            <th class="px-4 py-2 text-center">Montant maximum</th>
            <th class="px-4 py-2 text-center">Frais</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="transfer-fee-body" class="divide-y divide-gray-700"></tbody>
      </table>
    </div>
  </div>
</div>

<!-- Modal -->
<jsp:include page="/modal/transfer-fee-modal.jsp" />
