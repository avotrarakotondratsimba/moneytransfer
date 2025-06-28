<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%
response.setCharacterEncoding("UTF-8"); %>
<div
  id="export-pdf-modal"
  class="hidden fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
>
  <div
    class="flex flex-col gap-10 bg-[#1D1D1D] w-[23%] h-[50%] m-10 p-5 rounded-xl"
  >
    <!-- Contenu du Modal -->
    <h1 id="exportPdfModalTitle" class="text-white text-xl text-center">
      Génération du PDF
    </h1>
    <div class="flex flex-col gap-2">
      <p class="text-white">N° de l'envoyeur</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <p
          id="senderPhoneNumber"
          placeholder="Choisissez l'envoyeur"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[78%]"
        ></p>
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p
          class="text-white cursor-pointer hover:bg-gray-700 w-[20%] px-1 rounded-md"
          id="chooseSenderPhone"
        >
          Choisir
        </p>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Date</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <select
          id="monthSelect"
          class="bg-transparent text-white focus:outline-none w-[100%]"
        >
          <!-- <option value="1" class="bg-[#1A1A1A] text-white">Janvier</option>
          <option value="2" class="bg-[#1A1A1A] text-white">
            Février
          </option> -->
        </select>
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p class="text-white w-[16%] px-1 rounded-md" id="year">2025</p>
      </div>
    </div>

    <!-- Boutons de contrôle -->
    <div class="flex gap-3 justify-end">
      <button
        id="closePdfModal"
        class="text-white bg-[#3A3C3B] hover:bg-[#323332] transition-colors duration-300 rounded-md px-3 py-1"
      >
        Annuler
      </button>
      <button
        id="generatePdfButton"
        class="text-white bg-[#0d0e0d] hover:bg-[#060706] transition-colors duration-300 rounded-md px-3 py-1"
      >
        Générer
      </button>
    </div>
  </div>
</div>
