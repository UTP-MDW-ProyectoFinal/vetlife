package mdw_proyectofinal.vetlife.service;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserAccountService {

    private static final int ITERACIONES = 210_000;
    private static final int LONGITUD_HASH = 256;
    private static final int LONGITUD_SAL = 16;

    private final SecureRandom secureRandom = new SecureRandom();
    private final ObjectMapper objectMapper;
    private final Path archivoCuentas;
    private List<CuentaUsuario> cuentas;

    public UserAccountService(
            ObjectMapper objectMapper,
            @Value("${vetlife.cuentas.archivo:data/cuentas.json}") String rutaArchivo) {
        this.objectMapper = objectMapper;
        this.archivoCuentas = Path.of(rutaArchivo).toAbsolutePath();
        this.cuentas = cargarCuentas();
    }

    public synchronized boolean registrar(
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String correo,
            String contrasena) {
        String correoNormalizado = normalizar(correo);
        String dniNormalizado = normalizar(dni);
        boolean cuentaExistente = cuentas.stream().anyMatch(cuenta ->
                normalizar(cuenta.correo()).equals(correoNormalizado)
                        || normalizar(cuenta.dni()).equals(dniNormalizado));
        if (cuentaExistente) {
            return false;
        }

        byte[] sal = new byte[LONGITUD_SAL];
        secureRandom.nextBytes(sal);
        byte[] hash = derivarHash(contrasena.toCharArray(), sal);
        CuentaUsuario cuenta = new CuentaUsuario(
                nombre.trim(), apellido.trim(), dni.trim(), telefono.trim(), correo.trim(), sal, hash);
        List<CuentaUsuario> nuevasCuentas = new ArrayList<>(cuentas);
        nuevasCuentas.add(cuenta);
        guardarCuentas(nuevasCuentas);
        cuentas = nuevasCuentas;
        return true;
    }

        public synchronized CuentaUsuario autenticar(String identificador, String contrasena) {
        String valor = normalizar(identificador);
        CuentaUsuario cuenta = cuentas.stream()
            .filter(candidata -> normalizar(candidata.correo()).equals(valor)
                || normalizar(candidata.dni()).equals(valor))
            .findFirst()
            .orElse(null);
        if (cuenta == null) {
            return null;
        }

        byte[] hashIngresado = derivarHash(contrasena.toCharArray(), cuenta.sal());
        return java.security.MessageDigest.isEqual(hashIngresado, cuenta.hash()) ? cuenta : null;
    }

    public synchronized boolean existeIdentificador(String identificador) {
        String valor = normalizar(identificador);
        return cuentas.stream().anyMatch(cuenta -> normalizar(cuenta.correo()).equals(valor)
                || normalizar(cuenta.dni()).equals(valor));
    }

    private List<CuentaUsuario> cargarCuentas() {
        if (Files.notExists(archivoCuentas)) {
            return new ArrayList<>();
        }

        try {
            CuentaUsuario[] guardadas = objectMapper.readValue(archivoCuentas.toFile(), CuentaUsuario[].class);
            return new ArrayList<>(Arrays.asList(guardadas));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("No se pudieron cargar las cuentas desde " + archivoCuentas, exception);
        }
    }

    private void guardarCuentas(List<CuentaUsuario> cuentas) {
        try {
            Files.createDirectories(archivoCuentas.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivoCuentas.toFile(), cuentas);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("No se pudieron guardar las cuentas en " + archivoCuentas, exception);
        }
    }

    private byte[] derivarHash(char[] contrasena, byte[] sal) {
        PBEKeySpec especificacion = new PBEKeySpec(contrasena, sal, ITERACIONES, LONGITUD_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacion)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("No se pudo verificar la contraseña.", exception);
        } finally {
            especificacion.clearPassword();
            Arrays.fill(contrasena, '\0');
        }
    }

    private String normalizar(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT);
    }

    public record CuentaUsuario(
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String correo,
            byte[] sal,
            byte[] hash) {
    }
}