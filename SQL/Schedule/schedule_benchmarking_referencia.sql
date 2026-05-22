SELECT cron.schedule(
    'actualizar-bench-referencia-mensual',
    '0 4 1 * *',
    'SELECT actualizar_bench_referencia_nacional()'
);