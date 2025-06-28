async function setupDashboard() {
  try {
    const res = await fetch("/moneyTransferProject/dashboard");
    const data = await res.json();

    // 🔢 Injecter les totaux
    document.getElementById("monthlyRevenue").textContent =
      data.monthlyRevenue.toLocaleString("fr-FR") + " MGA";
    document.getElementById("annualRevenue").textContent =
      data.annualRevenue.toLocaleString("fr-FR") + " MGA";
    document.getElementById("monthlyOperations").textContent =
      data.monthlyOperations;
    document.getElementById("annualOperations").textContent =
      data.annualOperations;

    // 📈 Graphique ligne (revenus mensuels)
    const revenueCtx = document.getElementById("revenueChart").getContext("2d");
    const gradient = revenueCtx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, "rgba(59, 130, 246, 0.5)");
    gradient.addColorStop(1, "rgba(59, 130, 246, 0.0)");

    const revenueLabels = Array.from(
      { length: 12 },
      (_, i) =>
        [
          "Jan",
          "Fév",
          "Mar",
          "Avr",
          "Mai",
          "Juin",
          "Juil",
          "Août",
          "Sept",
          "Oct",
          "Nov",
          "Déc",
        ][i]
    );

    const monthlyRevenueData = new Array(12).fill(0);
    data.revenueStats.forEach(([month, total]) => {
      monthlyRevenueData[month - 1] = total;
    });

    new Chart(revenueCtx, {
      type: "line",
      data: {
        labels: revenueLabels,
        datasets: [
          {
            label: "Revenu",
            data: monthlyRevenueData,
            borderColor: "#3B82F6",
            backgroundColor: gradient,
            fill: true,
            pointRadius: 5,
            pointHoverRadius: 8,
            tension: 0.4,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => context.raw.toLocaleString("fr-FR") + " MGA",
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: (value) => value.toLocaleString("fr-FR") + " MGA",
              color: "#ccc",
            },
            grid: { color: "#333" },
          },
          x: {
            ticks: { color: "#ccc" },
            grid: { display: false },
          },
        },
      },
    });

    // 📊 Graphique barres (opérations mensuelles)
    const opCtx = document.getElementById("operationChart").getContext("2d");
    const monthlyOpsData = new Array(12).fill(0);
    data.operationStats.forEach(([month, count]) => {
      monthlyOpsData[month - 1] = count;
    });

    new Chart(opCtx, {
      type: "bar",
      data: {
        labels: revenueLabels,
        datasets: [
          {
            label: "Opérations",
            data: monthlyOpsData,
            backgroundColor: "rgba(76, 29, 149, 0.6)",
            borderRadius: 5,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => context.raw + " opérations",
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { color: "#ccc" },
            grid: { color: "#333" },
          },
          x: {
            ticks: { color: "#ccc" },
            grid: { display: false },
          },
        },
      },
    });
  } catch (error) {
    console.error("Erreur chargement dashboard:", error);
  }
}

// 🚀 Point d'entrée
setupDashboard();
