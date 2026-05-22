package com.hathor.hathorback.Servicios.Usuario.Hato.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Benchmark.Benchmarking.DTO.HatoAnonimizadoDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.CostosFijosDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.InfraestructuraBasicaDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.DTO.RegistroHatoDTO;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service.IServiceUsuario;

@Service
public class ServiceHato implements IServiceHato {

    @Autowired
    IRepositoryHato repositoryHato;

    @Autowired
    private IServiceUsuario usuarioService;

    private static final Map<String, String> GENTILICIOS = Map.ofEntries(
        Map.entry("Amazonas",                 "Amazónico"),
        Map.entry("Antioquia",                "Antioqueño"),
        Map.entry("Arauca",                   "Araucano"),
        Map.entry("Atlántico",                "Atlanticense"),
        Map.entry("Bolívar",                  "Bolivarense"),
        Map.entry("Boyacá",                   "Boyacense"),
        Map.entry("Caldas",                   "Caldense"),
        Map.entry("Caquetá",                  "Caqueteño"),
        Map.entry("Casanare",                 "Casanareño"),
        Map.entry("Cauca",                    "Caucano"),
        Map.entry("Cesar",                    "Cesarense"),
        Map.entry("Chocó",                    "Chocoano"),
        Map.entry("Córdoba",                  "Cordobés"),
        Map.entry("Cundinamarca",             "Cundinamarqués"),
        Map.entry("Guainía",                  "Guainiano"),
        Map.entry("Guaviare",                 "Guaviarense"),
        Map.entry("Huila",                    "Huilense"),
        Map.entry("La Guajira",               "Guajiro"),
        Map.entry("Magdalena",                "Magdalenense"),
        Map.entry("Meta",                     "Llanero"),
        Map.entry("Nariño",                   "Nariñense"),
        Map.entry("Norte de Santander",       "Nortesantandereano"),
        Map.entry("Putumayo",                 "Putumayense"),
        Map.entry("Quindío",                  "Quindiano"),
        Map.entry("Risaralda",                "Risaraldense"),
        Map.entry("San Andrés y Providencia", "Sanandresano"),
        Map.entry("Santander",                "Santandereano"),
        Map.entry("Sucre",                    "Sucreño"),
        Map.entry("Tolima",                   "Tolimense"),
        Map.entry("Valle del Cauca",          "Vallecaucano"),
        Map.entry("Vaupés",                   "Vaupesino"),
        Map.entry("Vichada",                  "Vichadeño")
    );

    @Override
    public Usuario findUsuarioByTokenEmail(String email) {
        return usuarioService.findUsuarioByCorreo(email);
    }

    @Override
    public Hato createHato(RegistroHatoDTO dto, String email) {
        Usuario usuario = findUsuarioByTokenEmail(email);

        Hato nuevoHato = Hato.builder()
                .nombreHato(dto.getNombreHato())
                .departamento(dto.getDepartamento())
                .ciudad(dto.getCiudad())
                .altitud(dto.getAltitud())
                .tropico(dto.getTropico())
                .areaHato(dto.getAreaHato())
                .areaPastoreo(dto.getAreaPastoreo())
                .cantCorrales(dto.getCantCorrales())
                .cantSalasOrdenio(dto.getCantSalasOrdenio())
                .capacidadAlmacenarLeche(dto.getCapacidadAlmacenarLeche())
                .cantEmpleadosPermanentes(dto.getCantEmpleadosPermanentes())
                .cantEmpleadosTemporales(dto.getCantEmpleadosTemporales())
                .tipoHato(dto.getTipoHato())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .usuario(usuario)
                .build();
        return repositoryHato.save(nuevoHato);
    }

    @Override
    public Hato actualizarCompletitud(UUID idHato, int porcentaje, String email) {
        Hato hato = findHatoById(idHato, email); 
        if (porcentaje < 0 || porcentaje > 100) {
            throw new RuntimeException("PORCENTAJE_INVALIDO");
        }
        hato.setPorcentajeCompletitud(porcentaje);
        return repositoryHato.save(hato);
    }

    @Override
    public List<Hato> findByUsuario_IdUsuario(UUID idUsuario) {
        return repositoryHato.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public Hato findHatoById(UUID idHato, String email) {
        Hato hato = repositoryHato.findById(idHato)
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));

        Usuario usuario = findUsuarioByTokenEmail(email);
        if (!hato.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("HATO_NO_AUTORIZADO");
        }
        return hato;
    }

    @Override
    public Hato findHatoById(UUID idHato) {
        return repositoryHato.findById(idHato)
            .orElseThrow(() -> new RuntimeException("HATO_NO_ENCONTRADO"));
    }
    
    @Override
    public Hato actualizarInfraestructuraBasica(UUID idHato, InfraestructuraBasicaDTO dto, String email) {
        Hato hato = findHatoById(idHato, email);
        hato.setCantCorrales(dto.getCantCorrales());
        hato.setCantSalasOrdenio(dto.getCantSalasOrdenio());
        hato.setCapacidadAlmacenarLeche(dto.getCapacidadAlmacenarLeche());
        hato.setCantEmpleadosPermanentes(dto.getCantEmpleadosPermanentes());
        hato.setCantEmpleadosTemporales(dto.getCantEmpleadosTemporales());
        return repositoryHato.save(hato);
    }
    
    @Override
    public Hato actualizarCostosFijos(UUID idHato, CostosFijosDTO dto, String email) {
        Hato hato = findHatoById(idHato, email);
        hato.setGastoMensualNomina(dto.getGastoMensualNomina());
        hato.setGastoMensualAlimentacion(dto.getGastoMensualAlimentacion());
        return repositoryHato.save(hato);
    }
    @Override
    public List<HatoAnonimizadoDTO> getMapaGeneral() {
        List<Hato> hatos = repositoryHato.findAllConCoordenadas();

        Map<String, Integer> contador = new HashMap<>();
        List<HatoAnonimizadoDTO> resultado = new ArrayList<>();

        for (Hato hato : hatos) {
            String depto     = hato.getDepartamento() != null
                ? hato.getDepartamento() : "Colombia";
            String gentilicio = GENTILICIOS.getOrDefault(depto, "Ganadero");
            int numero = contador.merge(gentilicio, 1, Integer::sum);
            String alias = "Hato " + gentilicio + " #" + numero;

            resultado.add(HatoAnonimizadoDTO.builder()
                .alias(alias)
                .latitudDifuminada(difuminar(hato.getLatitud()))
                .longitudDifuminada(difuminar(hato.getLongitud()))
                .tropico(hato.getTropico())
                .escala(hato.getEscala())
                .departamento(hato.getDepartamento())
                .valorKpiPrincipal(null)
                .interpretacion(null)
                .esMiHato(false)
                .build());
        }

        return resultado;
    }

    private Double difuminar(Double coordenada) {
        if (coordenada == null) return null;
        return Math.round(coordenada * 100.0) / 100.0;
    }
}
