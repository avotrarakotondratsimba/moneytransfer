// Dropdown toggle
document.querySelectorAll(".dropdown-toggle").forEach((btn) => {
  btn.addEventListener("click", () => {
    const dropdown = btn.nextElementSibling;
    dropdown.classList.toggle("hidden");
    btn.querySelector("svg").classList.toggle("rotate-180");
  });
});

// Mobile menu toggle (si tu veux ajouter un bouton hamburger)
