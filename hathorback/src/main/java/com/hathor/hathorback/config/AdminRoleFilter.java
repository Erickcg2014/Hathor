package com.hathor.hathorback.config;

import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Repository.IRepositoryUsuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import com.hathor.hathorback.Entities.Usuario.enums.Rol;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminRoleFilter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Autowired
    private IRepositoryUsuario repositoryUsuario;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getClaim("email");

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (email != null) {
            try {
                Usuario usuario = repositoryUsuario.findByCorreo(email);
                if (usuario != null) {
                    System.out.println("Rol del usuario: " + usuario.getRol());
                }
                if (usuario != null && Rol.ADMIN == usuario.getRol()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    System.out.println(" ROLE_ADMIN agregado");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Authorities finales: " + authorities);
        return new JwtAuthenticationToken(jwt, authorities);
    }
}