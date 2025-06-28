var localClients = window.localClients || [];
var abroadClients = window.abroadClients || [];
var clientId = window.clientId || null;

// Charger l'historique d'envoi
// async function loadSends(search = "") {
//   try {
//     const response = await fetch(
//       "http://localhost:8080/moneyTransferProject/sends?search=" +
//         encodeURIComponent(search) /*,
//       {
//         headers: {
//           "X-Requested-With": "XMLHttpRequest",
//         },
//       }*/
//     );

//     if (!response.ok) {
//       throw new Error("Erreur HTTP : " + response.status);
//     }

//     const data = await response.json();
//     renderSends(data);
//   } catch (err) {
//     console.error("Erreur lors du chargement des historiques d'envoi :", err);
//   }
// }
async function loadSends(search = "") {
  const startDateStr = document.getElementById("startDate").value;
  const endDateStr = document.getElementById("endDate").value;

  const params = new URLSearchParams();
  if (search) params.append("search", search);
  if (startDateStr) params.append("startDate", startDateStr);
  if (endDateStr) params.append("endDate", endDateStr);

  try {
    const response = await fetch(
      `http://localhost:8080/moneyTransferProject/sends?${params.toString()}`
    );

    if (!response.ok) {
      throw new Error("Erreur HTTP : " + response.status);
    }

    const data = await response.json();
    renderSends(data);
  } catch (err) {
    console.error("Erreur lors du chargement des historiques d'envoi :", err);
  }
}

// Afficher les envois dans le tableau
function renderSends(data) {
  const tbody = document.getElementById("send-body");
  tbody.innerHTML = "";

  data.forEach((send) => {
    const row = document.createElement("tr");
    const encodedSend = encodeURIComponent(JSON.stringify(send));

    row.classList.add(
      "group",
      "bg-transparent",
      "hover:bg-[#2A2A2A]",
      "transition-colors",
      "duration-100"
    );

    row.innerHTML = `
        <td class="px-4 py-2 text-center">${send.senderPhone}</td>
        <td class="px-4 py-2 text-center">${send.receiverPhone}</td>
        <td class="px-4 py-2 text-center">${Number(send.amount).toLocaleString(
          "fr-FR"
        )} ${send.currency.code}</td>
        <td class="px-4 py-2 text-center">
        ${new Date(send.date)
          .toLocaleString("fr-FR", {
            dateStyle: "short",
            timeStyle: "short",
            timeZone: "Indian/Antananarivo", // ou "Etc/GMT-3"
          })
          .replace(" ", " à ")}
        
        </td>
        <td class="px-4 py-2 text-center">${
          send.reason ? send.reason : "-"
        }</td>
        <td class="px-2 py-2 text-right">
          <div class="flex gap-2 justify-end opacity-0 group-hover:opacity-100 transition-opacity duration-100">
            <button onclick="deleteSend('${
              send.sendId
            }')" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">delete</span>
            </button>
          </div>
        </td>
      `;

    tbody.appendChild(row);
  });
}

async function deleteSend(sendId) {
  const confirmDelete = confirm("Voulez-vous vraiment supprimer cette ligne ?");
  if (!confirmDelete) return;
  console.log(sendId);

  try {
    const response = await fetch(
      `http://localhost:8080/moneyTransferProject/sends/${sendId}`,
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

    alert(" Suppression effectuée avec succès !");
    loadSends(); // Recharge la liste après suppression
  } catch (error) {
    console.error("Erreur lors de la suppression :", error);
    alert("Une erreur est survenue lors de la suppression.");
  }
}

// Charger tous les clients
async function loadClients(category, modalType) {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/clients/" +
        category +
        "?search=" /*,
        {
          headers: {
            "X-Requested-With": "XMLHttpRequest",
          },
        }*/
    );

    if (!response.ok) {
      throw new Error("Erreur HTTP : " + response.status);
    }

    const data = await response.json();
    if (category === "abroad") abroadClients = data;
    else localClients = data;
    renderClients(data, modalType);
  } catch (err) {
    console.error("Erreur lors du chargement des clients :", err);
  }
}

// Afficher les clients dans le tableau
function renderClients(data, modalType) {
  const tbody = document.getElementById("client-table-body");
  tbody.innerHTML = "";

  data.forEach((client) => {
    const row = document.createElement("tr");
    const encodedClient = encodeURIComponent(JSON.stringify(client));

    row.classList.add(
      "group",
      "bg-transparent",
      "hover:bg-[#2A2A2A]",
      "transition-colors",
      "duration-100"
    );

    row.innerHTML = `
          <td class="px-4 py-2 text-center">
            <input type="radio" name="selectedClient"  value="${
              client.phoneNumber
            }" />
          </td>
          <td class="px-4 py-2 text-center">${client.phoneNumber}</td>
          <td class="px-4 py-2 text-center">${client.clientName}</td>
          <td class="px-4 py-2 text-center">${
            client.gender == "male" ? "Masculin" : "Féminin"
          }</td>
          <td class="px-4 py-2 text-center">${client.country.name}</td>
          <td class="px-4 py-2 text-center">${Number(
            client.balance
          ).toLocaleString("fr-FR")} ${client.country.currency.code}</td>
          <td class="px-4 py-2 text-center">${client.email}</td>
        `;

    tbody.appendChild(row);
  });

  // Pour le bouton radio du tableau des clients
  document.querySelectorAll('input[name="selectedClient"]').forEach((radio) => {
    radio.addEventListener("change", (e) => {
      const phoneNumber = e.target.value;
      const client = abroadClients.find(
        (client) => client.phoneNumber === phoneNumber
      );
      if (
        document.getElementById("clientTabledModalTitle").textContent ===
        "Choisir l'envoyeur"
      ) {
        if (modalType === "pdf")
          document.getElementById("senderPhoneNumber").textContent =
            phoneNumber;
        else {
          document.getElementById("senderPhone").textContent = phoneNumber;
          document.getElementById("currency").textContent =
            client.country.currency.code;
        }
      } else document.getElementById("receiverPhone").textContent = phoneNumber;
      closeClientTableModal();
    });
  });
}

// Envoyer l'argent
async function sendMoney() {
  const sender = abroadClients.find(
    (client) =>
      client.phoneNumber === document.getElementById("senderPhone").textContent
  );
  console.log(sender);

  const receiver = localClients.find(
    (client) =>
      client.phoneNumber ===
      document.getElementById("receiverPhone").textContent
  );
  const amount = parseInt(document.getElementById("amount").value);
  const reason = document.getElementById("reason").value;

  if (!sender || !receiver || !amount) {
    alert("Veuillez remplir tous les champs.");
    return;
  }

  const loaderOverlay = document.getElementById("sendLoaderOverlay");
  loaderOverlay.classList.remove("hidden");

  // configurer la date de modification en GMT + 3
  const date = new Date();
  date.setHours(date.getHours() + 3);

  const sendInformation = {
    sender: sender,
    receiver: receiver,
    amount: amount,
    reason: reason,
    date: date.toISOString(),
    currency: sender.country.currency,
  };

  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/sends",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(sendInformation),
      }
    );

    if (!response.ok) {
      // Masquer l'overlay, puis lancer alert après le repaint
      loaderOverlay.classList.add("hidden");
      requestAnimationFrame(() => {
        setTimeout(async () => {
          if (!response.ok) {
            const errorData = await response.json();
            alert("Erreur : " + errorData.error);
            return;
          }
        }, 0);
      });
      return; // Ne pas exécuter la suite
    }

    loaderOverlay.classList.add("hidden");
    requestAnimationFrame(() => {
      setTimeout(() => {
        alert("Envoi effectué avec succès");
        closeModal();
        loadSends();
      }, 0);
    });
  } catch (err) {
    console.error("Erreur lors du transfert :", err);
    loaderOverlay.classList.add("hidden");
    requestAnimationFrame(() => {
      setTimeout(() => {
        alert("Erreur lors du transfert : " + err.message);
      }, 0);
    });
  }
}

// Ouvrir le modal
function openModal() {
  const modal = document.getElementById("modal");
  const modalTitle = document.getElementById("sendModalTitle");
  const sendButton = document.getElementById("sendBtn");
  modal.classList.remove("hidden");

  modalTitle.textContent = "Envoi d'argent";
  sendButton.textContent = "Envoyer";
}

// Fermer le modal
function closeModal() {
  const modal = document.getElementById("modal");
  modal.classList.add("hidden");

  // Nettoyer les champs
  document.getElementById("senderPhone").textContent = "";
  document.getElementById("receiverPhone").textContent = "";

  document.querySelectorAll("input").forEach((input) => {
    input.value = "";
  });

  //   clientId = null;
  localClients = [];
  abroadClients = [];
}

// Ouvrir le modal contenant le tableau des clients
function openClientTableModal(type, modalType = null) {
  const modal = document.getElementById("client-table-modal");
  const modalTitle = document.getElementById("clientTabledModalTitle");
  modal.classList.remove("hidden");

  if (type === "sender") {
    modalTitle.textContent = "Choisir l'envoyeur";
    loadClients("abroad", modalType);
  } else {
    modalTitle.textContent = "Choisir le récepteur";
    loadClients("local");
  }
}

// Ouvrir le modal d'exportation PDF
function openExportPdfModal() {
  const modal = document.getElementById("export-pdf-modal");
  modal.classList.remove("hidden");

  // Ajout des valeurs dans le select
  const monthNames = [
    "Janvier",
    "Février",
    "Mars",
    "Avril",
    "Mai",
    "Juin",
    "Juillet",
    "Août",
    "Septembre",
    "Octobre",
    "Novembre",
    "Décembre",
  ];

  const currentMonth = new Date().getMonth();
  const select = document.getElementById("monthSelect");

  const option = document.createElement("option");
  option.value = null;
  option.textContent = "Choisir le mois";
  option.className = "bg-[#1A1A1A] text-white";
  select.appendChild(option);

  for (let i = 0; i <= currentMonth; i++) {
    const option = document.createElement("option");
    option.value = i + 1;
    option.textContent = monthNames[i];
    option.className = "bg-[#1A1A1A] text-white";
    select.appendChild(option);
  }
}

// Fermer le modal d'exportation PDF
function closeExportPdfModal() {
  const modal = document.getElementById("export-pdf-modal");
  modal.classList.add("hidden");

  document.getElementById("senderPhoneNumber").textContent = "";

  document.getElementById("monthSelect").innerHTML = "";

  abroadClients = [];
}

// Fermer le modal contenant le tableau des clients
function closeClientTableModal() {
  const modal = document.getElementById("client-table-modal");
  modal.classList.add("hidden");
}

// Charger tous les clients
async function exportData() {
  try {
    const senderPhone = document
      .getElementById("senderPhoneNumber")
      .textContent.substring(1);
    const month = document.getElementById("monthSelect").value;
    const year = 2025;

    // Vérification
    if (!senderPhone || isNaN(senderPhone)) {
      alert("Veuillez choisir un envoyeur");
      return;
    }
    if (!month || isNaN(month)) {
      alert("Veuillez sélectionner un mois.");
      return;
    }

    const url = new URL(
      "http://localhost:8080/moneyTransferProject/downloadStatement"
    );
    url.searchParams.append("senderPhone", encodeURIComponent(senderPhone));
    url.searchParams.append("month", month);
    url.searchParams.append("year", year);

    const response = await fetch(url.toString());

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error);
    }

    // Télécharger le PDF
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = downloadUrl;
    a.download = `releve_${month}_${year}.pdf`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(downloadUrl);
    closeExportPdfModal();
  } catch (err) {
    console.error("Erreur lors de la génération du PDF:", err);
    alert("Erreur lors de la génération du PDF:\n" + err.message);
  }
}

// Initialiser les événements
function setupEventListeners() {
  const newButton = document.getElementById("newButton");
  const exportButton = document.getElementById("exportButton");
  const closeModalButton = document.getElementById("closeModal");
  const sendButton = document.getElementById("sendBtn");
  const searchInput = document.getElementById("searchInput");

  // Pour le modal contenant le tableau des clients
  const chooseSender = document.getElementById("chooseSender");
  const chooseReceiver = document.getElementById("chooseReceiver");
  const closeClientModal = document.getElementById("closeClientModal");

  // Pour le modal contenant l'exportation PDF
  const chooseSenderPhone = document.getElementById("chooseSenderPhone");
  const closePdfModal = document.getElementById("closePdfModal");
  const generatePdfButton = document.getElementById("generatePdfButton");

  if (newButton) newButton.addEventListener("click", openModal);

  if (exportButton) exportButton.addEventListener("click", openExportPdfModal); //exportData);

  if (closeModalButton) closeModalButton.addEventListener("click", closeModal);
  if (sendButton) sendButton.addEventListener("click", sendMoney);
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      loadSends(e.target.value);
    });
  }
  if (chooseSender)
    chooseSender.addEventListener("click", () =>
      openClientTableModal("sender")
    );
  if (chooseReceiver)
    chooseReceiver.addEventListener("click", () =>
      openClientTableModal("receiver")
    );
  if (closeClientModal)
    closeClientModal.addEventListener("click", closeClientTableModal);

  if (chooseSenderPhone)
    chooseSenderPhone.addEventListener("click", () =>
      openClientTableModal("sender", "pdf")
    );
  if (generatePdfButton)
    generatePdfButton.addEventListener("click", exportData);
  if (closePdfModal)
    closePdfModal.addEventListener("click", closeExportPdfModal);

  // Récupération des inputs
  const startInput = document.getElementById("startDate");
  const endInput = document.getElementById("endDate");

  // Initialisation Flatpickr pour la date de fin (endDate)
  const endPicker = flatpickr(endInput, {
    dateFormat: "d/m/Y",
    allowInput: false,
    locale: "fr",
  });

  // Initialisation Flatpickr pour la date de début (startDate)
  const startPicker = flatpickr(startInput, {
    dateFormat: "d/m/Y",
    maxDate: "today",
    allowInput: false,
    locale: "fr",
    onChange: function (selectedDates, dateStr, instance) {
      const startDate = selectedDates[0];

      if (startDate) {
        // Mise à jour de la minDate de endDate
        endPicker.set("minDate", startDate);

        // Vérifier si endDate est déjà sélectionnée
        const endDate = endPicker.selectedDates[0];
        if (endDate && endDate < startDate) {
          endPicker.clear(); // Efface la date de fin si invalide
        }
      } else {
        // Si la date de début est supprimée, on retire la restriction sur endDate
        endPicker.set("minDate", null);
      }
    },
  });

  startInput.addEventListener("change", () => loadSends(searchInput.value));
  endInput.addEventListener("change", () => loadSends(searchInput.value));
}

// Point d’entrée du script
loadSends();
setupEventListeners();
