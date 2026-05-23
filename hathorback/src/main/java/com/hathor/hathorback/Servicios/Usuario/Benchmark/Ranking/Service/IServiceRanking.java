package com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.Service;

import java.util.UUID;

import com.hathor.hathorback.Servicios.Usuario.Benchmark.Ranking.DTO.*;

public interface IServiceRanking {
    RankingResumenDTO     getResumenRanking(UUID idHato);
    RankingCompuestoDTO   getRankingCompuesto(UUID idHato, String region);
    RankingPorKpiDTO      getRankingPorKpi(UUID idHato, String codigoKpi, String region);
    EvolucionPosicionDTO  getEvolucionPosicion(UUID idHato, String codigoKpi, int meses);
}