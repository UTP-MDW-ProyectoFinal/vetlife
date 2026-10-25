package mdw_proyectofinal.vetlife.controller.api.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mdw_proyectofinal.vetlife.dto.UsuarioRegistroDTO;
import mdw_proyectofinal.vetlife.model.api.v1.Usuario;
import mdw_proyectofinal.vetlife.service.api.v1.UsuarioService;
import mdw_proyectofinal.vetlife.utils.EncryptPassword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping(ApiVersion.BASE_PATH + "/usuario")
public class UsuarioController {
  private final UsuarioService usuarioService;

  public UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @PostMapping("registro")
  public ResponseEntity<?> postUsuario(@Valid @RequestBody UsuarioRegistroDTO dto, BindingResult result) {
    if (result.hasErrors()) {
      Map<String, List<String>> errors = new HashMap<>();

      result.getFieldErrors().forEach(
          error -> errors.computeIfAbsent(error.getField(), field -> new ArrayList<>()).add(error.getDefaultMessage()));
      return ResponseEntity.badRequest().body(errors);
    }

    Usuario newUser = new Usuario(dto.getNombre(), dto.getCorreo(), null);
    newUser.setPassword(EncryptPassword.encrypt(dto.getPassword()));
    usuarioService.save(newUser);
    return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado con exito");
  }

  @GetMapping("perfil")
  public ResponseEntity<?> getPerfil(@RequestHeader("email") String email, @RequestHeader("password") String password) {
    if (usuarioService.getUsuarioInfo(email) != null) {
      String dbpassword = usuarioService.getPassword(email);

      if (EncryptPassword.test(dbpassword, password)) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(usuarioService.getUsuarioInfo(email));
      }
    }

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario o contraseña incorrecta");
  }

  @PatchMapping("perfil")
  public ResponseEntity<?> patchUsuario(@RequestHeader("email") String email,
      @RequestHeader("password") String password, @RequestBody Map<String, Object> usuario) {
    String dbpassword = usuarioService.getPassword(email);
    Usuario dbUsuario = usuarioService.getUsuario(email);
    if (dbUsuario == null || !EncryptPassword.test(dbpassword, password)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario o contraseña incorrecta");
    }

    if (usuario.containsKey("nombre") && usuario.get("nombre") != null) {
      dbUsuario.setNombre((String) usuario.get("nombre"));
    }
    if (usuario.containsKey("correo") && usuario.get("correo") != null) {
      dbUsuario.setCorreo((String) usuario.get("correo"));
    }
    if (usuario.containsKey("password") && usuario.get("password") != null) {
      String nuevoPass = (String) usuario.get("password");
      dbUsuario.setPassword(EncryptPassword.encrypt(nuevoPass));
    }
    usuarioService.save(dbUsuario);
    return ResponseEntity.status(HttpStatus.OK).body("El usuario " + email + " ha sido actualizado con exito");

  }

}
