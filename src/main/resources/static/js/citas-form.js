(function () {
  "use strict";

  var form = document.getElementById("formReservaCita");
  if (!form) {
    return;
  }

  var campoFecha = document.getElementById("citaFecha");
  var selectHora = document.getElementById("citaHora");
  var botonConfirmar = document.querySelector('[data-bs-target="#modalConfirmarCita"]');

  var fechaServidor = form.getAttribute("data-fecha-servidor");
  var horaServidor = form.getAttribute("data-hora-servidor");

  function minutosDesdeMedianoche(hhmm) {
    var partes = hhmm.split(":");
    return parseInt(partes[0], 10) * 60 + parseInt(partes[1], 10);
  }

  function aplicarBloqueo() {
    var fechaSeleccionada = campoFecha.value;
    if (!fechaSeleccionada || fechaSeleccionada !== fechaServidor) {
      restaurarHoras();
      return;
    }

    var limiteMinutos = minutosDesdeMedianoche(horaServidor);
    var quedanDisponibles = false;

    Array.prototype.forEach.call(selectHora.options, function (opcion) {
      if (!opcion.value) {
        return;
      }
      if (minutosDesdeMedianoche(opcion.value) < limiteMinutos) {
        opcion.disabled = true;
        opcion.selected = false;
      } else {
        opcion.disabled = false;
        quedanDisponibles = true;
      }
    });

    if (!quedanDisponibles) {
      selectHora.setCustomValidity("No quedan horarios disponibles para hoy, elige otra fecha.");
    } else if (selectHora.selectedOptions.length && selectHora.selectedOptions[0].disabled) {
      selectHora.value = "";
      selectHora.setCustomValidity("");
    } else {
      selectHora.setCustomValidity("");
    }
  }

  function restaurarHoras() {
    Array.prototype.forEach.call(selectHora.options, function (opcion) {
      opcion.disabled = false;
    });
    selectHora.setCustomValidity("");
  }

  campoFecha.addEventListener("change", aplicarBloqueo);
  selectHora.addEventListener("change", function () {
    selectHora.setCustomValidity("");
  });

  if (botonConfirmar) {
    botonConfirmar.addEventListener("click", function () {
      aplicarBloqueo();
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }
      var modalElement = document.getElementById("modalConfirmarCita");
      if (window.bootstrap && modalElement) {
        bootstrap.Modal.getOrCreateInstance(modalElement).show();
      }
    });
  }

  form.addEventListener("submit", function (evento) {
    aplicarBloqueo();
    if (!form.checkValidity()) {
      evento.preventDefault();
      form.reportValidity();
    }
  });

  aplicarBloqueo();
})();
