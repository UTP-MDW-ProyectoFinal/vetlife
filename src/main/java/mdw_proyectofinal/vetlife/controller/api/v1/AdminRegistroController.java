package mdw_proyectofinal.vetlife.controller.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mdw_proyectofinal.vetlife.common.ConflictoException;
import mdw_proyectofinal.vetlife.dto.AdminResponse;
import mdw_proyectofinal.vetlife.dto.RegistroAdminRequest;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.repository.AdminRepository;
import mdw_proyectofinal.vetlife.security.SoloAdam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
public class AdminRegistroController {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminRegistroController(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registro")
    @SoloAdam
    public ResponseEntity<AdminResponse> registrar(@Valid @RequestBody RegistroAdminRequest request) {
        String username = request.getUsername().toLowerCase();
        String email = request.getEmail().toLowerCase();

        if (adminRepository.existsByUsername(username)) {
            throw new ConflictoException("El nombre de usuario ya existe");
        }
        if (adminRepository.existsByEmail(email)) {
            throw new ConflictoException("El email ya existe");
        }

        Admin admin = new Admin();
        admin.setUsuario(username);
        admin.setContrasena(passwordEncoder.encode(request.getPassword()));
        admin.setNombre(request.getNombre());
        admin.setApellido("");
        admin.setCorreo(email);
        admin.setCargo("Administrador");
        admin = adminRepository.save(admin);

        AdminResponse response = new AdminResponse(admin.getId(), admin.getUsuario(), admin.getNombre(), admin.getCorreo());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
