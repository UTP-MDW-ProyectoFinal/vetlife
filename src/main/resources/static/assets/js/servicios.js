(function () {
  "use strict";

  const { obtenerServicios, escaparHtml } = window.VetLife;

  function formatearDuracion(minutos) {
    if (minutos >= 1440) return `${Math.round(minutos / 1440)} día(s)`;
    if (minutos >= 60) return `${Math.round(minutos / 60)} h`;
    return `${minutos} min`;
  }

  function configurarServiciosPublicos() {
    const contenedor = document.getElementById("listaServiciosPublicos");
    if (!contenedor) return;

    const servicios = obtenerServicios().filter((servicio) => servicio.activo);
    const categorias = [
      ...new Set(servicios.map((servicio) => servicio.categoria)),
    ];

    contenedor.innerHTML = categorias
      .map(
        (categoria) => `
      <section class="mb-5">
        <div class="d-flex align-items-center gap-2 mb-3">
          <span class="badge bg-primary-subtle text-primary">${escaparHtml(categoria)}</span>
          <span class="text-secondary small">${servicios.filter((servicio) => servicio.categoria === categoria).length} servicios</span>
        </div>
        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
          ${servicios
            .filter((servicio) => servicio.categoria === categoria)
            .map(
              (servicio) => `
            <article class="col">
              <div class="card shadow-sm h-100">
                  <div class="card-body p-4">
                      <div class="d-inline-flex align-items-center justify-content-center rounded-circle bg-primary-subtle text-primary mb-3" style="width:48px;height:48px;">
                          <i class="bi ${escaparHtml(servicio.icono || "bi-heart-pulse")} fs-4"></i>
                              </div>
                          <h2 class="h5">${escaparHtml(servicio.nombre)}</h2>
                  <p class="small text-secondary mb-3">Atención veterinaria con seguimiento profesional y registro digital.</p>
                  <div class="d-flex justify-content-between small">
                    <span>${escaparHtml(formatearDuracion(servicio.duracion))}</span>
                    <strong class="text-primary">S/ ${servicio.precio}</strong>
                  </div>
                </div>
              </div>
            </article>`,
            )
            .join("")}
        </div>
      </section>`,
      )
      .join("");
  }

function configurarInicio() {
  const contenedorServicios = document.getElementById("serviciosInicio");
  if (contenedorServicios) {
    contenedorServicios.innerHTML = obtenerServicios()
      .filter((servicio) => servicio.activo)
      .slice(0, 6)
      .map(
        (servicio) => `
        <div class="col">
          <div class="card shadow-sm h-100">
            <div class="card-body p-4">
              <div class="d-inline-flex align-items-center justify-content-center rounded-circle bg-primary-subtle text-primary mb-3" style="width:48px;height:48px;">
                <i class="bi ${escaparHtml(servicio.icono || "bi-heart-pulse")} fs-4"></i>
              </div>
              <h3 class="h6">${escaparHtml(servicio.nombre)}</h3>
              <p class="small text-secondary mb-0">${escaparHtml(servicio.categoria)}</p>
            </div>
          </div>
        </div>`,
      )
      .join("");
  }

    const contenedorPasos = document.getElementById("pasosInicio");
    if (contenedorPasos) {
      contenedorPasos.innerHTML = VetLifeDatos.pasos
        .map(
          (paso) => `
        <div class="col-md-4">
          <div class="card shadow-sm h-100">
            <div class="card-body p-4">
              <span class="badge bg-primary-subtle text-primary mb-3">${escaparHtml(paso.numero)}</span>
              <h3 class="h5">${escaparHtml(paso.titulo)}</h3>
              <p class="small text-secondary mb-0">${escaparHtml(paso.texto)}</p>
            </div>
          </div>
        </div>`,
        )
        .join("");
    }
  }

  window.VetLifeModulos = window.VetLifeModulos || {};
  window.VetLifeModulos.servicios = {
    iniciar() {
      configurarServiciosPublicos();
      configurarInicio();
    },
    formatearDuracion,
  };
})();
