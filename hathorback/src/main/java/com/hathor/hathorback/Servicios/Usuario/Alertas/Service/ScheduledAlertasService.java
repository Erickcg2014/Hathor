package com.hathor.hathorback.Servicios.Usuario.Alertas.Service;

import com.hathor.hathorback.Entities.Hato.Hato;
import com.hathor.hathorback.Servicios.Usuario.Alertas.Repository.IRepositoryAlertaHato;
import com.hathor.hathorback.Servicios.Usuario.Hato.Repository.IRepositoryHato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledAlertasService {

    @Autowired private IServiceAlertas      serviceAlertas;
    @Autowired private IRepositoryHato      repoHato;
    @Autowired private IRepositoryAlertaHato repoAlerta;

    // Evaluación diaria a las 6:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    public void evaluarAlertasDiarias() {
        System.out.println("Iniciando evaluación diaria de alertas: "
            + LocalDate.now());

        List<Hato> hatos = repoHato.findAll().stream()
            .filter(h -> h.getPorcentajeCompletitud() > 25)
            .toList();

        int exitosos = 0;
        int errores  = 0;

        for (Hato hato : hatos) {
            try {
                serviceAlertas.evaluarAlertas(hato.getIdHato());
                exitosos++;
                Thread.sleep(200);
            } catch (Exception e) {
                errores++;
                System.err.println("Error evaluando alertas hato "
                    + hato.getIdHato() + ": " + e.getMessage());
            }
        }

        System.out.println("Alertas evaluadas — Exitosos: "
            + exitosos + " | Errores: " + errores);
    }

    // Limpieza semanal de alertas expiradas — domingos a las 2:00 AM
    @Scheduled(cron = "0 0 2 * * SUN")
    public void limpiarAlertasExpiradas() {
        System.out.println("Limpiando alertas expiradas: "
            + LocalDate.now());

        int eliminadas = repoAlerta.expirarAlertas(LocalDate.now());

        System.out.println("Alertas expiradas: " + eliminadas);
    }
}