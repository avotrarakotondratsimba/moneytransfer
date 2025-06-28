var monetaryUnits = window.monetaryUnits || [];
var transferId = window.transferId || null;

// Charger tous les taux de change
async function loadTransferFees(search = "") {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/transfer-fees?search=" +
        encodeURIComponent(search) /*,
      {
        headers: {
          "X-Requested-With": "XMLHttpRequest",
        },
      }*/
    );

    if (!response.ok) {
      throw new Error("Erreur HTTP : " + response.status);
    }

    const data = await response.json(); // <-- C'est ça qu'il manquait
    renderTransferFees(data);
  } catch (err) {
    console.error("Erreur lors du chargement des taux :", err);
  }
}

// Afficher les taux dans le tableau
function renderTransferFees(data) {
  const tbody = document.getElementById("transfer-fee-body");
  tbody.innerHTML = "";

  data.forEach((fee) => {
    const row = document.createElement("tr");
    const encodedFee = encodeURIComponent(JSON.stringify(fee));

    row.classList.add(
      "group",
      "bg-transparent",
      "hover:bg-[#2A2A2A]",
      "transition-colors",
      "duration-100"
    );

    row.innerHTML = `
        <td class="px-4 py-2 text-center">${fee.transferId}</td>
        <td class="px-4 py-2 text-center">${fee.currency.code}</td>
        <td class="px-4 py-2 text-center">${Number(
          fee.minAmount
        ).toLocaleString("fr-FR")}</td>
        <td class="px-4 py-2 text-center">${Number(
          fee.maxAmount
        ).toLocaleString("fr-FR")}</td>
        <td class="px-4 py-2 text-center">${Number(
          fee.feeAmount
        ).toLocaleString("fr-FR")}</td>
        <td class="px-2 py-2 text-center">
          <div class="flex gap-2 justify-end opacity-0 group-hover:opacity-100 transition-opacity duration-100">
            <button onclick="openModal(${true}, '${encodedFee}')" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">edit</span>
            </button>
            <button onclick="deleteTransferFee(${
              fee.transferId
            })" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">delete</span>
            </button>
          </div>
        </td>
      `;

    tbody.appendChild(row);
  });
}

async function deleteTransferFee(transferId) {
  const confirmDelete = confirm(
    "Voulez-vous vraiment supprimer ce frais de transfert ?"
  );
  if (!confirmDelete) return;
  console.log(transferId);

  try {
    const response = await fetch(
      `http://localhost:8080/moneyTransferProject/transfer-fees/${transferId}`,
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

    alert("Frais supprimé avec succès !");
    loadTransferFees(); // Recharge la liste après suppression
  } catch (error) {
    console.error("Erreur lors de la suppression du taux de change :", error);
    alert("Une erreur est survenue lors de la suppression.");
  }
}

// Charger les devises dans le <select>
async function loadMonetaryUnits() {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/monetary-units"
    );
    const units = await response.json();
    monetaryUnits = units;
    populateCurrencySelect(units);
  } catch (error) {
    console.error("Erreur lors du chargement des devises :", error);
  }
}

// Ajouter les options dans le select
function populateCurrencySelect(units) {
  const select = document.getElementById("currencyNameSelect");
  select.innerHTML = "";
  let i = 1;

  units.forEach((unit) => {
    if (unit.code == "MGA") return;
    const option = document.createElement("option");
    option.value = unit.code;
    option.textContent = unit.name;
    option.className = "bg-[#1A1A1A] text-white";
    select.appendChild(option);

    if (i == 1) {
      const currencyDisplays = document.querySelectorAll(".currency-selected");

      currencyDisplays.forEach((el) => {
        console.log("Element: " + el);

        el.textContent = unit.code;
      });
    }

    i++;
  });
}

function changeCurrency(e) {
  const selectedCode = e.target.value;
  const currencyDisplays = document.querySelectorAll(".currency-selected");

  currencyDisplays.forEach((el) => {
    el.textContent = selectedCode;
  });
}

// Récupérer les données du formulaire et créer un taux ou mettre à jour un taux
async function saveTransferFee() {
  const selectedCode = document.getElementById("currencyNameSelect").value;
  const minAmount = parseInt(document.getElementById("minAmount").value);
  const maxAmount = parseInt(document.getElementById("maxAmount").value);
  const feeAmount = parseInt(document.getElementById("feeAmount").value);

  console.log(monetaryUnits);

  const currency = monetaryUnits.find((unit) => unit.code === selectedCode);

  if (!minAmount || !maxAmount || !feeAmount || !currency) {
    alert("Veuillez remplir tous les champs.");
    return;
  }

  if (minAmount < 0 || maxAmount < 0 || feeAmount < 0) {
    alert("Les montants ne peuvent pas être négatifs.");
    return;
  }

  if (minAmount > maxAmount) {
    alert("Le montant minimum doit être inférieur ou égal au montant maximum.");
    return;
  }

  const transferFee = {
    minAmount: minAmount,
    maxAmount: maxAmount,
    feeAmount: feeAmount,
    currency: currency,
  };

  try {
    let response;
    console.log("Transfer id: " + transferId);

    if (transferId) {
      // Si un ID d'édition existe, on met à jour le taux
      transferFee["transferId"] = transferId;
      response = await fetch(
        `http://localhost:8080/moneyTransferProject/transfer-fees`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(transferFee),
        }
      );
    } else {
      // Sinon, on crée un nouveau taux
      response = await fetch(
        "http://localhost:8080/moneyTransferProject/transfer-fees",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(transferFee),
        }
      );
    }

    if (!response.ok) {
      const errorData = await response.json();
      alert("Erreur : " + errorData.error); // Affiche le message venant du backend
      return;
    }

    alert(
      transferId
        ? "Frais mis à jour avec succès !"
        : "Frais ajouté avec succès !"
    );
    closeModal();
    loadTransferFees();
  } catch (err) {
    console.error("Erreur lors de la création du frais :", err);
    alert("Erreur lors de la création du frais");
  }
}

function openModal(isEdit = false, fee = null) {
  const modal = document.getElementById("modal");
  const modalTitle = document.getElementById("transferFeeModalTitle");
  modal.classList.remove("hidden");

  if (isEdit && fee) {
    const parsedFee = JSON.parse(decodeURIComponent(fee));
    console.log(parsedFee);
    modalTitle.textContent = "Modifier le taux de change";
    document.getElementById("addTransferFeeBtn").textContent = "Enregistrer";

    const select = document.getElementById("currencyNameSelect");
    select.innerHTML = "";

    const option = document.createElement("option");
    option.value = parsedFee.currency.code;
    option.textContent = parsedFee.currency.name;
    option.className = "bg-[#1A1A1A] text-white";
    select.appendChild(option);

    const currencyDisplays = document.querySelectorAll(".currency-selected");

    currencyDisplays.forEach((el) => {
      console.log("Element: " + el);

      el.textContent = parsedFee.currency.code;
    });

    document.getElementById("minAmount").value = parsedFee.minAmount;
    document.getElementById("maxAmount").value = parsedFee.maxAmount;
    document.getElementById("feeAmount").value = parsedFee.feeAmount;

    monetaryUnits = [parsedFee.currency];

    transferId = parsedFee.transferId;
  } else {
    modalTitle.textContent = "Nouveau frais de transfert";
    document.getElementById("addTransferFeeBtn").textContent = "Ajouter";
    loadMonetaryUnits();
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

  transferId = null; // Réinitialiser l'ID de transfert
  monetaryUnits = []; // Réinitialiser les unités monétaires
}

// Initialiser les événements
function setupEventListeners() {
  const newButton = document.getElementById("newButton");
  const closeModalButton = document.getElementById("closeModal");
  const addButton = document.getElementById("addTransferFeeBtn");
  const searchInput = document.getElementById("searchInput");
  const currencyNameSelect = document.getElementById("currencyNameSelect");

  if (newButton) newButton.addEventListener("click", openModal);
  if (closeModalButton) closeModalButton.addEventListener("click", closeModal);
  if (addButton) addButton.addEventListener("click", saveTransferFee);
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      loadTransferFees(e.target.value);
    });
  }
  if (currencyNameSelect)
    currencyNameSelect.addEventListener("change", changeCurrency);
}

// Point d’entrée du script
loadTransferFees();
setupEventListeners();
