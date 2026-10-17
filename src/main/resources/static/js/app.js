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

  function configurarMostrarContrasena() {
    document.querySelectorAll("[data-mostrar-contrasena]").forEach((boton) => {
      boton.addEventListener("click", () => {
        const campo = document.querySelector(boton.dataset.mostrarContrasena);
        if (!campo) return;

        campo.type = campo.type === "password" ? "text" : "password";
        boton.textContent = campo.type === "password" ? "Mostrar" : "Ocultar";
      });
    });
  }

  function configurarMenuAdministrador() {
    const botonMenu = document.getElementById("botonMenuAdmin");
    const barraLateral = document.getElementById("barraLateralAdmin");
    const superposicion = document.getElementById("superposicionAdmin");
    if (!botonMenu || !barraLateral) return;

    const cerrarMenu = () => {
      barraLateral.classList.remove("mostrar");
      superposicion?.classList.add("d-none");
    };

    botonMenu.addEventListener("click", () => {
      barraLateral.classList.toggle("mostrar");
      superposicion?.classList.toggle("d-none");
    });

    superposicion?.addEventListener("click", cerrarMenu);
    barraLateral
      .querySelectorAll("a")
      .forEach((enlace) => enlace.addEventListener("click", cerrarMenu));
  }

  document.addEventListener("DOMContentLoaded", () => {
    marcarNavegacion();
    configurarMostrarContrasena();
    configurarMenuAdministrador();
  });
})();
