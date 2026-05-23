package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;
import java.util.List;

@Data
public class SeedHatoCompletoDTO {

    // UUID del usuario en Supabase Auth
    private String idUsuarioAuth;

    // BUENO | MEDIO | BAJO | MEJORANDO
    private String perfil;

    private SeedHatoInfoDTO          hato;
    private SeedPerfilProductivoDTO  perfilProductivo;
    private List<SeedInventarioGanadoItemDTO> inventarioGanado;
    private List<SeedInventarioGeneralItemDTO> inventarioGeneral;
    private List<SeedFinanzasMesDTO> finanzas;
    private List<SeedProduccionDiaDTO> produccion;
    private List<SeedPracticaDTO>    practicas;
}