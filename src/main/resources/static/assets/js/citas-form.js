// Ticket de Brayan: logica de fecha y horario del formulario de citas
(function () {
  "use strict";

  const campoFecha = document.getElementById("citaFecha");
  const selectHora = document.getElementById("citaHora");

  if (!campoFecha || !selectHora) return;

  const INTERVALO_MINUTOS = 30;

  function obtenerFechaISO(fecha) {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, "0");
    const dia = String(fecha.getDate()).padStart(2, "0");
    return `${anio}-${mes}-${dia}`;
  }

  function obtenerFranjaLimite() {
    const ahora = new Date();
    const minutos = ahora.getHours() * 60 + ahora.getMinutes();
    const redondeados =
      Math.ceil(minutos / INTERVALO_MINUTOS) * INTERVALO_MINUTOS;
    const horas = Math.min(Math.floor(redondeados / 60), 23);
    const minutosRestantes = redondeados % 60;
    return `${String(horas).padStart(2, "0")}:${String(minutosRestantes).padStart(2, "0")}`;
  }

  function aplicarBloqueo() {
    const hoy = obtenerFechaISO(new Date());
    const esHoy = campoFecha.value === hoy;
    const limite = obtenerFranjaLimite();
    let disponibles = 0;

    selectHora.querySelectorAll("option").forEach((opcion) => {
      if (!opcion.value) return;

      const bloqueada = esHoy && opcion.value < limite;
      opcion.disabled = bloqueada;
      if (!bloqueada) disponibles += 1;
    });

    if (esHoy && disponibles === 0) {
      selectHora.setCustomValidity(
        "No hay horarios disponibles para hoy, elige otra fecha.",
      );
    } else {
      selectHora.setCustomValidity("");
    }

    if (esHoy && disponibles > 0 && selectHora.selectedOptions.length) {
      const opcionSeleccionada = selectHora.selectedOptions[0];
      if (opcionSeleccionada.disabled) {
        selectHora.value = "";
      }
    }
  }

  campoFecha.addEventListener("change", aplicarBloqueo);
  aplicarBloqueo();
})();
// Fin Ticket de Brayan
