<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%
response.setCharacterEncoding("UTF-8"); %>
<div
  id="modal"
  class="hidden fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
>
  <div
    class="flex flex-col gap-5 bg-[#1D1D1D] w-[28%] h-[65%] m-10 p-5 rounded-xl"
  >
    <!-- Contenu du Modal -->
    <h1 id="sendModalTitle" class="text-white text-xl text-center"></h1>
    <div class="flex flex-col gap-2">
      <p class="text-white">N° de l'envoyeur</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <p
          id="senderPhone"
          placeholder="Choisissez un envoyeur"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[78%]"
        ></p>
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p
          class="text-white cursor-pointer hover:bg-gray-700 w-[16%] px-1 rounded-md"
          id="chooseSender"
        >
          Choisir
        </p>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">N° du récepteur</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <p
          id="receiverPhone"
          placeholder="Choisissez un récepteur"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[78%]"
        ></p>
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p
          class="text-white cursor-pointer hover:bg-gray-700 w-[16%] px-1 rounded-md"
          id="chooseReceiver"
        >
          Choisir
        </p>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Montant à envoyer</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <input
          id="amount"
          type="number"
          placeholder="200"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[78%]"
        />
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p class="text-white" id="currency"></p>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Raison</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <input
          id="reason"
          type="text"
          placeholder="Saisir la raison"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[100%]"
        />
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
        id="sendBtn"
        class="text-white bg-[#0d0e0d] hover:bg-[#060706] transition-colors duration-300 rounded-md px-3 py-1"
      ></button>
    </div>
  </div>
</div>

<!-- Overlay Loader -->
<div
  id="sendLoaderOverlay"
  class="fixed inset-0 bg-black bg-opacity-50 z-50 hidden flex items-center justify-center"
>
  <div
    class="loader-spinner w-16 h-16 border-4 border-white border-t-transparent rounded-full animate-spin"
  ></div>
</div>
