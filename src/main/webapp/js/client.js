var countriesList = window.countriesList || [];
var clientId = window.clientId || null;

// Charger tous les clients
async function loadClients(search = "") {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/clients?search=" +
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
    renderClients(data);
  } catch (err) {
    console.error("Erreur lors du chargement des clients :", err);
  }
}

// Afficher les clients dans le tableau
function renderClients(data) {
  const tbody = document.getElementById("client-body");
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
        <td class="px-2 py-2 text-right">
          <div class="flex gap-2 justify-end opacity-0 group-hover:opacity-100 transition-opacity duration-100">
            <button onclick="openModal(${true}, '${encodedClient}')" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">edit</span>
            </button>
            <button onclick="deleteClient(${
              client.clientId
            })" class="text-gray-50 hover:text-gray-200">
              <span class="material-icons">delete</span>
            </button>
          </div>
        </td>
      `;

    tbody.appendChild(row);
  });
}

async function deleteClient(clientId) {
  const confirmDelete = confirm("Voulez-vous vraiment supprimer ce client ?");
  if (!confirmDelete) return;
  console.log(clientId);

  try {
    const response = await fetch(
      `http://localhost:8080/moneyTransferProject/clients/${clientId}`,
      {
        method: "DELETE",
      }
    );

    if (!response.ok) {
      let errorText = "";
      try {
        const errorData = await response.json();
        errorText = errorData.error; // Essaye de lire le message du serveur
        // alert("Erreur : " + errorData.error);
      } catch (e) {
        errorText = "Réponse vide ou non lisible.";
      }

      console.error("Erreur HTTP :", response.status, response.statusText);
      console.error("Détails serveur :", errorText);
      alert("Erreur lors de la suppression : " + errorText);
      return;
    }

    alert("Client supprimé avec succès !");
    loadClients(); // Recharge la liste après suppression
  } catch (error) {
    console.error("Erreur lors de la suppression du client :", error);
    alert("Une erreur est survenue lors de la suppression.");
  }
}

// Charger les clients dans le <select>
async function loadCountries() {
  try {
    const response = await fetch(
      "http://localhost:8080/moneyTransferProject/countries"
    );
    const countries = await response.json();
    countriesList = countries;
    populateCountrySelect(countries);
  } catch (error) {
    console.error("Erreur lors du chargement des devises :", error);
  }
}

// Ajouter les options dans le select
function populateCountrySelect(countries) {
  const select = document.getElementById("countrySelect");
  select.innerHTML = "";
  let i = 1;

  countries.forEach((country) => {
    const option = document.createElement("option");
    option.value = country.name;
    option.textContent = country.name;
    option.className = "bg-[#1A1A1A] text-white";
    select.appendChild(option);

    if (i == 1) {
      document.getElementById("phoneCode").textContent = country.phoneCode;
      document.getElementById("currency").textContent = country.currency.code;
      console.log(country);
    }

    i++;
  });
}

function changeCountry(e) {
  const selectedCountry = e.target.value;
  const country = countriesList.find(
    (countryObject) => countryObject.name === selectedCountry
  );
  document.getElementById("currency").textContent = country.currency.code;
  document.getElementById("phoneCode").textContent = country.phoneCode;
}

// Récupérer les données du formulaire et créer un client ou mettre à jour un client
async function saveClient() {
  const clientName = document.getElementById("clientName").value;
  const gender = document.getElementById("genderSelect").value;
  const countryName = document.getElementById("countrySelect").value;
  const phoneNumber =
    document.getElementById("phoneCode").textContent +
    document.getElementById("phoneNumber").value;
  const email = document.getElementById("email").value;
  const balanceValue = document.getElementById("balance").value;
  const balance = parseInt(balanceValue);

  console.log(countriesList);

  const country = countriesList.find(
    (countryObject) => countryObject.name === countryName
  );

  if (
    !clientName ||
    !gender ||
    !country ||
    !phoneNumber ||
    !email ||
    balanceValue === ""
  ) {
    alert("Veuillez remplir tous les champs.");
    console.log(clientName, gender, country, phoneNumber, email, balance);

    return;
  }

  if (balance < 0) {
    alert("Le solde ne peut pas être négatif.");
    return;
  }

  const emailValidFormat = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailValidFormat.test(email)) {
    alert("Veuillez entrer une adresse e-mail valide.");
    return;
  }

  const client = {
    phoneNumber: phoneNumber,
    clientName: clientName,
    gender: gender,
    country: country,
    balance: balance,
    email: email,
    isActive: true,
  };

  try {
    let response;
    console.log("Client id: " + clientId);

    if (clientId) {
      // Si un ID d'édition existe, on met à jour le taux
      client["clientId"] = clientId;
      response = await fetch(
        `http://localhost:8080/moneyTransferProject/clients`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(client),
        }
      );
    } else {
      // Sinon, on crée un nouveau taux
      response = await fetch(
        "http://localhost:8080/moneyTransferProject/clients",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(client),
        }
      );
    }

    if (!response.ok) {
      const errorData = await response.json();
      alert("Erreur : " + errorData.error);
      return;
    }

    alert(
      clientId
        ? "Client mis à jour avec succès !"
        : "Client ajouté avec succès !"
    );
    closeModal();
    loadClients();
  } catch (err) {
    console.error("Erreur lors de la création du client :", err);
    alert("Erreur lors de la création du client");
  }
}

// Ouvrir le modal
async function openModal(isEdit = false, client = null) {
  const modal = document.getElementById("modal");
  const modalTitle = document.getElementById("clientModalTitle");
  const addButton = document.getElementById("addClientBtn");
  modal.classList.remove("hidden");
  await loadCountries();

  if (isEdit && client) {
    const parsedClient = JSON.parse(decodeURIComponent(client));
    console.log("Ty io: " + parsedClient);
    modalTitle.textContent = "Modifier l'information du client";

    document.getElementById("clientName").value = parsedClient.clientName;
    document.getElementById("genderSelect").value = parsedClient.gender;
    document.getElementById("countrySelect").value = parsedClient.country.name;
    document.getElementById("phoneCode").textContent =
      parsedClient.country.phoneCode;
    document.getElementById("phoneNumber").value =
      parsedClient.phoneNumber.slice(parsedClient.country.phoneCode.length);
    document.getElementById("email").value = parsedClient.email;
    document.getElementById("balance").value = parsedClient.balance;
    document.getElementById("currency").textContent =
      parsedClient.country.currency.code;

    clientId = parsedClient.clientId;
    addButton.textContent = "Enregistrer";
  } else {
    modalTitle.textContent = "Nouveau client";
    addButton.textContent = "Ajouter";
  }
}

// Fermer le modal
function closeModal() {
  const modal = document.getElementById("modal");
  modal.classList.add("hidden");

  // Nettoyer les champs
  document.querySelectorAll("input").forEach((input) => {
    input.value = "";
  });

  document.getElementById("genderSelect").value = "male";

  clientId = null;
  countriesList = [];
}

// Initialiser les événements
function setupEventListeners() {
  const newButton = document.getElementById("newButton");
  const closeModalButton = document.getElementById("closeModal");
  const addButton = document.getElementById("addClientBtn");
  const searchInput = document.getElementById("searchInput");
  const countrySelect = document.getElementById("countrySelect");

  if (newButton) newButton.addEventListener("click", openModal);
  if (closeModalButton) closeModalButton.addEventListener("click", closeModal);
  if (addButton) addButton.addEventListener("click", saveClient);
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      loadClients(e.target.value);
    });
  }
  if (countrySelect) countrySelect.addEventListener("change", changeCountry);
}

// Point d’entrée du script
loadClients();
setupEventListeners();
