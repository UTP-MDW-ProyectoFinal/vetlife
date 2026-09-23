(function () {
  "use strict";

  function marcarNavegacion() {
    const paginaActual = document.body.dataset.pagina;

    document.querySelectorAll("[data-pagina-nav]").forEach((enlace) => {
      const activo = enlace.dataset.paginaNav === paginaActual;
      enlace.classList.toggle("active", activo);
      if (activo) enlace.setAttribute("aria-current", "page");
    });
  }

  function protegerPaginas() {
    const paginaRequerida = document.body.dataset.requiere;
    const sesion = window.VetLife.obtenerSesion();

    if (
      paginaRequerida === "usuario" &&
      (!sesion || sesion.rol !== "usuario")
    ) {
      window.location.replace(
        `login.html?redirect=${encodeURIComponent(window.location.pathname.split("/").pop())}`,
      );
      return;
    }

    if (
      paginaRequerida === "administrador" &&
      (!sesion || sesion.rol !== "administrador")
    ) {
      window.location.replace("../usuario/login.html?redirect=admin");
    }
  }

  function configurarContacto() {
    const mapa = document.getElementById("mapaVetLife");
    if (!mapa) return;

    mapa.src =
      "https://www.openstreetmap.org/export/embed.html?bbox=-80.645%2C-5.205%2C-80.620%2C-5.185&layer=mapnik&marker=-5.19449%2C-80.63282";
  }

  function iniciarAplicacion() {
    marcarNavegacion();
    protegerPaginas();

    window.VetLifeModulos.autenticacion?.iniciar();
    window.VetLifeModulos.mascotas?.iniciar();
    window.VetLifeModulos.citas?.iniciar();
    window.VetLifeModulos.servicios?.iniciar();
    window.VetLifeModulos.administrador?.iniciar();
    configurarContacto();
  }

  document.addEventListener("DOMContentLoaded", iniciarAplicacion);
})();
