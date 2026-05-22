package com.hathor.hathorback.Servicios.Seed.DTO;

import lombok.Data;
import java.util.List;

@Data
public class SeedFinanzasMesDTO {
    // Formato "yyyy-MM" — ej: "2025-10"
    private String mes;
    private List<SeedFinanzasItemDTO> ingresos;
    private List<SeedFinanzasItemDTO> egresos;
}