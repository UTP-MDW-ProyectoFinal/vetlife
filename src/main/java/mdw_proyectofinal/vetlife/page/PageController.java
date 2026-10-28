package mdw_proyectofinal.vetlife.page;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mdw_proyectofinal.vetlife.dto.ContactoResponse;
import mdw_proyectofinal.vetlife.dto.InfoApiResponse;
import mdw_proyectofinal.vetlife.dto.ServicioResponse;

@RestController
@RequestMapping("/api/v1")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping
    public ResponseEntity<InfoApiResponse> getInfo() {
        return ResponseEntity.ok(pageService.getInfo());
    }

    @GetMapping("/contacto")
    public ResponseEntity<ContactoResponse> getContacto() {
        return ResponseEntity.ok(pageService.getContacto());
    }

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioResponse>> getServicios() {
        return ResponseEntity.ok(pageService.getServicios());
    }
}
