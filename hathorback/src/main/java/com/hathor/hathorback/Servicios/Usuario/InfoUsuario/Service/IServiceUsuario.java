package com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service;
import java.util.List;
import java.util.UUID;

import com.hathor.hathorback.Entities.Usuario.Usuario;

public interface IServiceUsuario {

    public Usuario createUsuario(Usuario usuario);
    public Usuario findUsuarioByIdAuth(UUID id);
    public List<Usuario> findAllUsuario();
    public Usuario findUsuarioByCorreo(String email);
    void eliminarCuentaCompleta(UUID idAuth);
}
