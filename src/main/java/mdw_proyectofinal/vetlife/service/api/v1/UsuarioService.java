package mdw_proyectofinal.vetlife.service.api.v1;

import mdw_proyectofinal.vetlife.repository.api.v1.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mdw_proyectofinal.vetlife.model.api.v1.Usuario;;

@Service
public class UsuarioService {
  private final UsuarioRepository usuarioRepository;

  UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional
  public Usuario save(Usuario newUser) {
    return usuarioRepository.save(newUser);
  }

  public Map<String, Object> getUsuarioInfo(String email) {
    Usuario user = usuarioRepository.findByCorreo(email).orElse(null);

    
    Map<String, Object> response = new HashMap<>();
    
    if (user != null) {
      response.put("nombre", user.getNombre());
      response.put("correo", user.getCorreo());
      
      return response;
    }
    return null;
  }

  public Usuario getUsuario(String email) {
    return usuarioRepository.findByCorreo(email).orElse(null);
  }

  public String getPassword(String email) {
    Usuario user = usuarioRepository.findByCorreo(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return user.getPassword();
  }
}
