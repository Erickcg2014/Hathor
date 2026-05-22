package com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hathor.hathorback.Entities.Usuario.Usuario;

@Repository
public interface IRepositoryUsuario extends JpaRepository<Usuario, UUID>{
    
    public Usuario findByIdAuth(UUID IdAuth);

    public Usuario findByCorreo(String email);

}
