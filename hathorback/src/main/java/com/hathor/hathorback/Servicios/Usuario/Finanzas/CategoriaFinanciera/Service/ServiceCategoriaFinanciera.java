package com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hathor.hathorback.Entities.Finanzas.CategoriaFinanciera;
import com.hathor.hathorback.Entities.Usuario.Usuario;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CategoriaFinancieraDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CategoriasAgrupadasDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.DTO.CrearCategoriaPersonalizadaDTO;
import com.hathor.hathorback.Servicios.Usuario.Finanzas.CategoriaFinanciera.Repository.IRepositoryCategoriaFinanciera;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Repository.IRepositoryUsuario;
import com.hathor.hathorback.Servicios.Usuario.InfoUsuario.Service.IServiceUsuario;

@Service
public class ServiceCategoriaFinanciera implements IServiceCategoriaFinanciera{

    @Autowired
    IRepositoryCategoriaFinanciera repositoryCategoriaFinanciera;

    @Autowired
    IRepositoryUsuario repositoryUsuario;
    
    @Autowired
    IServiceUsuario usuarioService;

    @Autowired
    IServiceUsuario serviceUsuario;

    @Override
    public CategoriaFinanciera createCategoriaFinanciera(CategoriaFinanciera categoriaFinanciera) {
        return repositoryCategoriaFinanciera.save(categoriaFinanciera);
    }
    
    @Override
    public CategoriaFinancieraDTO crearCategoriaPersonalizada(
            CrearCategoriaPersonalizadaDTO dto, String email) {

        CategoriaFinanciera padre;

        if (dto.getIdCategoriaPadre() != null) {
            padre = repositoryCategoriaFinanciera
                .findById(dto.getIdCategoriaPadre())
                .orElseThrow(() -> new RuntimeException(
                    "Categoría padre no encontrada: " + dto.getIdCategoriaPadre()));
        } else {
            String nombrePadre = dto.getTipo().equals("INGRESO")
                ? "OTROS INGRESOS" : "OTROS GASTOS";
            padre = repositoryCategoriaFinanciera
                .findByNombreIgnoreCase(nombrePadre)
                .orElseThrow(() -> new RuntimeException(
                    "Categoría padre no encontrada: " + nombrePadre));
        }

        Usuario usuario = usuarioService.findUsuarioByCorreo(email);
        if (usuario == null) throw new RuntimeException("Usuario no encontrado");

        CategoriaFinanciera nueva = CategoriaFinanciera.builder()
            .nombre(dto.getNombre().toUpperCase().trim())
            .tipo(dto.getTipo())
            .esPredefinida(false)
            .categoriaPadre(padre)
            .usuario(usuario)
            .build();

        CategoriaFinanciera guardada = repositoryCategoriaFinanciera.save(nueva);

        return CategoriaFinancieraDTO.builder()
            .idCategoria(guardada.getIdCategoriaFinanciera())
            .nombre(guardada.getNombre())
            .descripcion(null)
            .tipo(guardada.getTipo())
            .build();
    }

    @Override
    public CategoriaFinanciera getCategoriaFinancieraById(UUID id) {
        return repositoryCategoriaFinanciera.findByIdCategoriaFinanciera(id);
    }

    @Override
    public CategoriaFinanciera getCategoriaByNombre(String nombre) {
        return repositoryCategoriaFinanciera.findByNombreIgnoreCase(nombre).orElse(null);
    }

    @Override
    public List<CategoriaFinanciera> getCategorias() {
        return repositoryCategoriaFinanciera.findAll();
    }

    @Override
    public CategoriasAgrupadasDTO getCategoriasAgrupadas() {
        return CategoriasAgrupadasDTO.builder()
            .ingresos(mapearADTO(repositoryCategoriaFinanciera.findPrimerNivelByTipo("INGRESO")))
            .gastos(mapearADTO(repositoryCategoriaFinanciera.findPrimerNivelByTipo("GASTO")))
            .inversiones(mapearADTO(repositoryCategoriaFinanciera.findPrimerNivelByTipo("INVERSION")))
            .build();
    }

    private List<CategoriaFinancieraDTO> mapearADTO(List<CategoriaFinanciera> categorias) {
        return categorias.stream()
            .map(c -> CategoriaFinancieraDTO.builder()
                .idCategoria(c.getIdCategoriaFinanciera())
                .nombre(c.getNombre())
                .descripcion(null)
                .tipo(c.getTipo())
                .build())
            .toList();
    }
    
    @Override
    public List<CategoriaFinanciera> getCategoriasAllAndMine(String emailUsuario) {
        Usuario usuario = serviceUsuario.findUsuarioByCorreo(emailUsuario);

        List<CategoriaFinanciera> catReturn = new ArrayList<>();
        List<CategoriaFinanciera> catAllSinUsuario = repositoryCategoriaFinanciera.findCategoriasSinUsuario();
        List<CategoriaFinanciera> catMine = repositoryCategoriaFinanciera.findByUsuario_IdUsuario(usuario.getIdUsuario());
        catReturn.addAll(catAllSinUsuario);
        catReturn.addAll(catMine);
        return catReturn;
    }
}