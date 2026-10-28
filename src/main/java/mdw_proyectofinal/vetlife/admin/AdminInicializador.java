package mdw_proyectofinal.vetlife.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class AdminInicializador implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.adam.username:adam}")
    private String adamUsername;

    @Value("${app.adam.password:adam1234}")
    private String adamPassword;

    public AdminInicializador(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Admin adam = new Admin();
            adam.setNombre("Adam");
            adam.setApellido("");
            adam.setUsuario(adamUsername.toLowerCase());
            adam.setCorreo("adam@vetlife.com");
            adam.setContrasena(passwordEncoder.encode(adamPassword));
            adam.setCargo("Administrador");
            adam = adminRepository.save(adam);
            if (adam.getId() != 1) {
                throw new IllegalStateException("Adam debe tener id 1");
            }
        }
    }
}
