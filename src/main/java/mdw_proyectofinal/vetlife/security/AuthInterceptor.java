package mdw_proyectofinal.vetlife.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mdw_proyectofinal.vetlife.common.AccesoDenegadoException;
import mdw_proyectofinal.vetlife.common.NoAutenticadoException;
import mdw_proyectofinal.vetlife.model.Admin;
import mdw_proyectofinal.vetlife.model.api.v1.Usuario;
import mdw_proyectofinal.vetlife.repository.AdminRepository;
import mdw_proyectofinal.vetlife.repository.api.v1.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AdminRepository adminRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthInterceptor(AdminRepository adminRepository, UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new NoAutenticadoException("Credenciales ausentes o incorrectas");
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        byte[] credDecoded;
        try {
            credDecoded = Base64.getDecoder().decode(base64Credentials);
        } catch (IllegalArgumentException e) {
            throw new NoAutenticadoException("Credenciales ausentes o incorrectas");
        }

        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        int colonIndex = credentials.indexOf(':');
        if (colonIndex == -1) {
            throw new NoAutenticadoException("Credenciales ausentes o incorrectas");
        }

        String username = credentials.substring(0, colonIndex).toLowerCase();
        String password = credentials.substring(colonIndex + 1);

        String uri = request.getRequestURI();

        if (uri.startsWith("/api/v1/admin")) {
            Admin admin = adminRepository.findByUsuario(username);
            if (admin != null && passwordEncoder.matches(password, admin.getContrasena())) {
                request.setAttribute(AuthContext.ADMIN_ID, (long) admin.getId());
                request.setAttribute(AuthContext.ES_ADAM, admin.getId() == AuthContext.ADAM_ID.intValue());
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                if (handlerMethod.hasMethodAnnotation(SoloAdam.class) || handlerMethod.getBeanType().isAnnotationPresent(SoloAdam.class)) {
                    if (admin.getId() != AuthContext.ADAM_ID.intValue()) {
                        throw new AccesoDenegadoException("Rol insuficiente");
                    }
                }
                return true;
            }
            // Try in other table
            Usuario usuario = usuarioRepository.findByEmail(username);
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(username);
            }
            if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
                throw new AccesoDenegadoException("Rol insuficiente");
            }
            throw new NoAutenticadoException("Credenciales ausentes o incorrectas");
        }

        if (uri.startsWith("/api/v1/usuario")) {
            Usuario usuario = usuarioRepository.findByEmail(username);
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(username);
            }
            if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
                request.setAttribute(AuthContext.USUARIO_ID, usuario.getId());
                return true;
            }
            Admin admin = adminRepository.findByUsuario(username);
            if (admin != null && passwordEncoder.matches(password, admin.getContrasena())) {
                throw new AccesoDenegadoException("Rol insuficiente");
            }
            throw new NoAutenticadoException("Credenciales ausentes o incorrectas");
        }

        return true;
    }
}
