package mdw_proyectofinal.vetlife.controller.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersion.BASE_PATH)
public class ApiController {
  @GetMapping("/")
  public ResponseEntity<?> saludoGeneral() {
    return ResponseEntity.status(HttpStatus.OK).body("Some");
  }
}
