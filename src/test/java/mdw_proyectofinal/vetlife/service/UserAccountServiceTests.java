package mdw_proyectofinal.vetlife.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

class UserAccountServiceTests {

    @TempDir
    Path directorioTemporal;

    @Test
    void guardaLaCuentaYLaRecuperaAlCrearOtroServicio() throws Exception {
        Path archivo = directorioTemporal.resolve("cuentas.json");
        UserAccountService primerServicio = crearServicio(archivo);

        assertTrue(primerServicio.registrar(
                "Ana", "García", "12345678", "900123456", "ana@example.com", "Vetlife123"));

        String contenido = Files.readString(archivo);
        assertTrue(contenido.contains("ana@example.com"));
        assertFalse(contenido.contains("Vetlife123"));

        UserAccountService segundoServicio = crearServicio(archivo);
        assertNotNull(segundoServicio.autenticar("12345678", "Vetlife123"));
        assertNull(segundoServicio.autenticar("ana@example.com", "incorrecta"));
        assertEquals("ana@example.com", segundoServicio.autenticar("ana@example.com", "Vetlife123").correo());
    }

    @Test
    void rechazaCorreoODniDuplicadosSinSobrescribirLaCuenta() {
        UserAccountService servicio = crearServicio(directorioTemporal.resolve("cuentas.json"));
        assertTrue(servicio.registrar("Ana", "García", "12345678", "900123456", "ana@example.com", "Vetlife123"));

        assertFalse(servicio.registrar("Otra", "Persona", "87654321", "900654321", "ANA@example.com", "Vetlife456"));
        assertFalse(servicio.registrar("Otra", "Persona", "12345678", "900654321", "otra@example.com", "Vetlife456"));
        assertEquals("Ana", servicio.autenticar("ana@example.com", "Vetlife123").nombre());
    }

    private UserAccountService crearServicio(Path archivo) {
        return new UserAccountService(JsonMapper.builder().build(), archivo.toString());
    }
}