var monetaryUnits = window.monetaryUnits || [];
var conversionMonetaryUnits = window.conversionMonetaryUnits || [];
var editExchangeId = window.editExchangeId || null;

// Charger tous les taux de change
async function loadExchangeRates(search = "") {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/exchange-rates?search=" +
        encodeURIComponent(search),
      {
        headers: {
          "X-Requested-With": "XMLHttpRequest",
        },
      }
    );

    if (!response.ok) {
      throw new Error("Erreur HTTP : " + response.status);
    }

    const data = await response.json(); // <-- C'est ça qu'il manquait
    renderExchangeRates(data);
    // renderChart(data);
  } catch (err) {
    console.error("Erreur lors du chargement des taux :", err);
  }
}

// Afficher les taux dans le tableau
function renderExchangeRates(data) {
  const tbody = document.getElementById("exchange-rate-body");
  tbody.innerHTML = "";

  data.forEach((rate) => {
    const row = document.createElement("tr");
    const encodedRate = encodeURIComponent(JSON.stringify(rate));

    row.classList.add(
      "group",
      "bg-transparent",
      "hover:bg-[#2A2A2A]",
      "transition-colors",
      "duration-100"
    );

    row.innerHTML = `
        <td class="px-4 py-2 text-center">${rate.exchangeId}</td>
        <td class="px-4 py-2 text-center">${rate.baseAmount}</td>
        <td class="px-4 py-2 text-center">${rate.sourceCurrency.code}</td>
        <td class="px-4 py-2 text-center">${Number(
          rate.rateToMGA
        ).toLocaleString("fr-FR")}</td>
        <td class="px-4 py-2 text-center">MGA</td>
        <td class="pl-4 py-2 text-center">${new Date(rate.modificationDate)
          .toLocaleString("fr-FR", {
            dateStyle: "short",
            timeStyle: "short",
            timeZone: "Indian/Antananarivo", // ou "Etc/GMT-3"
          })
          .replace(" ", " à ")}</td>
        <td class="px-2 py-2 text-center">
          <div class="flex gap-2 justify-end opacity-0 group-hover:opacity-100 transition-opacity duration-100">
            <button onclick="openModal(${true}, '${encodedRate}')" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">edit</span>
            </button>
            <button onclick="deleteExchangeRate(${
              rate.exchangeId
            })" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">delete</span>
            </button>
          </div>
        </td>
      `;

    tbody.appendChild(row);
  });
}

function renderChart(exchangeRates) {
  const top3 = exchangeRates
    .sort((a, b) => b.rateToMGA - a.rateToMGA)
    .slice(0, 3);

  const labels = top3.map((rate) => rate.sourceCurrency.code);
  const data = top3.map((rate) => rate.rateToMGA);

  const ctx = document.getElementById("topCurrenciesChart").getContext("2d");

  new Chart(ctx, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Taux en MGA",
          data: data,
          backgroundColor: ["#8b5cf6", "#4f46e5", "#06b6d4"],
          borderRadius: 5,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (context) =>
              `${context.parsed.y.toLocaleString("fr-FR")} MGA`,
          },
        },
      },
      scales: {
        y: {
          ticks: {
            callback: (value) => value.toLocaleString("fr-FR") + " MGA",
          },
        },
      },
    },
  });
}

async function deleteExchangeRate(exchangeId) {
  const confirmDelete = confirm(
    "Voulez-vous vraiment supprimer ce taux de change ?"
  );
  if (!confirmDelete) return;

  try {
    const response = await fetch(
      `http://localhost:8080/moneyTransferProject/exchange-rates/${exchangeId}`,
      {
        method: "DELETE",
      }
    );

    if (!response.ok) {
      let errorText = "";
      try {
        errorText = await response.text(); // Essaye de lire le message du serveur
      } catch (e) {
        errorText = "Réponse vide ou non lisible.";
      }

      console.error("Erreur HTTP :", response.status, response.statusText);
      console.error("Détails serveur :", errorText);
      alert("Erreur lors de la suppression : " + errorText);
      return;
    }

    alert("Taux supprimé avec succès !");
    loadExchangeRates(); // Recharge la liste après suppression
  } catch (error) {
    console.error("Erreur lors de la suppression du taux de change :", error);
    alert("Une erreur est survenue lors de la suppression.");
  }
}

// Charger les devises dans le <select>
async function loadMonetaryUnits(type) {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/monetary-units"
    );
    const units = await response.json();
    monetaryUnits = units;
    if (conversionMonetaryUnits.length === 0)
      conversionMonetaryUnits = monetaryUnits;
    populateCurrencySelect(units, type);
  } catch (error) {
    console.error("Erreur lors du chargement des devises :", error);
  }
}

// Ajouter les options dans le select
function populateCurrencySelect(units, type) {
  const select =
    type == "modal"
      ? document.getElementById("sourceCurrencySelect")
      : document.getElementById("conversionSourceCurrencySelect");
  select.innerHTML = "";

  units.forEach((unit) => {
    if (unit.code == "MGA") return;
    const option = document.createElement("option");
    option.value = unit.code;
    option.textContent = unit.code;
    option.className = "bg-[#1A1A1A] text-white";
    select.appendChild(option);
  });
}

// Récupérer les données du formulaire et créer un taux ou mettre à jour un taux
async function saveExchangeRate() {
  const baseAmount = parseInt(document.getElementById("baseAmount").value);
  const rateToMGA = parseInt(document.getElementById("rateToMGA").value);
  const selectedCode = document.getElementById("sourceCurrencySelect").value;

  console.log(monetaryUnits);

  const sourceCurrency = monetaryUnits.find(
    (unit) => unit.code === selectedCode
  );

  if (!baseAmount || !rateToMGA || !sourceCurrency) {
    alert("Veuillez remplir tous les champs.");
    return;
  }

  // configurer la date de modification en GMT + 3
  const date = new Date();
  date.setHours(date.getHours() + 3);

  const exchangeRate = {
    baseAmount: baseAmount,
    rateToMGA: rateToMGA,
    sourceCurrency: sourceCurrency,
    modificationDate: date.toISOString(),
  };

  try {
    let response;
    console.log("Exchange id: " + editExchangeId);

    if (editExchangeId) {
      // Si un ID d'édition existe, on met à jour le taux
      exchangeRate["exchangeId"] = editExchangeId;
      response = await fetch(
        `http://localhost:8080/moneyTransferProject/exchange-rates`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(exchangeRate),
        }
      );
    } else {
      // Sinon, on crée un nouveau taux
      response = await fetch(
        "http://localhost:8080/moneyTransferProject/exchange-rates",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(exchangeRate),
        }
      );
    }

    if (!response.ok) {
      const errorData = await response.json();
      alert("Erreur : " + errorData.error); // Affiche le message venant du backend
      return;
    }

    alert(
      editExchangeId
        ? "Taux mis à jour avec succès !"
        : "Taux ajouté avec succès !"
    );
    closeModal();
    loadExchangeRates();
  } catch (err) {
    console.error("Erreur lors de la création du taux :", err);
    alert("Erreur lors de la création du taux");
  }
}

async function conversion() {
  const amount = document.getElementById("conversionSourceAmountInput").value;
  if (!amount || isNaN(amount) || amount <= 0 || amount.trim() === "") {
    document.getElementById("conversionTargetAmount").value = "";
    return;
  }
  const sourceCurrencyCode = document.getElementById(
    "conversionSourceCurrencySelect"
  ).value;

  const currencyId = conversionMonetaryUnits.find(
    (unit) => unit.code === sourceCurrencyCode
  ).unitId;

  if (currencyId === undefined) {
    alert("Veuillez sélectionner une devise valide.");
    return;
  }

  try {
    const response = await fetch(
      `http://localhost:8080/moneyTransferProject/exchange-rates/convert?currencyId=${currencyId}&amount=${amount}`,
      {
        method: "GET",
        headers: {
          "X-Requested-With": "XMLHttpRequest",
        },
      }
    );

    const result = await response.json();

    if (response.ok) {
      document.getElementById("conversionTargetAmount").value = Number(
        result.convertedAmount
      )
        .toLocaleString("fr-FR")
        .toString();
      addToHistory(amount, sourceCurrencyCode, result.convertedAmount);
    } else {
      alert(`Erreur : ${result.error}`);
    }
  } catch (error) {
    alert("Erreur réseau : " + error.message);
  }
}

function addToHistory(amount, currencyCode, result) {
  const historyContainer = document.getElementById("conversionHistory");

  const item = document.createElement("div");
  item.className = "mb-2";
  item.innerHTML = `<span class="text-gray-300">${amount} ${currencyCode}</span> → 
                    <span class="text-white font-bold">${result.toLocaleString(
                      "fr-FR"
                    )} MGA</span>`;

  if (historyContainer.querySelector("p")) {
    historyContainer.innerHTML = ""; // remove "aucune conversion"
  }

  historyContainer.prepend(item);
}

// Ouvrir le modal
// function openModal() {
//   const modal = document.getElementById("modal");
//   modal.classList.remove("hidden");
//   loadMonetaryUnits();
// }
function openModal(isEdit = false, rate = null) {
  const modal = document.getElementById("modal");
  const modalTitle = document.getElementById("exchangeModalTitle");
  modal.classList.remove("hidden");

  if (isEdit && rate) {
    const parsedRate = JSON.parse(decodeURIComponent(rate));
    console.log(parsedRate);
    modalTitle.textContent = "Modifier le taux de change";
    document.getElementById("addExchangeRateBtn").textContent = "Enregistrer";
    document.getElementById("baseAmount").value = parsedRate.baseAmount;
    document.getElementById("rateToMGA").value = parsedRate.rateToMGA;

    monetaryUnits = [parsedRate.sourceCurrency];
    const select = document.getElementById("sourceCurrencySelect");
    select.innerHTML = "";

    const option = document.createElement("option");
    option.value = parsedRate.sourceCurrency.code;
    option.textContent = parsedRate.sourceCurrency.code;
    option.className = "bg-[#1A1A1A] text-white";
    select.appendChild(option);
    editExchangeId = parsedRate.exchangeId;
  } else {
    modalTitle.textContent = "Nouveau taux de change";
    document.getElementById("addExchangeRateBtn").textContent = "Ajouter";
    loadMonetaryUnits("modal");
  }
}

// Fermer le modal
function closeModal() {
  const modal = document.getElementById("modal");
  modal.classList.add("hidden");

  // Nettoyer les champs
  document.querySelectorAll("input[type='number']").forEach((input) => {
    input.value = "";
  });

  editExchangeId = null; // Réinitialiser l'ID d'édition
  monetaryUnits = []; // Réinitialiser les unités monétaires
}

// Initialiser les événements
function setupEventListeners() {
  const newButton = document.getElementById("newButton");
  const closeModalButton = document.getElementById("closeModal");
  const addButton = document.getElementById("addExchangeRateBtn");
  const searchInput = document.getElementById("searchInput");
  const conversionButton = document.getElementById(
    "conversionSourceAmountInput"
  );
  const conversionSourceCurrencySelect = document.getElementById(
    "conversionSourceCurrencySelect"
  );

  if (conversionButton) conversionButton.addEventListener("input", conversion);
  if (conversionSourceCurrencySelect)
    conversionSourceCurrencySelect.addEventListener("change", conversion);

  if (newButton) newButton.addEventListener("click", openModal);
  if (closeModalButton) closeModalButton.addEventListener("click", closeModal);
  if (addButton) addButton.addEventListener("click", saveExchangeRate);
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      loadExchangeRates(e.target.value);
    });
  }
}

// Point d’entrée du script
loadExchangeRates();
loadMonetaryUnits("conversion");
setupEventListeners();
