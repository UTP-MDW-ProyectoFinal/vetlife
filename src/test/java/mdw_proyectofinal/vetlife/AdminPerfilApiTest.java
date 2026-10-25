package mdw_proyectofinal.vetlife;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.repository.AdminRepository;

/**
 * Pruebas automaticas de P6: GET y PATCH /api/v1/admin/perfil.
 * Usan una BD de pruebas separada (vetlife_test) para no tocar la de desarrollo.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/vetlife_test?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminPerfilApiTest {

    private static final long RUN = System.currentTimeMillis();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    /** Admin extra (id 2, no-Adam) para los casos con rol cruzado y correo duplicado. */
    @BeforeEach
    void asegurarAdminAdicional() {
        if (adminRepository.buscarPorUsuarioOCorreo("admin2@vetlife.com").isEmpty()) {
            adminRepository.guardar(new Admin(0, "Test", "AdminDos", "testadmin", "admin2@vetlife.com",
                    "Prueba12345", "Administrador"));
        }
    }

    @Test
    void getPerfil_conCredencialesValidas_responde200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario").value("admin"))
                .andExpect(jsonPath("$.correo").value("admin@vetlife.com"))
                .andExpect(jsonPath("$.cargo").value("Administrador"));
    }

    @Test
    void getPerfil_conPasswordInvalida_responde401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "incorrecta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPerfil_sinHeaders_responde401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/perfil"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchPerfil_vacio_responde400() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchPerfil_conCamposInvalidos_responde400ConDetalle() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{\"password\":\"corta\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    void patchPerfil_cambiaNombreEIgnoraUsername() throws Exception {
        // El DTO no tiene campo username: aunque el cuerpo envie "usuario",
        // el perfil se queda con el username original (regla P6).
        mockMvc.perform(patch("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Dra. Test " + RUN + "\",\"usuario\":\"renadaminado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("admin"))
                .andExpect(jsonPath("$.nombre").value("Dra. Test " + RUN));
    }

    @Test
    void patchPerfil_correoDuplicadoEnOtroAdmin_responde409() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{\"correo\":\"admin2@vetlife.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void patchPerfil_cambiaPasswordYFuncionaConElNuevoHash() throws Exception {
        // Cambiar la contrasena (queda hasheada con Argon2), verificar con la
        // nueva, y restaurarla para no afectar a las demas pruebas del contexto.
        mockMvc.perform(patch("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123")
                        .contentType("application/json")
                        .content("{\"password\":\"NuevaClave9\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "NuevaClave9"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "NuevaClave9")
                        .contentType("application/json")
                        .content("{\"password\":\"admin123\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/perfil")
                        .header("email", "admin@vetlife.com")
                        .header("password", "admin123"))
                .andExpect(status().isOk());
    }

    @Test
    void getPerfil_conAdminSecundario_responde200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/perfil")
                        .header("email", "admin2@vetlife.com")
                        .header("password", "Prueba12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }
}
