<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1 class="text-[25px] text-white mb-5 font-bold">Dashboard</h1>

<div
  data-script="dashboard.js"
  class="flex flex-col w-full h-[90%] justify-between"
>
  <div class="flex justify-between w-full h-[20%]">
    <div
      class="flex bg-[#1D1D1D] w-[23%] rounded-lg items-center justify-center gap-5"
    >
      <div
        class="bg-[#1A2551] w-16 h-16 rounded-full flex items-center justify-center"
      >
        <span class="material-icons text-[#0D3DA0] text-4xl">attach_money</span>
      </div>
      <div class="flex flex-col gap-2">
        <p id="monthlyRevenue" class="text-white text-xl font-semibold">
          0 MGA
        </p>
        <p class="text-gray-500 text-base">Revenu mensuel</p>
      </div>
    </div>

    <div
      class="flex bg-[#1D1D1D] w-[23%] rounded-lg items-center justify-center gap-5"
    >
      <div
        class="bg-green-900/30 w-16 h-16 rounded-full flex items-center justify-center"
      >
        <span class="material-icons text-green-600 text-4xl"
          >account_balance</span
        >
      </div>
      <div class="flex flex-col gap-2">
        <p id="annualRevenue" class="text-white text-xl font-semibold">0 MGA</p>
        <p class="text-gray-500 text-base">Revenu annuel</p>
      </div>
    </div>

    <div
      class="flex bg-[#1D1D1D] w-[23%] rounded-lg items-center justify-center gap-5"
    >
      <div
        class="bg-purple-900/30 w-16 h-16 rounded-full flex items-center justify-center"
      >
        <span class="material-icons text-purple-600 text-4xl">speed</span>
      </div>
      <div class="flex flex-col gap-2">
        <p id="monthlyOperations" class="text-white text-xl font-semibold">0</p>
        <p class="text-gray-500 text-base">Opération mensuelle</p>
      </div>
    </div>

    <div
      class="flex bg-[#1D1D1D] w-[23%] rounded-lg items-center justify-center gap-5"
    >
      <div
        class="bg-red-900/30 w-16 h-16 rounded-full flex items-center justify-center"
      >
        <span class="material-icons text-red-600 text-4xl">timeline</span>
      </div>
      <div class="flex flex-col gap-2">
        <p id="annualOperations" class="text-white text-xl font-semibold">0</p>
        <p class="text-gray-500 text-base">Opération annuelle</p>
      </div>
    </div>
  </div>
  <div class="flex w-full h-[76%] justify-between">
    <!-- <div class="w-[48.666%] h-[100%] bg-[#1D1D1D] rounded-lg"></div> -->
    <div class="w-[48.666%] h-[100%] bg-[#1D1D1D] rounded-lg p-5">
      <p class="text-white text-lg font-semibold mb-2">
        Statistique des revenus mensuels
      </p>
      <canvas id="revenueChart" class="w-full h-full p-5"></canvas>
    </div>

    <div class="w-[48.666%] h-[100%] bg-[#1D1D1D] rounded-lg p-5">
      <p class="text-white text-lg font-semibold mb-2">
        Statistique des opérations mensuelles
      </p>
      <canvas id="operationChart" class="w-full h-full p-5"></canvas>
    </div>
  </div>
</div>
