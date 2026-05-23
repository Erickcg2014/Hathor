SELECT cron.schedule('calcular-bench-hatos-diario', '0 3 * * *',
    'SELECT calcular_bench_hatos_global()');