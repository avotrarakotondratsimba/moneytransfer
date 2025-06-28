<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%
response.setCharacterEncoding("UTF-8"); %>
<div
  id="modal"
  class="hidden fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
>
  <div
    class="flex flex-col gap-5 bg-[#1D1D1D] w-[20%] h-[65%] m-10 p-5 rounded-xl"
  >
    <!-- Contenu du Modal -->
    <h1 id="transferFeeModalTitle" class="text-white text-xl text-center"></h1>
    <div class="flex flex-col gap-2">
      <p class="text-white">Devise</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[250px]"
      >
        <select
          id="currencyNameSelect"
          class="bg-transparent text-white focus:outline-none w-[100%]"
        ></select>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Montant minimum</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[250px]"
      >
        <input
          id="minAmount"
          type="number"
          placeholder="1"
          min="1"
          class="bg-transparent text-white w-1/2 focus:outline-none"
        />
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p class="text-white currency-selected"></p>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Montant maximum</p>
      <div
        class="flex items-center bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[250px]"
      >
        <input
          id="maxAmount"
          type="number"
          placeholder="5000"
          min="1"
          class="bg-transparent text-white w-1/2 focus:outline-none"
        />
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p class="text-white currency-selected"></p>
      </div>

      <div class="flex flex-col gap-2 mb-[10%]">
        <p class="text-white">Frais</p>
        <div
          class="flex items-center bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[250px]"
        >
          <input
            id="feeAmount"
            type="number"
            placeholder="5000"
            min="1"
            class="bg-transparent text-white w-1/2 focus:outline-none"
          />
          <div class="border-l border-gray-600 h-5 mx-3"></div>
          <p class="text-white currency-selected"></p>
        </div>
      </div>

      <!-- Boutons de contrôle -->
      <div class="flex gap-3 justify-end">
        <button
          id="closeModal"
          class="text-white bg-[#3A3C3B] hover:bg-[#323332] transition-colors duration-300 rounded-md px-3 py-1"
        >
          Annuler
        </button>
        <button
          id="addTransferFeeBtn"
          class="text-white bg-[#0d0e0d] hover:bg-[#060706] transition-colors duration-300 rounded-md px-3 py-1"
        ></button>
      </div>
    </div>
  </div>
</div>
