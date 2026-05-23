package com.hathor.hathorback.Servicios.Usuario.InfoUsuario;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Entities.Usuario.enums.Rol;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.DTO.ActualizarUsuarioDTO;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.DTO.RegistroUsuarioDTO;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service.IServiceUsuario;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/Usuario")
public class ControllerUsuario {

    @Autowired
    IServiceUsuario serviceUsuario;

    // obtener informacion del token
    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioToken (@AuthenticationPrincipal Jwt jwt, HttpServletRequest request){
        String email = jwt.getClaim("email");

        return new ResponseEntity<Usuario>(serviceUsuario.findUsuarioByCorreo(email), HttpStatus.OK);
    } 

    // Crear o registrar usuario
    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody RegistroUsuarioDTO dto) {
        try {
            Usuario usuario = Usuario.builder()
                .idAuth(dto.getIdAuth())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .correo(dto.getCorreo())
                .celular(dto.getCelular())
                .rol(Rol.USER)
                .build();

            return new ResponseEntity<>(serviceUsuario.createUsuario(usuario), HttpStatus.CREATED);

        } catch (RuntimeException e) {
            if ("CORREO_DUPLICADO".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "CORREO_DUPLICADO"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> actualizarUsuario(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody ActualizarUsuarioDTO dto
    ) {
        try {
            String email = jwt.getClaim("email");
            return ResponseEntity.ok(serviceUsuario.actualizarUsuario(email, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> eliminarMiCuenta(
        @AuthenticationPrincipal Jwt jwt
    ) {
        UUID idAuth = UUID.fromString(jwt.getSubject());
        serviceUsuario.eliminarCuentaCompleta(idAuth);
        return ResponseEntity.noContent().build();
    }
}
