package com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository.IRepositoryPerfilFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.PerfilFinanciero.Repository.IRepositoryPerfilFinancieroDetalle;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Repository.IRepositoryRegistroFinanciero;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.VentaLeche.Repository.IRepositoryVentaLeche;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Repository.IRepositoryUsuario;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGanado.Repository.IRepositoryInventarioGanado;
import com.hathor.hathorback.Servicios.Usuario.Inventarios.InventarioGeneral.Repository.IRepositoryInventarioGeneral;
import com.hathor.hathorback.Servicios.Usuario.Produccion.PerfilProductivo.Repository.IRepositoryPerfilProductivo;
import com.hathor.hathorback.Servicios.Usuario.Produccion.ProduccionLeche.Repository.IRepositoryProduccionLeche;

import jakarta.transaction.Transactional;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.DTO.ActualizarUsuarioDTO;

@Service
public class ServiceUsuario implements IServiceUsuario {

    @Autowired
    IRepositoryUsuario repositoryUsuario;

    @Autowired
    IRepositoryHato repositoryHato;

    @Autowired
    IRepositoryVentaLeche repositoryVentaLeche;

    @Autowired
    IRepositoryRegistroFinanciero repositoryRegistroFinanciero;

    @Autowired
    IRepositoryPerfilFinancieroDetalle repositoryPerfilFinancieroDetalle;

    @Autowired
    IRepositoryPerfilFinanciero repositoryPerfilFinanciero;

    @Autowired
    IRepositoryInventarioGanado repositoryInventarioGanado;

    @Autowired
    IRepositoryInventarioGeneral repositoryInventarioGeneral;

    @Autowired
    IRepositoryProduccionLeche repositoryProduccionLeche;

    @Autowired
    IRepositoryPerfilProductivo repositoryPerfilProductivo;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String supabaseServiceRoleKey;

    @Autowired
    private RestTemplate restTemplate;
    
    @Override
    public Usuario createUsuario(Usuario usuario) {
        if (repositoryUsuario.findByCorreo(usuario.getCorreo()) != null) {
            throw new RuntimeException("CORREO_DUPLICADO");
        }
        return repositoryUsuario.save(usuario);
    }

    @Override
    public Usuario findUsuarioByIdAuth(UUID id) {
        return repositoryUsuario.findByIdAuth(id);
    }

    @Override
    public List<Usuario> findAllUsuario() {
        return repositoryUsuario.findAll();
    }

    @Override
    public Usuario findUsuarioByCorreo(String email) {
        return repositoryUsuario.findByCorreo(email);
    }


    @Override
    @Transactional
    public void eliminarCuentaCompleta(UUID idAuth) {
        Usuario usuario = repositoryUsuario.findByIdAuth(idAuth);
        if (usuario == null) throw new RuntimeException("Usuario no encontrado");

        List<Hato> hatos = repositoryHato.findByUsuario_IdUsuario(usuario.getIdUsuario());

        for (Hato hato : hatos) {
            UUID idHato = hato.getIdHato();
            repositoryVentaLeche.deleteByHatoId(idHato);
            repositoryRegistroFinanciero.deleteByHatoId(idHato);
            repositoryPerfilFinancieroDetalle.deleteByHatoIdHato(idHato);
            repositoryPerfilFinanciero.deleteByHatoIdHato(idHato);
            repositoryInventarioGanado.deleteByHatoId(idHato);
            repositoryInventarioGeneral.deleteByHatoId(idHato);
            repositoryProduccionLeche.deleteByHatoId(idHato);
            repositoryPerfilProductivo.deleteByHatoId(idHato);
        }

        repositoryHato.deleteAll(hatos);
        repositoryUsuario.delete(usuario);

        eliminarDeSupabaseAuth(idAuth);
    }

    private void eliminarDeSupabaseAuth(UUID idAuth) {
        try {
            String url = supabaseUrl + "/auth/v1/admin/users/" + idAuth;

            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseServiceRoleKey);
            headers.set("Authorization", "Bearer " + supabaseServiceRoleKey);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario de Supabase Auth: " + e.getMessage());
        }
    }

    @Override
    public Usuario actualizarUsuario(String email, ActualizarUsuarioDTO dto) {
        Usuario usuario = repositoryUsuario.findByCorreo(email);
        if (usuario == null) throw new RuntimeException("USUARIO_NO_ENCONTRADO");
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCelular(dto.getCelular());
        return repositoryUsuario.save(usuario);
    }
}