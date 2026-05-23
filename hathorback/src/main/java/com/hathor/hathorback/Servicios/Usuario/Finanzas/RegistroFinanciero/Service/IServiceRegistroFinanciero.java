package com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.Service;

import com.hathor.hathorback.Entities.Finanzas.RegistroFinanciero;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.RegistroFinanciero.DTO.RegistroFinancieroDTO;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IServiceRegistroFinanciero {
    public Usuario findUsuarioByTokenEmail(String email);
    public List<RegistroFinanciero> createRegistroFinanciero (List<RegistroFinancieroDTO> registrosDTO, String email);
    List<Map<String, Object>> procesarCargaMasiva(MultipartFile archivo, UUID idHato, String email);
    public List<RegistroFinanciero> getRegistrosFinancierosByIdHato(UUID id_hato);
    public void eliminarRegistroFinanciero (UUID id_registro);

    List<RegistroFinanciero> getRegistrosRealesByHato(UUID idHato, String email);
    List<RegistroFinanciero> getRegistrosHistoricosByHato(UUID idHato, String email);
    
    // TODO: PRUEBA — eliminar antes de producción
    void limpiarRegistrosPorHato(UUID idHato, String email);

    List<RegistroFinanciero> getRegistrosPorPeriodo(UUID idHato, String email, String mesDesde, String mesHasta);
}
