CREATE OR REPLACE FUNCTION calcular_bench_hatos_global()
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    WITH ultimos_kpis AS (
        SELECT DISTINCT ON (kh.id_hato, kh.id_kpi)
            kh.id_hato,
            kh.id_kpi,
            kh.valor
        FROM kpi_hato kh
        WHERE kh.valor IS NOT NULL
        ORDER BY kh.id_hato, kh.id_kpi, kh.fecha_calculo DESC
    ),
    stats_por_kpi AS (
        SELECT
            id_kpi,
            AVG(valor)  AS promedio,
            MAX(valor)  AS maximo,
            COUNT(*)    AS total_hatos
        FROM ultimos_kpis
        GROUP BY id_kpi
    ),
    percentiles AS (
        SELECT
            uk.id_hato,
            uk.id_kpi,
            uk.valor,
            s.promedio,
            s.maximo,
            CASE
                WHEN (s.maximo - s.promedio) = 0 THEN 0
                ELSE GREATEST(0, LEAST(100,
                    ((uk.valor - s.promedio) / (s.maximo - s.promedio)) * 100
                ))
            END AS percentil,
            CASE
                WHEN uk.valor < s.promedio * 0.7 THEN 'CRITICO'
                WHEN uk.valor < s.promedio       THEN 'ACEPTABLE'
                WHEN uk.valor <= s.maximo        THEN 'BUENO'
                ELSE                                  'OPTIMO'
            END AS interpretacion
        FROM ultimos_kpis uk
        JOIN stats_por_kpi s ON s.id_kpi = uk.id_kpi
    )
    INSERT INTO benchmark_hato (
        id_hato,
        id_kpi,
        id_benchreferencia,
        percentil,
        interpretacion,
        valor_hato,
        fecha_calculo,
        nivel_benchmark
    )
    SELECT
        p.id_hato,
        p.id_kpi,
        NULL,
        p.percentil,
        p.interpretacion,
        p.valor,
        CURRENT_DATE,
        'PLATAFORMA'
    FROM percentiles p
    ON CONFLICT (id_hato, id_kpi, nivel_benchmark)
    DO UPDATE SET
        percentil      = EXCLUDED.percentil,
        interpretacion = EXCLUDED.interpretacion,
        valor_hato     = EXCLUDED.valor_hato,
        fecha_calculo  = EXCLUDED.fecha_calculo;
END;
$$;