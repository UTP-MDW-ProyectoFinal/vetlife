(function () {
  "use strict";

  const {
    obtenerCuenta,
    obtenerMascotas,
    obtenerCitas,
    obtenerServicios,
    guardarAlmacenamiento,
    escaparHtml,
    obtenerFechaActual,
    formatearFecha,
  } = window.VetLife;

  function citasAdministrativas() {
    const citasUsuario = obtenerCitas().map((cita) => ({
      id: cita.id,
      fecha: cita.fecha,
      hora: cita.hora,
      paciente: cita.mascotaNombre,
      propietario: cita.nombrePropietario,
      servicio: cita.servicio,
      veterinario: "Por asignar",
      telefono: cita.telefonoPropietario,
      estado: cita.estado,
    }));

    return [...VetLifeDatos.citasDemo, ...citasUsuario];
  }

  function renderizarDashboard() {
    const citas = citasAdministrativas();
    const contenedor = document.getElementById("resumenAdmin");
    if (!contenedor) return;

    const hoy = obtenerFechaActual();
    const citasHoy = citas.filter((cita) => cita.fecha === hoy).length;
    const pendientes = citas.filter(
      (cita) => cita.estado === "pendiente",
    ).length;
    const pacientes =
      VetLifeDatos.pacientesDemo.length + obtenerMascotas().length;
    const serviciosActivos = obtenerServicios().filter(
      (servicio) => servicio.activo,
    ).length;

    const estadisticas = [
      ["Citas de hoy", citasHoy],
      ["Pendientes", pendientes],
      ["Pacientes", pacientes],
      ["Servicios activos", serviciosActivos],
    ];

    contenedor.innerHTML = estadisticas
      .map(
        ([titulo, valor]) => `
      <div class="col-sm-6 col-xl-3">
        <div class="card p-4 h-100">
          <div class="d-flex justify-content-between">
            <span class="small text-secondary">${escaparHtml(titulo)}</span>
          </div>
          <div class="display-6 font-heading mt-2">${valor}</div>
        </div>
      </div>`,
      )
      .join("");

    const agenda = document.getElementById("agendaAdmin");
    if (agenda) {
      agenda.innerHTML = citas
        .slice(0, 5)
        .map(
          (cita) => `
        <tr>
          <td>${escaparHtml(cita.hora)}</td>
          <td>${escaparHtml(cita.paciente)}</td>
          <td>${escaparHtml(cita.servicio)}</td>
          <td><span class="badge text-bg-light">${escaparHtml(cita.estado)}</span></td>
        </tr>`,
        )
        .join("");
    }

    const fecha = document.getElementById("fechaAdministrador");
    if (fecha) {
      fecha.textContent = new Date().toLocaleDateString("es-PE", {
        weekday: "long",
        day: "2-digit",
        month: "long",
        year: "numeric",
      });
    }
  }

  function renderizarAdminCitas() {
    const cuerpoTabla = document.getElementById("cuerpoTablaCitas");
    if (!cuerpoTabla) return;

    const buscador = document.getElementById("buscarCita");
    const botonesFiltro = document.querySelectorAll("[data-filtro-cita]");
    let filtroActual = "todas";
    let busquedaActual = "";

    const renderizar = () => {
      const citas = citasAdministrativas();
      const textoBusqueda = busquedaActual.toLowerCase();
      const citasFiltradas = citas.filter(
        (cita) =>
          (filtroActual === "todas" || cita.estado === filtroActual) &&
          (!textoBusqueda ||
            [cita.id, cita.paciente, cita.propietario, cita.servicio]
              .join(" ")
              .toLowerCase()
              .includes(textoBusqueda)),
      );

      botonesFiltro.forEach((boton) => {
        const activo = boton.dataset.filtroCita === filtroActual;
        boton.classList.toggle("btn-primary", activo);
        boton.classList.toggle("btn-outline-primary", !activo);
      });

      cuerpoTabla.innerHTML = citasFiltradas.length
        ? citasFiltradas
            .map(
              (cita) => `
          <tr class="cursor-pointer" data-id="${escaparHtml(cita.id)}">
            <td class="font-monospace small">${escaparHtml(cita.id)}</td>
            <td>${escaparHtml(formatearFecha(cita.fecha))}</td>
            <td class="fw-semibold">${escaparHtml(cita.hora)}</td>
            <td><strong>${escaparHtml(cita.paciente)}</strong><div class="small text-secondary">${escaparHtml(cita.propietario)}</div></td>
            <td>${escaparHtml(cita.servicio)}</td>
            <td>${escaparHtml(cita.veterinario)}</td>
            <td><span class="badge text-bg-light">${escaparHtml(cita.estado)}</span></td>
          </tr>`,
            )
            .join("")
        : '<tr><td colspan="7" class="text-center text-secondary py-5">No se encontraron citas.</td></tr>';

      cuerpoTabla.querySelectorAll("tr[data-id]").forEach((fila) => {
        fila.addEventListener("click", () =>
          mostrarDetalleCita(fila.dataset.id, citas),
        );
      });
    };

    botonesFiltro.forEach((boton) => {
      boton.addEventListener("click", () => {
        filtroActual = boton.dataset.filtroCita;
        renderizar();
      });
    });

    buscador?.addEventListener("input", () => {
      busquedaActual = buscador.value;
      renderizar();
    });

    renderizar();
  }

  function mostrarDetalleCita(id, citas) {
    const cita = citas.find((citaActual) => citaActual.id === id);
    const panel = document.getElementById("detalleCita");
    if (!cita || !panel) return;

    panel.classList.remove("d-none");
    panel.innerHTML = `
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h5 mb-0">Detalle de cita</h2>
        <button class="btn btn-sm btn-outline-secondary" aria-label="Cerrar">Cerrar</button>
      </div>
      <div class="bg-primary-subtle rounded p-3 mb-3">
        <div class="font-monospace small">${escaparHtml(cita.id)}</div>
        <div class="h3 font-heading text-primary">${escaparHtml(cita.hora)}</div>
        <div class="small text-secondary">${escaparHtml(formatearFecha(cita.fecha))}</div>
      </div>
      <dl class="row small">
        <dt class="col-5 text-secondary">Paciente</dt><dd class="col-7">${escaparHtml(cita.paciente)}</dd>
        <dt class="col-5 text-secondary">Propietario</dt><dd class="col-7">${escaparHtml(cita.propietario)}</dd>
        <dt class="col-5 text-secondary">Teléfono</dt><dd class="col-7">${escaparHtml(cita.telefono || "—")}</dd>
        <dt class="col-5 text-secondary">Servicio</dt><dd class="col-7">${escaparHtml(cita.servicio)}</dd>
        <dt class="col-5 text-secondary">Veterinario</dt><dd class="col-7">${escaparHtml(cita.veterinario)}</dd>
      </dl>`;

    panel
      .querySelector(".btn-close")
      .addEventListener("click", () => panel.classList.add("d-none"));
  }

  function pacientesAdministrativos() {
    const cuenta = obtenerCuenta();
    const mascotasUsuario = obtenerMascotas().map((mascota, indice) => ({
      id: `U-${indice + 1}`,
      nombre: mascota.nombre,
      especie: mascota.especie,
      raza: mascota.raza || "Sin raza",
      edad: mascota.edad || "No indicada",
      propietario: cuenta
        ? `${cuenta.nombre} ${cuenta.apellido}`.trim()
        : "Usuario VetLife",
      telefono: cuenta?.telefono || "—",
      ultimaVisita: "Nueva",
      visitas: 0,
      estado: "activo",
    }));

    return [...VetLifeDatos.pacientesDemo, ...mascotasUsuario];
  }

  function renderizarAdminPacientes() {
    const grid = document.getElementById("gridPacientes");
    if (!grid) return;

    const buscador = document.getElementById("buscarPaciente");
    const filtroEspecie = document.getElementById("filtroEspecie");
    const contador = document.getElementById("contadorPacientes");

    const renderizar = () => {
      const textoBusqueda = (buscador?.value || "").trim().toLowerCase();
      const especieActual = filtroEspecie?.value || "todas";
      const pacientes = pacientesAdministrativos();

      const pacientesFiltrados = pacientes.filter((paciente) => {
        const coincideEspecie =
          especieActual === "todas" || paciente.especie === especieActual;
        const datosPaciente = [
          paciente.nombre,
          paciente.propietario,
          paciente.raza,
          paciente.especie,
        ]
          .join(" ")
          .toLowerCase();
        return (
          coincideEspecie &&
          (!textoBusqueda || datosPaciente.includes(textoBusqueda))
        );
      });

      if (contador) {
        contador.textContent = `${pacientesFiltrados.length} paciente${pacientesFiltrados.length === 1 ? "" : "s"}`;
      }

      grid.innerHTML = pacientesFiltrados.length
        ? pacientesFiltrados
            .map(
              (paciente) => `
          <div class="col">
            <article class="card h-100 shadow-sm">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start gap-3">
                  <div class="d-flex align-items-center gap-3">
                    <div>
                      <h3 class="h5 mb-1">${escaparHtml(paciente.nombre)}</h3>
                      <p class="small text-secondary mb-0">${escaparHtml(paciente.especie)} · ${escaparHtml(paciente.raza)}</p>
                    </div>
                  </div>
                  <span class="badge ${paciente.estado === "activo" ? "text-bg-success" : "text-bg-secondary"}">${escaparHtml(paciente.estado)}</span>
                </div>

                <hr>

                <dl class="row small mb-0">
                  <dt class="col-5 text-secondary">Propietario</dt>
                  <dd class="col-7">${escaparHtml(paciente.propietario)}</dd>
                  <dt class="col-5 text-secondary">Edad</dt>
                  <dd class="col-7">${escaparHtml(paciente.edad)}</dd>
                  <dt class="col-5 text-secondary">Visitas</dt>
                  <dd class="col-7">${paciente.visitas}</dd>
                </dl>
              </div>
              <div class="card-footer bg-transparent border-top-0 pt-0 pb-3 px-3">
                <button class="btn btn-outline-primary w-100" type="button" data-ver-paciente="${escaparHtml(paciente.id)}">Ver ficha
                </button>
              </div>
            </article>
          </div>`,
            )
            .join("")
        : `<div class="col-12">
            <div class="alert alert-light border text-center" role="status">
              No se encontraron pacientes con los criterios seleccionados.
            </div>
          </div>`;

      grid.querySelectorAll("[data-ver-paciente]").forEach((boton) => {
        boton.addEventListener("click", () =>
          mostrarDetallePaciente(boton.dataset.verPaciente, pacientes),
        );
      });
    };

    buscador?.addEventListener("input", renderizar);
    filtroEspecie?.addEventListener("change", renderizar);
    renderizar();
  }

  function mostrarDetallePaciente(id, pacientes) {
    const paciente = pacientes.find(
      (pacienteActual) => pacienteActual.id === id,
    );
    const detalle = document.getElementById("detallePaciente");
    const elementoModal = document.getElementById("modalPaciente");
    if (!paciente || !detalle || !elementoModal || !window.bootstrap?.Modal)
      return;

    detalle.innerHTML = `
      <div class="row g-4">
        <div class="col-md-4 text-center">
          <h3 class="h4 mb-1">${escaparHtml(paciente.nombre)}</h3>
          <p class="text-secondary mb-0">${escaparHtml(paciente.id)}</p>
          <span class="badge ${paciente.estado === "activo" ? "text-bg-success" : "text-bg-secondary"} mt-2">${escaparHtml(paciente.estado)}</span>
        </div>
        <div class="col-md-8">
          <h3 class="h6 text-uppercase text-secondary">Información</h3>
          <dl class="row">
            <dt class="col-sm-5">Especie</dt><dd class="col-sm-7">${escaparHtml(paciente.especie)}</dd>
            <dt class="col-sm-5">Raza</dt><dd class="col-sm-7">${escaparHtml(paciente.raza)}</dd>
            <dt class="col-sm-5">Edad</dt><dd class="col-sm-7">${escaparHtml(paciente.edad)}</dd>
            <dt class="col-sm-5">Propietario</dt><dd class="col-sm-7">${escaparHtml(paciente.propietario)}</dd>
            <dt class="col-sm-5">Teléfono</dt><dd class="col-sm-7">${escaparHtml(paciente.telefono)}</dd>
            <dt class="col-sm-5">Última visita</dt><dd class="col-sm-7">${escaparHtml(paciente.ultimaVisita)}</dd>
            <dt class="col-sm-5">Visitas registradas</dt><dd class="col-sm-7">${paciente.visitas}</dd>
          </dl>
        </div>
      </div>`;

    window.bootstrap.Modal.getOrCreateInstance(elementoModal).show();
  }

  function renderizarAdminServicios() {
    const tabla = document.getElementById("tablaServicios");
    if (!tabla) return;

    let servicios = obtenerServicios();
    const buscador = document.getElementById("buscarServicio");
    const filtroCategoria = document.getElementById("filtroCategoria");
    const filtroEstado = document.getElementById("filtroEstadoServicio");
    const contador = document.getElementById("contadorServicios");
    const estadisticas = document.getElementById("estadisticasServicios");

    const formatearDuracion =
      window.VetLifeModulos.servicios?.formatearDuracion ||
      ((minutos) => `${minutos} min`);

    const renderizar = () => {
      const textoBusqueda = (buscador?.value || "").trim().toLowerCase();
      const categoriaActual = filtroCategoria?.value || "todas";
      const estadoActual = filtroEstado?.value || "todos";

      const serviciosFiltrados = servicios.filter((servicio) => {
        const coincideTexto =
          !textoBusqueda ||
          [servicio.nombre, servicio.categoria]
            .join(" ")
            .toLowerCase()
            .includes(textoBusqueda);
        const coincideCategoria =
          categoriaActual === "todas" || servicio.categoria === categoriaActual;
        const coincideEstado =
          estadoActual === "todos" ||
          (estadoActual === "activos" && servicio.activo) ||
          (estadoActual === "inactivos" && !servicio.activo);
        return coincideTexto && coincideCategoria && coincideEstado;
      });

      if (contador) {
        contador.textContent = `${serviciosFiltrados.length} servicio${serviciosFiltrados.length === 1 ? "" : "s"}`;
      }

      if (estadisticas) {
        const precioPromedio = servicios.length
          ? Math.round(
              servicios.reduce(
                (total, servicio) => total + servicio.precio,
                0,
              ) / servicios.length,
            )
          : 0;
        estadisticas.innerHTML = [
          ["Total", servicios.length],
          ["Activos", servicios.filter((servicio) => servicio.activo).length],
          [
            "Inactivos",
            servicios.filter((servicio) => !servicio.activo).length,
          ],
          ["Precio promedio", `S/ ${precioPromedio}`],
        ]
          .map(
            ([titulo, valor]) => `
          <div class="col">
            <div class="card h-100 shadow-sm">
              <div class="card-body d-flex justify-content-between align-items-start">
                <div>
                  <p class="small text-secondary mb-1">${escaparHtml(titulo)}</p>
                  <p class="h4 mb-0">${escaparHtml(valor)}</p>
                </div>
              </div>
            </div>
          </div>`,
          )
          .join("");
      }

      tabla.innerHTML = serviciosFiltrados.length
        ? serviciosFiltrados
            .map(
              (servicio) => `
          <tr>
            <td>
              <div class="fw-semibold">${escaparHtml(servicio.nombre)}</div>
              <div class="small text-secondary">${escaparHtml(servicio.id)}</div>
            </td>
            <td>${escaparHtml(servicio.categoria)}</td>
            <td>${escaparHtml(formatearDuracion(servicio.duracion))}</td>
            <td class="fw-semibold">S/ ${escaparHtml(servicio.precio)}</td>
            <td>
              <span class="badge ${servicio.activo ? "text-bg-success" : "text-bg-secondary"}">
                ${servicio.activo ? "Activo" : "Inactivo"}
              </span>
            </td>
            <td class="text-end">
              <button class="btn btn-sm ${servicio.activo ? "btn-outline-secondary" : "btn-outline-primary"}" type="button" data-cambiar-servicio="${escaparHtml(servicio.id)}">
                ${servicio.activo ? "Desactivar" : "Activar"}
              </button>
            </td>
          </tr>`,
            )
            .join("")
        : `<tr><td colspan="6" class="text-center py-5 text-secondary">No se encontraron servicios.</td></tr>`;

      tabla.querySelectorAll("[data-cambiar-servicio]").forEach((boton) => {
        boton.addEventListener("click", () => {
          const idServicio = boton.dataset.cambiarServicio;
          servicios = servicios.map((servicio) =>
            servicio.id === idServicio
              ? { ...servicio, activo: !servicio.activo }
              : servicio,
          );
          guardarAlmacenamiento(
            window.VetLife.CLAVES_ALMACENAMIENTO.servicios,
            servicios,
          );
          renderizar();
        });
      });
    };

    buscador?.addEventListener("input", renderizar);
    filtroCategoria?.addEventListener("change", renderizar);
    filtroEstado?.addEventListener("change", renderizar);
    renderizar();
  }

  function configurarMenuAdministrador() {
    const botonMenu = document.getElementById("botonMenuAdmin");
    const barraLateral = document.getElementById("barraLateralAdmin");
    const superposicion = document.getElementById("superposicionAdmin");

    const cerrarMenu = () => {
      barraLateral?.classList.remove("mostrar");
      superposicion?.classList.add("d-none");
    };

    botonMenu?.addEventListener("click", () => {
      barraLateral?.classList.toggle("mostrar");
      superposicion?.classList.toggle("d-none");
    });

    superposicion?.addEventListener("click", cerrarMenu);
    barraLateral
      ?.querySelectorAll("a")
      .forEach((enlace) => enlace.addEventListener("click", cerrarMenu));
  }

  window.VetLifeModulos = window.VetLifeModulos || {};
  window.VetLifeModulos.administrador = {
    iniciar() {
      configurarMenuAdministrador();
      const paginaAdministrativa = document.body.dataset.adminPagina;
      if (paginaAdministrativa === "dashboard") renderizarDashboard();
      if (paginaAdministrativa === "citas") renderizarAdminCitas();
      if (paginaAdministrativa === "pacientes") renderizarAdminPacientes();
      if (paginaAdministrativa === "servicios") renderizarAdminServicios();
    },
  };
})();
