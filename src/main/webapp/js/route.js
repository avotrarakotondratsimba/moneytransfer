document.addEventListener("DOMContentLoaded", function () {
  const main = document.getElementById("main-content");
  const defaultPage = "dashboard-page.jsp";

  function loadPage(page) {
    fetch(`pages/${page}`)
      .then((res) => {
        if (!res.ok) throw new Error("Page not found");
        return res.text();
      })
      .then((html) => {
        main.innerHTML = html;
        setActiveLink(page);

        // Attendre que le DOM soit inséré puis chercher data-script
        const scriptTag = main.querySelector("[data-script]");
        if (scriptTag) {
          const scriptPath = scriptTag.getAttribute("data-script");
          loadPageScript(scriptPath);
          console.log("LoadPage");
        }
      })
      .catch((err) => {
        main.innerHTML = `<p class="text-red-500 p-4">Erreur : ${err.message}</p>`;
      });
  }

  function loadPageScript(scriptPath) {
    // Supprimer l'ancien script s’il existe déjà
    const oldScript = document.querySelector(`script[src="js/${scriptPath}"]`);
    if (oldScript) {
      oldScript.remove();
      console.log("Old script: " + oldScript);
    }

    const script = document.createElement("script");
    script.src = `js/${scriptPath}`;
    script.type = "text/javascript";
    script.defer = true;
    console.log(script);

    document.body.appendChild(script);
  }

  function setActiveLink(page) {
    document.querySelectorAll(".menu-link").forEach((link) => {
      const isActive = link.getAttribute("data-page") === page;
      link.classList.toggle("bg-[#3A3C3B]", isActive);
    });
  }

  document.querySelectorAll(".menu-link").forEach((link) => {
    link.addEventListener("click", () => {
      const page = link.getAttribute("data-page");
      loadPage(page);
      history.pushState({ page }, "", `#${page}`);
    });
  });

  const initialPage = location.hash ? location.hash.substring(1) : defaultPage;
  loadPage(initialPage);

  window.addEventListener("popstate", (e) => {
    const page = e.state?.page || defaultPage;
    loadPage(page);
  });
});
