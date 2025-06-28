<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%
response.setCharacterEncoding("UTF-8"); %>
<div
  id="modal"
  class="hidden fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
>
  <div
    class="flex flex-col gap-5 bg-[#1D1D1D] w-[28%] h-[85%] m-10 p-5 rounded-xl"
  >
    <!-- Contenu du Modal -->
    <h1 id="clientModalTitle" class="text-white text-xl text-center"></h1>
    <div class="flex flex-col gap-2">
      <p class="text-white">Nom</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <input
          id="clientName"
          type="text"
          placeholder="Saisir le nom"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[100%]"
        />
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Sexe</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <select
          id="genderSelect"
          class="bg-transparent text-white focus:outline-none w-[100%]"
        >
          <option value="male" class="bg-[#1A1A1A] text-white">Masculin</option>
          <option value="female" class="bg-[#1A1A1A] text-white">
            Féminin
          </option>
        </select>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Pays</p>
      <div
        class="flex items-center bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <select
          id="countrySelect"
          class="bg-transparent text-white focus:outline-none w-[100%]"
        ></select>
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Téléphone</p>
      <div
        class="flex items-center bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <p class="text-white" id="phoneCode"></p>
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <input
          id="phoneNumber"
          type="number"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[90%]"
        />
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Email</p>
      <div
        class="flex items-center bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <input
          id="email"
          type="email"
          placeholder="votrenom@domaine.extension"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[100%]"
        />
      </div>
    </div>

    <div class="flex flex-col gap-2">
      <p class="text-white">Solde</p>
      <div
        class="flex bg-[#1A1A1A] border border-gray-600 rounded-md px-3 py-2 w-[90%]"
      >
        <input
          id="balance"
          type="number"
          placeholder="200"
          class="bg-transparent text-white w-1/2 focus:outline-none w-[78%]"
        />
        <div class="border-l border-gray-600 h-5 mx-3"></div>
        <p class="text-white" id="currency"></p>
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
        id="addClientBtn"
        class="text-white bg-[#0d0e0d] hover:bg-[#060706] transition-colors duration-300 rounded-md px-3 py-1"
      ></button>
    </div>
  </div>
</div>
