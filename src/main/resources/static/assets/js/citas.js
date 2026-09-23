(function () {
  "use strict";

  const {
    CLAVES_ALMACENAMIENTO,
    HORARIOS_DISPONIBLES,
    HORARIOS_BLOQUEADOS,
    obtenerCuenta,
    obtenerMascotas,
    obtenerCitas,
    obtenerSesion,
    obtenerServicios,
    guardarAlmacenamiento,
    escaparHtml,
    mostrarToast,
    obtenerFechaActual,
    formatearFecha,
  } = window.VetLife;

  function obtenerClaseEstado(estado) {
    const clases = {
      pendiente: "text-bg-warning",
      confirmada: "text-bg-success",
      "en curso": "text-bg-info",
      completada: "text-bg-secondary",
      cancelada: "text-bg-danger",
    };

    return clases[estado] || "text-bg-secondary";
  }

  function obtenerCitasUsuario() {
    const cuenta = obtenerCuenta();
    const citas = obtenerCitas();

    if (!cuenta?.correo) return [];

    return citas.filter(
      (cita) =>
        !cita.correoPropietario ||
        cita.correoPropietario.toLowerCase() === cuenta.correo.toLowerCase(),
    );
  }

  function configurarReservas() {
    const contenedor = document.getElementById("listaReservas");
    if (!contenedor) return;

    const citas = obtenerCitasUsuario();

    if (!citas.length) {
      contenedor.innerHTML = `
        <div class="card card shadow-sm">
          <div class="card-body p-4 text-center">
            <h2 class="h5 mt-3">Aún no tienes reservas</h2>
            <p class="text-secondary">Registra una mascota y agenda tu primera cita.</p>
            <a href="citas.html" class="btn btn-primary">Reservar una cita</a>
          </div>
        </div>`;
      return;
    }

    contenedor.innerHTML = citas
      .map((cita) => {
        const estado = cita.estado || "pendiente";
        const puedeCancelar = ["pendiente", "confirmada"].includes(estado);

        return `
        <article class="card card shadow-sm mb-3">
          <div class="card-body p-4">
            <div class="row align-items-center g-4">
              <div class="col-lg-8">
                <span class="badge ${obtenerClaseEstado(estado)} mb-2">
                  ${escaparHtml(estado)}
                </span>
                <h2 class="h5 mb-2">${escaparHtml(cita.servicio)}</h2>
                <p class="text-secondary mb-1">${escaparHtml(formatearFecha(cita.fecha))}
                  ·${escaparHtml(cita.hora)}
                </p>
                <p class="small text-secondary mb-0">${escaparHtml(cita.mascotaNombre || cita.paciente || "")}
                </p>
              </div>
              <div class="col-lg-4 text-lg-end">
                <small class="text-secondary d-block">Código</small>
                <div class="font-monospace mb-3">${escaparHtml(cita.id)}</div>
                ${
                  puedeCancelar
                    ? `
                  <button
                    type="button"
                    class="btn btn-outline-danger btn-sm"
                    data-cancelar-cita="${escaparHtml(cita.id)}">
                    Cancelar cita
                  </button>`
                    : ""
                }
              </div>
            </div>
          </div>
        </article>`;
      })
      .join("");

    contenedor.querySelectorAll("[data-cancelar-cita]").forEach((boton) => {
      boton.addEventListener("click", () =>
        cancelarCita(boton.dataset.cancelarCita),
      );
    });
  }

  function cancelarCita(idCita) {
    const confirmar = window.confirm(
      '¿Deseas cancelar esta cita? Esta acción cambiará su estado a "cancelada".',
    );
    if (!confirmar) return;

    const citas = obtenerCitas();
    const indiceCita = citas.findIndex((cita) => cita.id === idCita);

    if (indiceCita === -1) {
      mostrarToast("No se encontró la cita active.", "danger");
      return;
    }

    const estadoActual = citas[indiceCita].estado || "pendiente";
    if (!["pendiente", "confirmada"].includes(estadoActual)) {
      mostrarToast("Esta cita ya no puede ser cancelada.", "warning");
      return;
    }

    citas[indiceCita].estado = "cancelada";
    citas[indiceCita].fechaCancelacion = new Date().toISOString();
    guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.citas, citas);

    mostrarToast("La cita fue cancelada correctamente.", "success");
    configurarReservas();
  }

  function configurarCitas() {
    const contenedorFormulario = document.getElementById("formularioCita");
    if (!contenedorFormulario) return;

    const sesion = obtenerSesion();
    const cuenta = obtenerCuenta();
    const mascotas = obtenerMascotas();
    const mensajeInicioSesion = document.getElementById("mensajeInicioSesion");
    const mensajeSinMascotas = document.getElementById("mensajeSinMascotas");

    if (!sesion || sesion.rol !== "usuario" || !cuenta) {
      mensajeInicioSesion?.classList.remove("d-none");
      return;
    }

    if (!mascotas.length) {
      mensajeSinMascotas?.classList.remove("d-none");
      return;
    }

    contenedorFormulario.classList.remove("d-none");

    const servicios = obtenerServicios().filter((servicio) => servicio.activo);
    const estadoCita = {
      servicio: null,
      mascota: null,
      fecha: "",
      hora: "",
      notas: "",
    };

    const opcionesServicios = document.getElementById("opcionesServicios");
    const opcionesMascotas = document.getElementById("opcionesMascotas");
    const opcionesHorarios = document.getElementById("opcionesHorarios");
    const campoFecha = document.getElementById("citaFecha");
    const campoNotas = document.getElementById("citaNotas");
    const botonConfirmar = document.getElementById("botonConfirmarCita");

    campoFecha.min = obtenerFechaActual();

    servicios.forEach((servicio) => {
      const boton = document.createElement("button");
      boton.type = "button";
      boton.className = "btn btn-outline-primary text-start";
      boton.innerHTML = `<span>${escaparHtml(servicio.nombre)}</span>`;
      boton.addEventListener("click", () => {
        estadoCita.servicio = servicio;
        renderizar();
      });
      opcionesServicios.appendChild(boton);
    });

    mascotas.forEach((mascota) => {
      const boton = document.createElement("button");
      boton.type = "button";
      boton.className = "btn btn-outline-primary text-start";
      boton.innerHTML = `
        <div class="fw-semibold">${escaparHtml(mascota.nombre)}</div>
        <small class="text-secondary">${escaparHtml(mascota.especie)} · ${escaparHtml(mascota.edad || "")}</small>`;
      boton.addEventListener("click", () => {
        estadoCita.mascota = mascota;
        renderizar();
      });
      opcionesMascotas.appendChild(boton);
    });

    campoFecha.addEventListener("change", () => {
      estadoCita.fecha = campoFecha.value;
      estadoCita.hora = "";
      renderizar();
    });

    campoNotas.addEventListener("input", () => {
      estadoCita.notas = campoNotas.value;
    });

    botonConfirmar.addEventListener("click", confirmarCita);

    function obtenerHorariosOcupados() {
      return obtenerCitas()
        .filter(
          (cita) =>
            cita.fecha === estadoCita.fecha && cita.estado !== "cancelada",
        )
        .map((cita) => cita.hora);
    }

    function renderizarHorarios() {
      opcionesHorarios.innerHTML = "";

      if (!estadoCita.fecha) {
        opcionesHorarios.innerHTML =
          '<p class="small text-secondary">Selecciona una fecha primero.</p>';
        return;
      }

      const horariosOcupados = obtenerHorariosOcupados();
      HORARIOS_DISPONIBLES.forEach((hora) => {
        const ocupado =
          HORARIOS_BLOQUEADOS.includes(hora) || horariosOcupados.includes(hora);
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "btn btn-outline-primary text-start col-6 col-md-4";
        boton.dataset.opcionCita = "horario";
        boton.disabled = ocupado;
        boton.textContent = ocupado ? `${hora} · Ocupado` : hora;

        if (estadoCita.hora === hora) boton.classList.add("active");
        if (!ocupado) {
          boton.addEventListener("click", () => {
            estadoCita.hora = hora;
            renderizar();
          });
        }

        opcionesHorarios.appendChild(boton);
      });
    }

    function renderizar() {
      opcionesServicios
        .querySelectorAll(".btn.btn-outline-primary.text-start")
        .forEach((boton, indice) => {
          boton.classList.toggle(
            "active",
            servicios[indice] === estadoCita.servicio,
          );
        });

      opcionesMascotas
        .querySelectorAll(".btn.btn-outline-primary.text-start")
        .forEach((boton, indice) => {
          boton.classList.toggle(
            "active",
            mascotas[indice] === estadoCita.mascota,
          );
        });

      renderizarHorarios();

      const faltantes = [];
      if (!estadoCita.servicio) faltantes.push("servicio");
      if (!estadoCita.mascota) faltantes.push("mascota");
      if (!estadoCita.fecha) faltantes.push("fecha");
      if (!estadoCita.hora) faltantes.push("horario");

      document.getElementById("resumenCita").textContent = faltantes.length
        ? `Falta seleccionar: ${faltantes.join(", ")}.`
        : `${estadoCita.servicio.nombre} · ${estadoCita.mascota.nombre} · ${formatearFecha(estadoCita.fecha)} · ${estadoCita.hora}`;

      botonConfirmar.disabled = faltantes.length > 0;
    }

    function confirmarCita() {
      if (
        !estadoCita.servicio ||
        !estadoCita.mascota ||
        !estadoCita.fecha ||
        !estadoCita.hora
      )
        return;

      const citas = obtenerCitas();
      const nuevaCita = {
        id: `C-${Date.now()}`,
        nombrePropietario: `${cuenta.nombre} ${cuenta.apellido}`.trim(),
        correoPropietario: cuenta.correo,
        telefonoPropietario: cuenta.telefono,
        dniPropietario: cuenta.dni,
        mascotaId: estadoCita.mascota.id,
        mascotaNombre: estadoCita.mascota.nombre,
        mascotaEspecie: estadoCita.mascota.especie,
        servicio: estadoCita.servicio.nombre,
        fecha: estadoCita.fecha,
        hora: estadoCita.hora,
        notas: estadoCita.notas,
        estado: "pendiente",
      };

      citas.push(nuevaCita);
      guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.citas, citas);
      document.getElementById("tarjetaReserva")?.classList.add("d-none");
      document.getElementById("mensajeExitoCita")?.classList.remove("d-none");
      mostrarToast("Cita reservada correctamente.");
    }

    renderizar();
  }

  window.VetLifeModulos = window.VetLifeModulos || {};
  window.VetLifeModulos.citas = {
    iniciar() {
      configurarReservas();
      configurarCitas();
    },
  };
})();
