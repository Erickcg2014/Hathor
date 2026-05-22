SELECT cron.schedule(
    'calcular-ranking-diario',
    '0 2 * * *',
    'SELECT calcular_ranking_global()'
);