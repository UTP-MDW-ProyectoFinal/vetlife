package mdw_proyectofinal.vetlife;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.repository.AdminRepository;

/**
 * Pruebas automaticas de P6: GET y POST /api/v1/admin/doctores.
 * Usan una BD de pruebas separada (vetlife_test) para no tocar la de desarrollo.
 * Los correos y licencias del POST usan un sufijo unico por ejecucion porque la
 * BD de pruebas persiste entre corridas (mysql ddl-auto=update).
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/vetlife_test?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminDoctoresApiTest {

    private static final long RUN = System.currentTimeMillis();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void asegurarAdminAdicional() {
        // Admin no-Adam (id 2) para el caso de 403 en POST.
        if (adminRepository.buscarPorUsuarioOCorreo("admin2@vetlife.com").isEmpty()) {
            adminRepository.guardar(new Admin(0, "Test", "AdminDos", "testadmin", "admin2@vetlife.com",
                    "Prueba12345", "Administrador"));
        }
    }

    @Test
    void getDoctores_conCredencialesDevuelveListaOrdenada() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        DoctorResponseRow[] doctores = DoctorResponseRow.desde(resultado.getResponse().getContentAsString());
        assertThat(doctores).isNotEmpty();

        // Orden por apellido y nombre, ascendente.
        String apellidoPrevio = null;
        for (DoctorResponseRow d : doctores) {
            if (apellidoPrevio != null && d.apellido != null) {
                assertThat(d.apellido.compareToIgnoreCase(apellidoPrevio) >= 0).isTrue();
            }
            apellidoPrevio = d.apellido;
        }
    }

    @Test
    void getDoctores_filtroEspecialidad_devuelveSoloEsaEspecialidad() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/api/v1/admin/doctores?especialidadId=1")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        DoctorResponseRow[] doctores = DoctorResponseRow.desde(resultado.getResponse().getContentAsString());
        assertThat(doctores).isNotEmpty();
        assertThat(doctores).allMatch(d -> d.especialidadId == 1);
    }

    @Test
    void getDoctores_filtroActivoFalse_devuelveVacio() throws Exception {
        // En la BD de pruebas nadie se crea inactivo, siempre es vacio.
        mockMvc.perform(get("/api/v1/admin/doctores?activo=false")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getDoctores_filtroActivoInvalido_responde400() throws Exception {
        mockMvc.perform(get("/api/v1/admin/doctores?activo=nonsense")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDoctores_especialidadInexistente_responde404() throws Exception {
        mockMvc.perform(get("/api/v1/admin/doctores?especialidadId=999999")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDoctores_sinCredenciales_responde401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/doctores"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getDoctores_passwordInvalida_responde401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "incorrecta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postDoctor_feliz_responde201ConLocationYActivo() throws Exception {
        String email = "dr." + RUN + "@vetlife.com";
        String licencia = "TM-" + RUN;

        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\":\"Test Nombre\","
                                + "\"apellido\":\"Test Apellido\","
                                + "\"especialidadId\":2,"
                                + "\"email\":\"" + email + "\","
                                + "\"telefono\":\"987654321\","
                                + "\"numeroLicencia\":\"" + licencia + "\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.especialidadNombre").value("Cirugia"))
                .andExpect(jsonPath("$.numeroLicencia").value(licencia));
    }

    @Test
    void postDoctor_emailDuplicado_responde409() throws Exception {
        String email = "dup." + RUN + "@vetlife.com";
        String cuerpo1 = "{"
                + "\"nombre\":\"Dup A\","
                + "\"apellido\":\"Dup Apellido\","
                + "\"especialidadId\":1,"
                + "\"email\":\"" + email + "\","
                + "\"numeroLicencia\":\"DM-" + RUN + "A\""
                + "}";
        String cuerpo2 = "{"
                + "\"nombre\":\"Dup B\","
                + "\"apellido\":\"Dup Apellido\","
                + "\"especialidadId\":2,"
                + "\"email\":\"" + email + "\","
                + "\"numeroLicencia\":\"DM-" + RUN + "B\""
                + "}";

        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content(cuerpo1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content(cuerpo2))
                .andExpect(status().isConflict());
    }

    @Test
    void postDoctor_licenciaDuplicada_responde409() throws Exception {
        String email1 = "lic." + RUN + "a@vetlife.com";
        String email2 = "lic." + RUN + "b@vetlife.com";
        String licencia = "LM-" + RUN;

        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\":\"Lic A\","
                                + "\"apellido\":\"Lic Apellido\","
                                + "\"especialidadId\":1,"
                                + "\"email\":\"" + email1 + "\","
                                + "\"numeroLicencia\":\"" + licencia + "\""
                                + "}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\":\"Lic B\","
                                + "\"apellido\":\"Lic Apellido\","
                                + "\"especialidadId\":3,"
                                + "\"email\":\"" + email2 + "\","
                                + "\"numeroLicencia\":\"" + licencia + "\""
                                + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void postDoctor_especialidadInexistente_responde404() throws Exception {
        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\":\"Nie Esp\","
                                + "\"apellido\":\"Esp Apellido\","
                                + "\"especialidadId\":999999,"
                                + "\"email\":\"nie." + RUN + "@vetlife.com\","
                                + "\"numeroLicencia\":\"NM-" + RUN + "\""
                                + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postDoctor_cuerpoInvalido_responde400ConDetalle() throws Exception {
        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\":\"X\","
                                + "\"apellido\":\"Salas Rios\","
                                + "\"especialidadId\":2,"
                                + "\"email\":\"no-es-correo\","
                                + "\"numeroLicencia\":\"AB/erroneo\""
                                + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty())
                .andExpect(jsonPath("$.numeroLicencia").isNotEmpty());
    }

    @Test
    void postDoctor_sinCredenciales_responde401() throws Exception {
        mockMvc.perform(post("/api/v1/admin/doctores")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postDoctor_adminNoAdam_responde403() throws Exception {
        // El admin con id 2 (no Adam) no puede crear doctores.
        mockMvc.perform(post("/api/v1/admin/doctores")
                        .header("email", "admin2@vetlife.com")
                        .header("password", "Prueba12345")
                        .contentType("application/json")
                        .content("{"
                                + "\"nombre\":\"Inv X\","
                                + "\"apellido\":\"Inv Apellido\","
                                + "\"especialidadId\":1,"
                                + "\"email\":\"inv." + RUN + "@vetlife.com\","
                                + "\"numeroLicencia\":\"IM-" + RUN + "\""
                                + "}"))
                .andExpect(status().isForbidden());
    }

    /** Lectura minima del JSON de respuesta para las aserciones del listado. */
    private static final class DoctorResponseRow {
        long id;
        int especialidadId;
        String apellido;

        static DoctorResponseRow[] desde(String json) {
            List<Map<String, Object>> lista = JsonPath.read(json, "$");
            DoctorResponseRow[] filas = new DoctorResponseRow[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
                Map<String, Object> nodo = lista.get(i);
                DoctorResponseRow fila = new DoctorResponseRow();
                Object idNodo = nodo.get("id");
                fila.id = idNodo == null ? -1 : ((Number) idNodo).longValue();
                Object espNodo = nodo.get("especialidadId");
                fila.especialidadId = espNodo == null ? -1 : ((Number) espNodo).intValue();
                Object apeNodo = nodo.get("apellido");
                fila.apellido = apeNodo == null ? null : apeNodo.toString();
                filas[i] = fila;
            }
            return filas;
        }
    }
}
