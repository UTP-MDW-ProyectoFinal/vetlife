package mdw_proyectofinal.vetlife.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mdw_proyectofinal.vetlife.service.AuthService;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String ruta = request.getRequestURI();
        HttpSession sesion = request.getSession(false);

        boolean esRutaAdmin = ruta.startsWith("/admin");
        boolean esRutaUsuario = ruta.equals("/perfil") || ruta.equals("/mascota") || ruta.equals("/reservas");

        if (!esRutaAdmin && !esRutaUsuario) {
            return true;
        }

        if (sesion == null || sesion.getAttribute(AuthService.SESION_ROL) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Object rol = sesion.getAttribute(AuthService.SESION_ROL);
        if (esRutaAdmin && !"admin".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        return true;
    }
}
