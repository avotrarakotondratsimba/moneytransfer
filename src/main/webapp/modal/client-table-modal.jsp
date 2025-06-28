<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%
response.setCharacterEncoding("UTF-8"); %>
<div
  id="client-table-modal"
  class="hidden fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
>
  <div class="flex flex-col gap-5 bg-[#1D1D1D] w-[75%] m-10 p-5 rounded-xl">
    <!-- Contenu du Modal -->
    <h1 id="clientTabledModalTitle" class="text-white text-xl text-center"></h1>
    <div class="overflow-hidden rounded-xl border border-gray-700 h-[90%]">
      <table class="w-full table-auto border-collapse text-white">
        <thead class="bg-[#2A2A2A]">
          <tr>
            <th></th>
            <th class="px-4 py-2 text-center">Téléphone</th>
            <th class="px-4 py-2 text-center">Nom</th>
            <th class="px-4 py-2 text-center">Sexe</th>
            <th class="px-4 py-2 text-center">Pays</th>
            <th class="px-4 py-2 text-center">Solde</th>
            <th class="px-4 py-2 text-center">Mail</th>
          </tr>
        </thead>
        <tbody id="client-table-body" class="divide-y divide-gray-700"></tbody>
      </table>
    </div>

    <!-- Boutons de contrôle -->
    <div class="flex gap-3 justify-end">
      <button
        id="closeClientModal"
        class="text-white bg-[#3A3C3B] hover:bg-[#323332] transition-colors duration-300 rounded-md px-3 py-1"
      >
        Annuler
      </button>
    </div>
  </div>
</div>
