CREATE OR REPLACE FUNCTION calcular_ranking_global()
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    WITH scores AS (
        SELECT 
            bh.id_hato,
            AVG(bh.percentil) as score_compuesto,
            h.departamento
        FROM benchmark_hato bh
        JOIN hato h ON h.id_hato = bh.id_hato
        WHERE bh.nivel_benchmark = 'NACIONAL'
        AND bh.percentil IS NOT NULL
        AND bh.percentil BETWEEN 0 AND 100
        AND bh.fecha_calculo = (
            SELECT MAX(b2.fecha_calculo)
            FROM benchmark_hato b2
            WHERE b2.id_hato = bh.id_hato
            AND b2.nivel_benchmark = 'NACIONAL'
        )
        GROUP BY bh.id_hato, h.departamento
    ),
    ranking_nacional AS (
        SELECT 
            id_hato,
            departamento,
            score_compuesto,
            RANK() OVER (ORDER BY score_compuesto DESC) as posicion_nacional,
            COUNT(*) OVER () as total_nacional
        FROM scores
    ),
    ranking_regional AS (
        SELECT
            id_hato,
            RANK() OVER (
                PARTITION BY departamento 
                ORDER BY score_compuesto DESC
            ) as posicion_regional,
            COUNT(*) OVER (
                PARTITION BY departamento
            ) as total_regional
        FROM scores
    )
    INSERT INTO ranking_hato (
        id_hato, score_compuesto,
        posicion_nacional, total_nacional,
        posicion_regional, total_regional,
        fecha_calculo
    )
    SELECT 
        rn.id_hato,
        rn.score_compuesto,
        rn.posicion_nacional,
        rn.total_nacional,
        rr.posicion_regional,
        rr.total_regional,
        CURRENT_DATE
    FROM ranking_nacional rn
    JOIN ranking_regional rr ON rr.id_hato = rn.id_hato
    ON CONFLICT (id_hato)
    DO UPDATE SET
        score_compuesto   = EXCLUDED.score_compuesto,
        posicion_nacional = EXCLUDED.posicion_nacional,
        total_nacional    = EXCLUDED.total_nacional,
        posicion_regional = EXCLUDED.posicion_regional,
        total_regional    = EXCLUDED.total_regional,
        fecha_calculo     = EXCLUDED.fecha_calculo;
END;
$$;