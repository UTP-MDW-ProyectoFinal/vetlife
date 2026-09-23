(function () {
  "use strict";

  const {
    CLAVES_ALMACENAMIENTO,
    obtenerCuenta,
    obtenerSesion,
    guardarAlmacenamiento,
    eliminarAlmacenamiento,
    escaparHtml,
    mostrarToast,
  } = window.VetLife;

  function obtenerRutaUsuario(nombreArchivo) {
    const estaEnUsuario = window.location.pathname.includes("/usuario/");
    return estaEnUsuario ? nombreArchivo : `usuario/${nombreArchivo}`;
  }

  function obtenerRutaInicio() {
    return window.location.pathname.includes("/usuario/")
      ? "../index.html"
      : "index.html";
  }

  function actualizarZonaUsuario() {
    const zonaUsuario = document.getElementById("zonaUsuario");
    if (!zonaUsuario) return;

    const sesion = obtenerSesion();
    const cuenta = obtenerCuenta();

    if (!sesion || sesion.rol !== "usuario" || !cuenta) {
      zonaUsuario.innerHTML = `
        <a class="nav-link" href="${obtenerRutaUsuario("login.html")}">Iniciar sesión</a>
        <a class="btn btn-primary btn-sm px-3" href="${obtenerRutaUsuario("registro.html")}">Crear cuenta</a>`;
      return;
    }

    const inicial = (cuenta.nombre || "U").trim().charAt(0).toUpperCase();
    zonaUsuario.innerHTML = `
      <div class="dropdown position-relative">
        <button
          type="button"
          class="btn btn-light border d-flex align-items-center gap-2"
          id="botonMenuUsuario"
          data-bs-toggle="dropdown"
          aria-expanded="false"
          aria-haspopup="true">
          <span class="rounded-circle bg-primary-subtle text-primary d-inline-flex align-items-center justify-content-center fw-bold">${escaparHtml(inicial)}</span>
          <span>${escaparHtml((cuenta.nombre || "Usuario").split(" ")[0])}</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end shadow-sm" id="menuUsuario" aria-labelledby="botonMenuUsuario">
          <li>
            <div class="px-3 py-2">
              <div class="fw-semibold">${escaparHtml(`${cuenta.nombre || ""} ${cuenta.apellido || ""}`.trim())}</div>
              <small class="text-secondary">${escaparHtml(cuenta.correo || "")}</small>
            </div>
          </li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item" href="${obtenerRutaUsuario("perfil.html")}">Mi perfil</a></li>
          <li><a class="dropdown-item" href="${obtenerRutaUsuario("reservas.html")}">Mis reservas</a></li>
          <li><a class="dropdown-item" href="${obtenerRutaUsuario("mascota.html")}">Registrar mascota</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><button type="button" class="dropdown-item text-danger" id="botonCerrarSesion">Cerrar sesión</button></li>
        </ul>
      </div>`;

    document
      .getElementById("botonCerrarSesion")
      ?.addEventListener("click", () => {
        eliminarAlmacenamiento(CLAVES_ALMACENAMIENTO.sesion);
        window.location.href = obtenerRutaInicio();
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

  function configurarFormularioInicioSesion() {
    const formulario = document.getElementById("formularioInicioSesion");
    if (!formulario) return;

    formulario.addEventListener("submit", (evento) => {
      evento.preventDefault();

      const usuario = document.getElementById("usuarioInicio").value.trim();
      const contrasena = document.getElementById("contrasenaInicio").value;
      const administrador = VetLifeDatos.credencialesAdministrador.find(
        (credencial) =>
          credencial.usuario.toLowerCase() === usuario.toLowerCase() &&
          credencial.contrasena === contrasena,
      );

      if (administrador) {
        guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.sesion, {
          rol: "administrador",
          nombre: administrador.nombre,
          correo: administrador.correo,
        });
        window.location.href = "../admin/dashboard.html";
        return;
      }

      const cuenta = obtenerCuenta();
      const identificadorCorrecto =
        cuenta &&
        ((cuenta.correo || "").toLowerCase() === usuario.toLowerCase() ||
          String(cuenta.dni || "") === usuario);

      if (
        !cuenta ||
        !identificadorCorrecto ||
        cuenta.contrasena !== contrasena
      ) {
        mostrarToast(
          "El correo/DNI o la contraseña no son correctos.",
          "danger",
        );
        return;
      }

      guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.sesion, {
        rol: "usuario",
        nombre: cuenta.nombre,
        correo: cuenta.correo,
      });

      const redireccion = new URLSearchParams(window.location.search).get(
        "redirect",
      );
      window.location.href =
        redireccion === "citas.html" ? "citas.html" : "reservas.html";
    });
  }

  function configurarFormularioRegistro() {
    const formulario = document.getElementById("formularioRegistro");
    if (!formulario) return;

    formulario.addEventListener("submit", (evento) => {
      evento.preventDefault();

      const contrasena = document.getElementById("registroContrasena").value;
      const confirmarContrasena = document.getElementById(
        "registroConfirmarContrasena",
      ).value;
      const correo = document
        .getElementById("registroCorreo")
        .value.trim()
        .toLowerCase();
      const cuentaExistente = obtenerCuenta();

      if (contrasena !== confirmarContrasena) {
        mostrarToast("Las contraseñas no coinciden.", "danger");
        return;
      }

      if (cuentaExistente && cuentaExistente.correo === correo) {
        mostrarToast("Ya existe una cuenta con ese correo.", "danger");
        return;
      }

      const cuenta = {
        nombre: document.getElementById("registroNombre").value.trim(),
        apellido: document.getElementById("registroApellido").value.trim(),
        dni: document.getElementById("registroDni").value.trim(),
        telefono: document.getElementById("registroTelefono").value.trim(),
        correo,
        contrasena,
      };

      guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.cuenta, cuenta);
      guardarAlmacenamiento(CLAVES_ALMACENAMIENTO.sesion, {
        rol: "usuario",
        nombre: cuenta.nombre,
        correo: cuenta.correo,
      });
      window.location.href = "mascota.html";
    });
  }

  window.VetLifeModulos = window.VetLifeModulos || {};
  window.VetLifeModulos.autenticacion = {
    iniciar() {
      actualizarZonaUsuario();
      configurarFormularioInicioSesion();
      configurarFormularioRegistro();
      configurarMostrarContrasena();
    },
    actualizarZonaUsuario,
  };
})();
