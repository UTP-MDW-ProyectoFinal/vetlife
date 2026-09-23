(function () {
  "use strict";

  const {
    CLAVES_ALMACENAMIENTO,
    obtenerMascotas,
    obtenerCuenta,
    guardarAlmacenamiento,
    escaparHtml,
    mostrarToast,
  } = window.VetLife;

  function configurarFormularioMascota() {
    const formulario = document.getElementById("formularioMascota");
    if (!formulario) return;

    formulario.addEventListener("submit", (evento) => {
      evento.preventDefault();

      const especieSeleccionada = formulario.querySelector(
        'input[name="especie"]:checked',
      );
      if (!especieSeleccionada) {
        mostrarToast("Selecciona la especie de tu mascota.", "danger");
        return;
      }

      const mascotas = obtenerMascotas();
      mascotas.push({
        id: Date.now(),
        nombre: document.getElementById("mascotaNombre").value.trim(),
        especie: especieSeleccionada.value,
        edad: document.getElementById("mascotaEdad").value,
        sexo: document.getElementById("mascotaSexo").value,
        raza: document.getElementById("mascotaRaza").value.trim(),
      });

      guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.mascotas, mascotas);
      const origen = new URLSearchParams(window.location.search).get("from");
      window.location.href = origen === "cita" ? "citas.html" : "reservas.html";
    });
  }

  function renderizarMascotasPerfil() {
    const contenedor = document.getElementById("listaMascotasPerfil");
    if (!contenedor) return;

    const mascotas = obtenerMascotas();
    contenedor.innerHTML = mascotas.length
      ? mascotas
          .map(
            (mascota) => `
        <div class="card border-vet mb-2">
          <div class="card-body d-flex align-items-center gap-3">
            <div>
              <div class="fw-semibold">${escaparHtml(mascota.nombre)}</div>
              <small class="text-secondary">${escaparHtml(mascota.especie)} · ${escaparHtml(mascota.raza || "Sin raza registrada")}</small>
            </div>
          </div>
        </div>`,
          )
          .join("")
      : '<p class="text-secondary small mb-0">Aún no hay mascotas registradas.</p>';
  }

  function configurarFormularioPerfil() {
    const formulario = document.getElementById("formularioPerfil");
    if (!formulario) return;

    const cuenta = obtenerCuenta() || {};
    const camposPerfil = {
      nombre: "perfilNombre",
      apellido: "perfilApellido",
      dni: "perfilDni",
      telefono: "perfilTelefono",
      correo: "perfilCorreo",
    };

    Object.entries(camposPerfil).forEach(([clave, idCampo]) => {
      const campo = document.getElementById(idCampo);
      if (campo) campo.value = cuenta[clave] || "";
    });

    renderizarMascotasPerfil();

    formulario.addEventListener("submit", (evento) => {
      evento.preventDefault();

      const cuentaActual = obtenerCuenta() || {};
      const cuentaActualizada = {
        ...cuentaActual,
        nombre: document.getElementById("perfilNombre").value.trim(),
        apellido: document.getElementById("perfilApellido").value.trim(),
        dni: document.getElementById("perfilDni").value.trim(),
        telefono: document.getElementById("perfilTelefono").value.trim(),
        correo: document
          .getElementById("perfilCorreo")
          .value.trim()
          .toLowerCase(),
      };

      guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.cuenta, cuentaActualizada);
      window.VetLifeModulos.autenticacion?.actualizarZonaUsuario();
      mostrarToast("Perfil actualizado correctamente.");
    });
  }

  window.VetLifeModulos = window.VetLifeModulos || {};
  window.VetLifeModulos.mascotas = {
    iniciar() {
      configurarFormularioMascota();
      configurarFormularioPerfil();
    },
  };
})();
