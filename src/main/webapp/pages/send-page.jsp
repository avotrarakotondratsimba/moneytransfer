<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1 class="text-[25px] text-white mb-5 font-bold">Envoi d'argent</h1>

<div data-script="send.js" class="w-full h-[90%]">
  <div class="flex flex-col gap-5 bg-[#1D1D1D] w-full h-[100%] p-5 rounded-xl">
    <div class="flex justify-between items-center gap-3">
      <div class="flex gap-5 items-center w-[75%]">
        <div class="relative w-[25%]">
          <input
            id="searchInput"
            type="text"
            placeholder="Rechercher"
            class="w-full px-3 py-2 pr-10 rounded-xl bg-[#2A2A2A] text-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-700"
          />
          <span
            class="absolute right-3 top-2.5 text-gray-400 pointer-events-none"
          >
            <i class="material-icons">search</i>
          </span>
        </div>

        <div
          class="flex w-[35%] px-3 py-1 rounded-xl bg-[#2A2A2A] justify-between"
        >
          <div class="relative w-[40%]">
            <input
              id="startDate"
              placeholder="Date début"
              readonly
              class="w-full px-3 py-1 pr-10 text-gray-300 focus:outline-none bg-[#2A2A2A] rounded-xl cursor-pointer"
            />
            <span
              class="absolute right-2 top-1 text-gray-400 pointer-events-none"
            >
              <i class="material-icons text-lg">calendar_today</i>
            </span>
          </div>

          <div class="border-l border-gray-600 h-8 mx-3"></div>

          <div class="relative w-[40%]">
            <input
              id="endDate"
              placeholder="Date fin"
              readonly
              class="w-full px-3 py-1 pr-10 text-gray-300 focus:outline-none bg-[#2A2A2A] rounded-xl cursor-pointer"
            />
            <span
              class="absolute right-2 top-1 text-gray-400 pointer-events-none"
            >
              <i class="material-icons text-lg">calendar_today</i>
            </span>
          </div>
        </div>
      </div>
      <div class="flex gap-2">
        <button
          id="newButton"
          class="flex gap-2 text-white bg-purple-500 hover:bg-purple-600 rounded-md px-3 py-1"
        >
          <i class="material-icons">add</i>
          Nouveau
        </button>

        <button
          id="exportButton"
          class="flex items-center gap-2 text-white bg-blue-500 hover:bg-blue-600 rounded-md px-3 py-1"
        >
          <i class="material-icons">file_download</i>
          Export
        </button>
      </div>
    </div>

    <div class="overflow-hidden rounded-xl border border-gray-700 h-[90%]">
      <table class="w-full table-auto border-collapse text-white">
        <thead class="bg-[#2A2A2A]">
          <tr>
            <th class="px-4 py-2 text-center">Envoyeur</th>
            <th class="px-4 py-2 text-center">Récepteur</th>
            <th class="px-4 py-2 text-center">Montant</th>
            <th class="px-4 py-2 text-center">Date d'envoi</th>
            <th class="px-4 py-2 text-center">Raison</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="send-body" class="divide-y divide-gray-700"></tbody>
      </table>
    </div>
  </div>
</div>

<!-- Modal d'envoi d'argent-->
<jsp:include page="/modal/send-modal.jsp" />

<!-- Modal de l'exportation PDF -->
<jsp:include page="/modal/export-pdf-modal.jsp" />

<!-- Modal de sélection des clients-->
<jsp:include page="/modal/client-table-modal.jsp" />
