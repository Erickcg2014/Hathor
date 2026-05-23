CREATE OR REPLACE FUNCTION actualizar_bench_referencia_nacional()
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE benchmarkreferencia br
    SET 
        valor_promedio = sub.promedio_sistema,
        valor_top      = sub.top_sistema,
        anio           = EXTRACT(YEAR FROM CURRENT_DATE)::integer
    FROM (
        SELECT 
            kh.id_kpi,
            AVG(kh.valor)                                    AS promedio_sistema,
            PERCENTILE_CONT(0.9) 
                WITHIN GROUP (ORDER BY kh.valor)             AS top_sistema
        FROM kpi_hato kh
        WHERE kh.valor IS NOT NULL
        AND kh.fecha_calculo = (
            SELECT MAX(kh2.fecha_calculo)
            FROM kpi_hato kh2
            WHERE kh2.id_hato = kh.id_hato
            AND   kh2.id_kpi  = kh.id_kpi
        )
        GROUP BY kh.id_kpi
        HAVING COUNT(*) >= 10  
    ) sub
    WHERE br.id_kpi   = sub.id_kpi
    AND   br.tropico  IS NULL
    AND   br.escala   IS NULL
    AND   br.region   = 'NACIONAL';

    RAISE NOTICE 'Benchmark referencia nacional actualizado: %', CURRENT_DATE;
END;
$$;