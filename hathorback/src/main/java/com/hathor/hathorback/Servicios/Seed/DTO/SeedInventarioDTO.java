package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;
import java.util.List;

@Data
public class SeedInventarioDTO {
    private List<SeedInventarioGanadoItemDTO> inventarioGanado;
    private List<SeedInventarioGeneralItemDTO> inventarioGeneral;
}