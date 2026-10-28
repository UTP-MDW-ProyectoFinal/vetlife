package mdw_proyectofinal.vetlife;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import mdw_proyectofinal.vetlife.model.api.v1.Cita;
import mdw_proyectofinal.vetlife.model.api.v1.Doctor;
import mdw_proyectofinal.vetlife.model.api.v1.Especialidad;
import mdw_proyectofinal.vetlife.model.api.v1.Especie;
import mdw_proyectofinal.vetlife.model.api.v1.EstadoCita;
import mdw_proyectofinal.vetlife.model.api.v1.Mascota;
import mdw_proyectofinal.vetlife.model.api.v1.Sexo;
import mdw_proyectofinal.vetlife.model.api.v1.Usuario;

/**
 * Pruebas automaticas de P5: GET /api/v1/admin/doctores/{idDoctor} y sus tres
 * sub-rutas (citas, citas/{idCita}, pacientes).
 *
 * Usan la BD de pruebas vetlife_test (igual que las pruebas de P6). Cada prueba
 * crea sus propios datos dentro de una transaccion que se revierte al terminar
 * (@Transactional), asi que no ensucian la BD ni dependen de las semillas.
 *
 * Datos de la fixture (doctor A):
 *   Luna  - c1 PENDIENTE  2030-03-10 10:00
 *   Luna  - c2 COMPLETADA 2029-12-01 09:00
 *   Luna  - c3 CANCELADA  2030-03-10 15:00
 *   Michi - c4 CONFIRMADA 2030-03-11 09:00
 * Doctor B tiene una sola cita (c5, de Michi).
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/vetlife_test?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
})
@AutoConfigureMockMvc
@Transactional
class AdminDoctorConsultaApiTest {

    private static final long RUN = System.currentTimeMillis();
    private static final String BASE = "/api/v1/admin/doctores";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    private Doctor doctorA;
    private Doctor doctorB;
    private Cita c1;
    private Cita c2;
    private Cita c3;
    private Cita c4;
    private Cita c5;

    @BeforeEach
    void crearDatos() {
        Especialidad especialidad = especialidad("Medicina general");
        Especie perro = especie("PERRO");
        Especie gato = especie("GATO");
        EstadoCita pendiente = estado(EstadoCita.PENDIENTE);
        EstadoCita confirmada = estado(EstadoCita.CONFIRMADA);
        EstadoCita cancelada = estado(EstadoCita.CANCELADA);
        EstadoCita completada = estado(EstadoCita.COMPLETADA);

        Usuario cliente = new Usuario("cli_p5_" + RUN, "cliente.p5." + RUN + "@test.com", "hash-no-se-expone");
        em.persist(cliente);

        Mascota luna = mascota(cliente, perro, "Luna", "Border Collie", Sexo.HEMBRA);
        Mascota michi = mascota(cliente, gato, "Michi", "Siames", Sexo.MACHO);

        doctorA = doctor(especialidad, "Prueba", "Alfa", "a");
        doctorB = doctor(especialidad, "Prueba", "Beta", "b");

        c1 = cita(luna, doctorA, pendiente, LocalDateTime.of(2030, 3, 10, 10, 0), "Vacuna anual");
        c2 = cita(luna, doctorA, completada, LocalDateTime.of(2029, 12, 1, 9, 0), "Control general");
        c3 = cita(luna, doctorA, cancelada, LocalDateTime.of(2030, 3, 10, 15, 0), "Revision de piel");
        c4 = cita(michi, doctorA, confirmada, LocalDateTime.of(2030, 3, 11, 9, 0), "Desparasitacion");
        c5 = cita(michi, doctorB, pendiente, LocalDateTime.of(2030, 4, 1, 9, 0), "Chequeo");
        em.flush();
    }

    // ---------------------------------------------------------------- detalle

    @Test
    void detalle_devuelveDoctorConConteos() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorA.getId()))
                .andExpect(jsonPath("$.nombre").value("Prueba"))
                .andExpect(jsonPath("$.apellido").value("Alfa"))
                .andExpect(jsonPath("$.especialidad").value("Medicina general"))
                .andExpect(jsonPath("$.numeroLicencia").value("P5-A-" + RUN))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.totalCitas").value(4))
                .andExpect(jsonPath("$.citasPendientes").value(1));
    }

    @Test
    void detalle_doctorSinCitas_devuelveConteosEnCero() throws Exception {
        Doctor sinCitas = doctor(especialidad("Medicina general"), "Prueba", "Gamma", "c");
        em.flush();

        mockMvc.perform(admin(get(BASE + "/" + sinCitas.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCitas").value(0))
                .andExpect(jsonPath("$.citasPendientes").value(0));
    }

    @Test
    void detalle_doctorInexistente_responde404() throws Exception {
        mockMvc.perform(admin(get(BASE + "/999999999")))
                .andExpect(status().isNotFound());
    }

    @Test
    void detalle_idNoPositivo_responde400ConDetalle() throws Exception {
        mockMvc.perform(admin(get(BASE + "/0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles[0].campo").value("idDoctor"));
    }

    @Test
    void detalle_idNoNumerico_responde400() throws Exception {
        mockMvc.perform(admin(get(BASE + "/abc")))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ citas

    @Test
    void citas_devuelveTodasOrdenadasPorFechaAscendente() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(c2.getId()))
                .andExpect(jsonPath("$[1].id").value(c1.getId()))
                .andExpect(jsonPath("$[2].id").value(c3.getId()))
                .andExpect(jsonPath("$[3].id").value(c4.getId()));
    }

    @Test
    void citas_incluyeMascotaYClienteSinDatosSensibles() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].mascota.nombre").value("Luna"))
                .andExpect(jsonPath("$[1].mascota.especie").value("PERRO"))
                .andExpect(jsonPath("$[1].cliente.correo").value("cliente.p5." + RUN + "@test.com"))
                .andExpect(jsonPath("$[1].estado").value("PENDIENTE"))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("hash-no-se-expone"))));
    }

    @Test
    void citas_filtroEstado_ignoraMayusculasYFiltra() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas?estado=cancelada")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(c3.getId()));
    }

    @Test
    void citas_estadoInexistente_responde400() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas?estado=NOEXISTE")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void citas_filtroFecha_devuelveSoloEseDia() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas?fecha=2030-03-10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(c1.getId()))
                .andExpect(jsonPath("$[1].id").value(c3.getId()));
    }

    @Test
    void citas_filtrosCombinados() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas?fecha=2030-03-10&estado=PENDIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(c1.getId()));
    }

    @Test
    void citas_fechaConFormatoInvalido_responde400() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas?fecha=10-03-2030")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void citas_sinResultados_devuelveListaVacia() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas?fecha=2031-01-01")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void citas_doctorInexistente_responde404() throws Exception {
        mockMvc.perform(admin(get(BASE + "/999999999/citas")))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------- una cita

    @Test
    void cita_deEseDoctor_responde200() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas/" + c1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c1.getId()))
                .andExpect(jsonPath("$.motivo").value("Vacuna anual"))
                .andExpect(jsonPath("$.fechaHora").value(startsWith("2030-03-10T10:00")))
                .andExpect(jsonPath("$.mascota.nombre").value("Luna"));
    }

    @Test
    void cita_deOtroDoctor_responde404() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas/" + c5.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    void cita_losTresCasosDe404_usanElMismoMensaje() throws Exception {
        String otroDoctor = body(admin(get(BASE + "/" + doctorA.getId() + "/citas/" + c5.getId())));
        String citaInexistente = body(admin(get(BASE + "/" + doctorA.getId() + "/citas/999999999")));
        String doctorInexistente = body(admin(get(BASE + "/999999999/citas/" + c1.getId())));

        org.assertj.core.api.Assertions.assertThat(citaInexistente).isEqualTo(otroDoctor);
        org.assertj.core.api.Assertions.assertThat(doctorInexistente).isEqualTo(otroDoctor);
    }

    @Test
    void cita_idNoPositivo_responde400() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/citas/0")))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------- pacientes

    @Test
    void pacientes_noSeRepitenYLasCanceladasNoCuentan() throws Exception {
        // Luna tiene 3 citas con el doctor A, pero una esta cancelada: cuenta 2 y aparece una sola vez.
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/pacientes")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Luna"))
                .andExpect(jsonPath("$[0].especie").value("PERRO"))
                .andExpect(jsonPath("$[0].raza").value("Border Collie"))
                .andExpect(jsonPath("$[0].totalCitas").value(2))
                .andExpect(jsonPath("$[0].ultimaVisita").value(startsWith("2029-12-01T09:00")))
                .andExpect(jsonPath("$[0].duenio.correo").value("cliente.p5." + RUN + "@test.com"))
                .andExpect(jsonPath("$[1].nombre").value("Michi"))
                .andExpect(jsonPath("$[1].totalCitas").value(1))
                .andExpect(jsonPath("$[1].ultimaVisita").value(nullValue()));
    }

    @Test
    void pacientes_soloCuentaLasCitasDeEseDoctor() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorB.getId() + "/pacientes")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Michi"))
                .andExpect(jsonPath("$[0].totalCitas").value(1));
    }

    @Test
    void pacientes_doctorSinCitas_devuelveListaVacia() throws Exception {
        Doctor sinCitas = doctor(especialidad("Medicina general"), "Prueba", "Delta", "d");
        em.flush();

        mockMvc.perform(admin(get(BASE + "/" + sinCitas.getId() + "/pacientes")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void pacientes_doctorInexistente_responde404() throws Exception {
        mockMvc.perform(admin(get(BASE + "/999999999/pacientes")))
                .andExpect(status().isNotFound());
    }

    @Test
    void pacientes_noExponeContrasenas() throws Exception {
        mockMvc.perform(admin(get(BASE + "/" + doctorA.getId() + "/pacientes")))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("hash-no-se-expone"))));
    }

    // ------------------------------------------------------------- seguridad

    @Test
    void sinCredenciales_lasCuatroRutasResponden401() throws Exception {
        long id = doctorA.getId();
        for (String ruta : List.of(
                BASE + "/" + id,
                BASE + "/" + id + "/citas",
                BASE + "/" + id + "/citas/" + c1.getId(),
                BASE + "/" + id + "/pacientes")) {
            mockMvc.perform(get(ruta)).andExpect(status().isUnauthorized());
        }
    }

    @Test
    void contrasenaIncorrecta_responde401() throws Exception {
        mockMvc.perform(get(BASE + "/" + doctorA.getId())
                        .header("email", "admin@vetlife.com")
                        .header("password", "incorrecta"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------------------------------------------- helpers

    /** Agrega las credenciales del admin semilla (id 1). Cualquier admin puede usar estas rutas. */
    private static MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
        return request.header("email", "admin@vetlife.com").header("password", "admin123");
    }

    private String body(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request).andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
    }

    private Especialidad especialidad(String nombre) {
        List<Especialidad> existentes = em
                .createQuery("SELECT e FROM Especialidad e WHERE e.nombre = :n", Especialidad.class)
                .setParameter("n", nombre).getResultList();
        if (!existentes.isEmpty()) {
            return existentes.get(0);
        }
        Especialidad nueva = new Especialidad(nombre);
        em.persist(nueva);
        return nueva;
    }

    private Especie especie(String nombre) {
        List<Especie> existentes = em
                .createQuery("SELECT e FROM Especie e WHERE e.nombre = :n", Especie.class)
                .setParameter("n", nombre).getResultList();
        if (!existentes.isEmpty()) {
            return existentes.get(0);
        }
        Especie nueva = new Especie();
        nueva.setNombre(nombre);
        em.persist(nueva);
        return nueva;
    }

    private EstadoCita estado(String nombre) {
        List<EstadoCita> existentes = em
                .createQuery("SELECT e FROM EstadoCita e WHERE e.nombre = :n", EstadoCita.class)
                .setParameter("n", nombre).getResultList();
        if (!existentes.isEmpty()) {
            return existentes.get(0);
        }
        EstadoCita nuevo = new EstadoCita(nombre);
        em.persist(nuevo);
        return nuevo;
    }

    private Mascota mascota(Usuario dueno, Especie especie, String nombre, String raza, Sexo sexo) {
        Mascota mascota = new Mascota();
        mascota.setUsuario(dueno);
        mascota.setEspecie(especie);
        mascota.setNombre(nombre);
        mascota.setRaza(raza);
        mascota.setSexo(sexo);
        mascota.setFechaNacimiento(LocalDate.of(2022, 1, 1));
        mascota.setPesoKg(new BigDecimal("10.50"));
        em.persist(mascota);
        return mascota;
    }

    private Doctor doctor(Especialidad especialidad, String nombre, String apellido, String sufijo) {
        Doctor doctor = new Doctor();
        doctor.setEspecialidad(especialidad);
        doctor.setNombre(nombre);
        doctor.setApellido(apellido);
        doctor.setEmail("p5." + sufijo + "." + RUN + "@vetlife.test");
        doctor.setTelefono("987654321");
        doctor.setNumeroLicencia("P5-" + sufijo.toUpperCase() + "-" + RUN);
        doctor.setActivo(true);
        em.persist(doctor);
        return doctor;
    }

    private Cita cita(Mascota mascota, Doctor doctor, EstadoCita estado, LocalDateTime fechaHora, String motivo) {
        Cita cita = new Cita();
        cita.setMascota(mascota);
        cita.setDoctor(doctor);
        cita.setEstado(estado);
        cita.setFechaHora(fechaHora);
        cita.setMotivo(motivo);
        em.persist(cita);
        return cita;
    }
}