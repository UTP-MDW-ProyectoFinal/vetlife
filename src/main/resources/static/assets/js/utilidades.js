(function () {
  'use strict';

  const CLAVES_ALMACENAMIENTO = {
    cuenta: 'vetlife_cuenta_v2',
    mascotas: 'vetlife_mascotas_v2',
    citas: 'vetlife_citas_v2',
    sesion: 'vetlife_sesion_v2',
    servicios: 'vetlife_servicios_v2'
  };

  const HORARIOS_DISPONIBLES = [
    '08:00', '08:30', '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
    '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00', '17:30'
  ];

  const HORARIOS_BLOQUEADOS = ['09:00', '10:30', '14:00', '15:30'];
function leerAlmacenamiento(clave, valorPorDefecto) {
    try {
      return JSON.parse(localStorage.getItem(clave) || JSON.stringify(valorPorDefecto));
    } catch (error) {
      console.warn(`No se pudo leer la clave de almacenamiento: ${clave}`, error);
      return valorPorDefecto;
    }
  }

  function guardarAlmacenamiento(clave, valor) {
    localStorage.setItem(clave, JSON.stringify(valor));
  }

  function eliminarAlmacenamiento(clave) {
    localStorage.removeItem(clave);
  }

  function obtenerCuenta() {
    return leerAlmacenamiento(CLAVES_ALMACENAMIENTO.cuenta, null);
  }

  function obtenerMascotas() {
    return leerAlmacenamiento(CLAVES_ALMACENAMIENTO.mascotas, []);
  }

  function obtenerCitas() {
    return leerAlmacenamiento(CLAVES_ALMACENAMIENTO.citas, []);
  }

  function obtenerSesion() {
    return leerAlmacenamiento(CLAVES_ALMACENAMIENTO.sesion, null);
  }

  function obtenerServicios() {
    return leerAlmacenamiento(CLAVES_ALMACENAMIENTO.servicios, VetLifeDatos.servicios);
  }

  function escaparHtml(valor) {
    return String(valor ?? '').replace(/[&<>"']/g, caracter => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;'
    }[caracter]));
  }

  function mostrarToast(mensaje, tipo = 'success') {
    const contenedor = document.getElementById('contenedorToast');
    if (!contenedor) return;

    contenedor.innerHTML = `<div class="alert alert-${tipo} shadow-sm mb-0" role="alert">${escaparHtml(mensaje)}</div>`;
    window.setTimeout(() => {
      contenedor.innerHTML = '';
    }, 3500);
  }

  function obtenerFechaActual() {
    const hoy = new Date();
    const anio = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const dia = String(hoy.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }

  function formatearFecha(fecha) {
    return new Date(`${fecha}T00:00:00`).toLocaleDateString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  window.VetLife = {
    CLAVES_ALMACENAMIENTO,
    HORARIOS_DISPONIBLES,
    HORARIOS_BLOQUEADOS,
    leerAlmacenamiento,
    guardarAlmacenamiento,
    eliminarAlmacenamiento,
    obtenerCuenta,
    obtenerMascotas,
    obtenerCitas,
    obtenerSesion,
    obtenerServicios,
    escaparHtml,
    mostrarToast,
    obtenerFechaActual,
    formatearFecha
  };
}());
