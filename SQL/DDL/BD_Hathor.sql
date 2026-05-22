-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.alerta_hato (
  id_alerta bigint NOT NULL DEFAULT nextval('alerta_hato_id_alerta_seq'::regclass),
  tipo character varying NOT NULL,
  severidad character varying NOT NULL,
  mensaje text NOT NULL,
  titulo character varying NOT NULL,
  leida boolean NOT NULL DEFAULT false,
  fecha_creacion timestamp without time zone NOT NULL DEFAULT now(),
  fecha_expiracion date,
  codigo_kpi character varying,
  valor_referencia double precision,
  estado character varying NOT NULL DEFAULT 'ACTIVA'::character varying,
  id_hato uuid NOT NULL,
  CONSTRAINT alerta_hato_pkey PRIMARY KEY (id_alerta),
  CONSTRAINT alerta_hato_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.benchmark_hato (
  id_benchmark_resultado integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_hato uuid NOT NULL,
  id_kpi integer NOT NULL,
  percentil real,
  interpretacion text,
  valor_hato real,
  fecha_calculo date NOT NULL DEFAULT CURRENT_DATE,
  id_benchreferencia integer,
  nivel_benchmark character varying NOT NULL DEFAULT 'NACIONAL'::character varying,
  CONSTRAINT benchmark_hato_pkey PRIMARY KEY (id_benchmark_resultado),
  CONSTRAINT fk_benchhato_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT fk_benchhato_kpi FOREIGN KEY (id_kpi) REFERENCES public.kpi(id_kpi),
  CONSTRAINT benchmark_hato_id_benchReferencia_fkey FOREIGN KEY (id_benchreferencia) REFERENCES public.benchmarkreferencia(id_benchmark)
);
CREATE TABLE public.benchmarkreferencia (
  id_benchmark integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_kpi integer NOT NULL,
  region character varying,
  valor_promedio real,
  valor_top real,
  anio integer,
  tropico character varying,
  sistema_ordenio character varying,
  escala character varying DEFAULT NULL::character varying,
  CONSTRAINT benchmarkreferencia_pkey PRIMARY KEY (id_benchmark),
  CONSTRAINT fk_benchref_kpi FOREIGN KEY (id_kpi) REFERENCES public.kpi(id_kpi)
);
CREATE TABLE public.categoria_financiera (
  id_categoria uuid NOT NULL DEFAULT gen_random_uuid(),
  id_usuario uuid,
  nombre character varying NOT NULL,
  tipo character varying NOT NULL,
  id_categoria_padre uuid,
  es_predefinida boolean NOT NULL,
  unidad_produccion character varying,
  descripcion text,
  activa boolean DEFAULT true,
  orden smallint DEFAULT 0,
  CONSTRAINT categoria_financiera_pkey PRIMARY KEY (id_categoria),
  CONSTRAINT categoriafinanciera_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario),
  CONSTRAINT categoriafinanciera_padre_fkey FOREIGN KEY (id_categoria_padre) REFERENCES public.categoria_financiera(id_categoria)
);
CREATE TABLE public.categoria_ganado (
  id_categoria integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  nombre_categoria character varying NOT NULL,
  descripcion text,
  CONSTRAINT categoria_ganado_pkey PRIMARY KEY (id_categoria)
);
CREATE TABLE public.categoria_inventario (
  id_categoria integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  nombre character varying NOT NULL,
  descripcion text,
  id_usuario uuid,
  tipo character varying NOT NULL,
  id_categoria_padre integer,
  es_predefinida boolean NOT NULL,
  unidad_medida character varying,
  orden smallint DEFAULT 0,
  activa boolean DEFAULT true,
  CONSTRAINT categoria_inventario_pkey PRIMARY KEY (id_categoria),
  CONSTRAINT categoria_inventario_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario),
  CONSTRAINT categoria_inventario_padre_fkey FOREIGN KEY (id_categoria_padre) REFERENCES public.categoria_inventario(id_categoria)
);
CREATE TABLE public.hato (
  id_hato uuid NOT NULL DEFAULT gen_random_uuid(),
  nombre text NOT NULL,
  departamento text NOT NULL,
  ciudad text NOT NULL,
  direccion text,
  altitud real NOT NULL,
  tropico text NOT NULL,
  area_hato double precision NOT NULL,
  area_pastoreo double precision NOT NULL,
  cant_corrales integer NOT NULL,
  cant_salasordenio integer NOT NULL,
  capacidad_almacenarleche double precision NOT NULL,
  cant_empleadospermanentes integer NOT NULL,
  cant_empleadostemporales integer NOT NULL,
  tipo_hato text NOT NULL DEFAULT ''::text,
  id_usuario uuid NOT NULL,
  porcentaje_completitud integer NOT NULL DEFAULT 25,
  fecha_registro date NOT NULL DEFAULT CURRENT_DATE,
  gasto_mensual_nomina double precision DEFAULT 0,
  gasto_mensual_alimentacion double precision DEFAULT 0,
  escala character varying DEFAULT 'PEQUEÑA'::character varying,
  latitud double precision,
  longitud double precision,
  CONSTRAINT hato_pkey PRIMARY KEY (id_hato),
  CONSTRAINT hato_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario)
);
CREATE TABLE public.hato_practica_paso (
  id_paso bigint NOT NULL DEFAULT nextval('hato_practica_paso_id_paso_seq'::regclass),
  indice_paso integer NOT NULL,
  completado boolean NOT NULL DEFAULT false,
  fecha_completado date,
  id_hato_practica uuid NOT NULL,
  CONSTRAINT hato_practica_paso_pkey PRIMARY KEY (id_paso),
  CONSTRAINT hato_practica_paso_id_hato_practica_fkey FOREIGN KEY (id_hato_practica) REFERENCES public.hatopractica(id_hato_practica)
);
CREATE TABLE public.hatopractica (
  id_hato_practica uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL,
  id_practica integer NOT NULL,
  estado character varying,
  fecha_inicio date,
  fecha_fin date,
  porcentaje_avance real,
  observaciones text,
  id_recomendacion integer,
  CONSTRAINT hatopractica_pkey PRIMARY KEY (id_hato_practica),
  CONSTRAINT fk_hatoprac_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT fk_hatoprac_practica FOREIGN KEY (id_practica) REFERENCES public.practica(id_practica),
  CONSTRAINT hatopractica_id_recomendacion_fkey FOREIGN KEY (id_recomendacion) REFERENCES public.recomendacion_hato(id_recomendacion_hato)
);
CREATE TABLE public.inventario_ganado (
  id_inventario uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL,
  id_raza integer NOT NULL,
  id_categoria integer NOT NULL,
  cantidad integer,
  edad_promedio_meses integer,
  fecha_registro date NOT NULL DEFAULT CURRENT_DATE,
  valor_unitario real,
  valor_total real,
  CONSTRAINT inventario_ganado_pkey PRIMARY KEY (id_inventario),
  CONSTRAINT fk_invgan_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT fk_invgan_raza FOREIGN KEY (id_raza) REFERENCES public.raza(id_raza),
  CONSTRAINT fk_invgan_categoria FOREIGN KEY (id_categoria) REFERENCES public.categoria_ganado(id_categoria)
);
CREATE TABLE public.inventario_general (
  id_inventario_general uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL,
  id_categoria_inventario integer NOT NULL,
  nombre_item character varying NOT NULL,
  cantidad integer,
  valor_unitario real,
  valor_total real,
  fecha_registro date NOT NULL DEFAULT CURRENT_DATE,
  descripcion text,
  CONSTRAINT inventario_general_pkey PRIMARY KEY (id_inventario_general),
  CONSTRAINT fk_invgen_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT fk_invgen_categoria FOREIGN KEY (id_categoria_inventario) REFERENCES public.categoria_inventario(id_categoria)
);
CREATE TABLE public.inversion_planeada (
  id_inversion bigint NOT NULL DEFAULT nextval('inversion_planeada_id_inversion_seq'::regclass),
  descripcion character varying NOT NULL,
  monto double precision NOT NULL,
  mes_ejecucion character varying NOT NULL,
  retorno_esperado_pct double precision,
  meses_retorno integer,
  estado character varying NOT NULL DEFAULT 'PLANEADA'::character varying,
  fecha_creacion timestamp without time zone DEFAULT now(),
  id_categoria uuid,
  id_hato uuid NOT NULL,
  CONSTRAINT inversion_planeada_pkey PRIMARY KEY (id_inversion),
  CONSTRAINT inversion_planeada_id_categoria_fkey FOREIGN KEY (id_categoria) REFERENCES public.categoria_financiera(id_categoria),
  CONSTRAINT inversion_planeada_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.kpi (
  id_kpi integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  nombre character varying NOT NULL,
  descripcion text,
  formula text,
  unidad character varying,
  codigo character varying UNIQUE,
  categoria character varying,
  CONSTRAINT kpi_pkey PRIMARY KEY (id_kpi)
);
CREATE TABLE public.kpi_hato (
  id_kpi_hato integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_hato uuid NOT NULL,
  id_kpi integer NOT NULL,
  valor real,
  fecha_calculo date NOT NULL DEFAULT CURRENT_DATE,
  periodo character varying,
  estado character varying,
  CONSTRAINT kpi_hato_pkey PRIMARY KEY (id_kpi_hato),
  CONSTRAINT fk_kpihato_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT fk_kpihato_kpi FOREIGN KEY (id_kpi) REFERENCES public.kpi(id_kpi)
);
CREATE TABLE public.perfil_financiero (
  id_perfil_financiero uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL,
  metodo_registro character varying NOT NULL CHECK (metodo_registro::text = ANY (ARRAY['EXCEL'::character varying::text, 'MANUAL'::character varying::text, 'OMITIDO'::character varying::text])),
  fecha_registro date NOT NULL DEFAULT CURRENT_DATE,
  periodo character varying,
  descripcion text,
  CONSTRAINT perfil_financiero_pkey PRIMARY KEY (id_perfil_financiero),
  CONSTRAINT perfil_financiero_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.perfil_financiero_detalle (
  id_detalle uuid NOT NULL DEFAULT gen_random_uuid(),
  id_perfil_financiero uuid NOT NULL,
  id_categoria uuid NOT NULL,
  tipo character varying NOT NULL CHECK (tipo::text = ANY (ARRAY['INGRESO'::character varying::text, 'GASTO'::character varying::text, 'COSTO'::character varying::text, 'INVERSION'::character varying::text])),
  titulo character varying NOT NULL,
  monto_mensual double precision NOT NULL,
  fecha_registro date NOT NULL DEFAULT CURRENT_DATE,
  CONSTRAINT perfil_financiero_detalle_pkey PRIMARY KEY (id_detalle),
  CONSTRAINT perfil_financiero_detalle_id_perfil_financiero_fkey FOREIGN KEY (id_perfil_financiero) REFERENCES public.perfil_financiero(id_perfil_financiero),
  CONSTRAINT perfil_financiero_detalle_id_categoria_fkey FOREIGN KEY (id_categoria) REFERENCES public.categoria_financiera(id_categoria)
);
CREATE TABLE public.perfil_productivo (
  id_perfil uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL UNIQUE,
  raza_predominante character varying NOT NULL,
  produccion_diaria_litros double precision NOT NULL,
  precio_litro_promedio double precision NOT NULL,
  fecha_registro date NOT NULL DEFAULT CURRENT_DATE,
  fecha_actualizacion date,
  vacas_en_ordenio integer,
  frecuencia_ordenio integer,
  sistema_ordenio character varying,
  destino_leche character varying,
  periodo_lactancia_promedio integer,
  CONSTRAINT perfil_productivo_pkey PRIMARY KEY (id_perfil),
  CONSTRAINT perfil_productivo_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.practica (
  id_practica integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  nombre character varying NOT NULL,
  descripcion text,
  objetivo text,
  categoria character varying,
  impacto_esperado text,
  estado character varying,
  pasos text,
  kpi_impactado character varying,
  dificultad character varying NOT NULL DEFAULT 'MEDIA'::character varying,
  duracion_dias integer,
  escala character varying NOT NULL DEFAULT 'TODAS'::character varying,
  tropico_aplicable character varying NOT NULL DEFAULT 'TODOS'::character varying,
  CONSTRAINT practica_pkey PRIMARY KEY (id_practica)
);
CREATE TABLE public.produccion_leche (
  id_produccion uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL,
  fecha date NOT NULL,
  litros_producidos real,
  vacas_ordenadas integer,
  CONSTRAINT produccion_leche_pkey PRIMARY KEY (id_produccion),
  CONSTRAINT fk_prodleche_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.ranking_hato (
  id_ranking uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL UNIQUE,
  score_compuesto real,
  posicion_nacional integer,
  posicion_regional integer,
  total_nacional integer,
  total_regional integer,
  fecha_calculo date NOT NULL DEFAULT CURRENT_DATE,
  CONSTRAINT ranking_hato_pkey PRIMARY KEY (id_ranking),
  CONSTRAINT ranking_hato_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.raza (
  id_raza integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  tipo_raza character varying,
  nombre character varying NOT NULL,
  CONSTRAINT raza_pkey PRIMARY KEY (id_raza)
);
CREATE TABLE public.recomendacion_general (
  id_recomendacion bigint NOT NULL DEFAULT nextval('recomendacion_general_id_recomendacion_seq'::regclass),
  tipo character varying NOT NULL,
  subtipo character varying,
  titulo character varying NOT NULL,
  mensaje text NOT NULL,
  prioridad character varying NOT NULL DEFAULT 'MEDIA'::character varying,
  estado character varying NOT NULL DEFAULT 'ACTIVA'::character varying,
  leida boolean NOT NULL DEFAULT false,
  fecha_creacion timestamp without time zone NOT NULL DEFAULT now(),
  fecha_expiracion date,
  icono character varying,
  url_accion character varying,
  label_accion character varying,
  id_hato uuid,
  CONSTRAINT recomendacion_general_pkey PRIMARY KEY (id_recomendacion),
  CONSTRAINT recomendacion_general_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.recomendacion_hato (
  id_recomendacion_hato integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_hato uuid NOT NULL,
  tipo character varying,
  mensaje text,
  indicador character varying,
  valor_actual real,
  valor_referencia real,
  leida boolean NOT NULL DEFAULT false,
  id_regla integer,
  prioridad character varying NOT NULL DEFAULT 'MEDIA'::character varying,
  tipo_estado character varying NOT NULL DEFAULT 'ACTIVA'::character varying,
  fecha_creacion date NOT NULL DEFAULT CURRENT_DATE,
  escala_hato character varying,
  tropico_hato character varying,
  region_hato character varying,
  CONSTRAINT recomendacion_hato_pkey PRIMARY KEY (id_recomendacion_hato),
  CONSTRAINT fk_rechato_hato FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT recomendacion_hato_id_regla_fkey FOREIGN KEY (id_regla) REFERENCES public.regla(id_regla)
);
CREATE TABLE public.registrofinanciero (
  id_registro uuid NOT NULL DEFAULT gen_random_uuid(),
  id_hato uuid NOT NULL,
  id_categoria uuid NOT NULL,
  titulo character varying NOT NULL,
  tipo_movimiento character varying NOT NULL,
  fecha date NOT NULL,
  descripcion text,
  monto real NOT NULL,
  es_historico boolean NOT NULL DEFAULT false,
  precision_fecha character varying DEFAULT 'EXACTA'::character varying,
  CONSTRAINT registrofinanciero_pkey PRIMARY KEY (id_registro),
  CONSTRAINT registrofinanciero_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato),
  CONSTRAINT registrofinanciero_id_categoria_fkey FOREIGN KEY (id_categoria) REFERENCES public.categoria_financiera(id_categoria)
);
CREATE TABLE public.regla (
  id_regla integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_kpi integer NOT NULL,
  id_practica integer,
  mensaje text,
  prioridad integer,
  operador character varying NOT NULL DEFAULT 'MENOR_QUE'::character varying,
  umbral_1 real,
  umbral_2 real,
  umbral_tipo character varying NOT NULL DEFAULT 'ABSOLUTO'::character varying,
  estado_kpi_objetivo character varying NOT NULL DEFAULT 'CRITICO'::character varying,
  escala_aplicable character varying NOT NULL DEFAULT 'TODAS'::character varying,
  estado character varying NOT NULL DEFAULT 'ACTIVA'::character varying,
  CONSTRAINT regla_pkey PRIMARY KEY (id_regla),
  CONSTRAINT fk_regla_kpi FOREIGN KEY (id_kpi) REFERENCES public.kpi(id_kpi),
  CONSTRAINT fk_regla_practica FOREIGN KEY (id_practica) REFERENCES public.practica(id_practica)
);
CREATE TABLE public.regla_practica (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_regla integer NOT NULL,
  id_practica integer NOT NULL,
  orden smallint NOT NULL DEFAULT 1,
  CONSTRAINT regla_practica_pkey PRIMARY KEY (id),
  CONSTRAINT regla_practica_id_regla_fkey FOREIGN KEY (id_regla) REFERENCES public.regla(id_regla),
  CONSTRAINT regla_practica_id_practica_fkey FOREIGN KEY (id_practica) REFERENCES public.practica(id_practica)
);
CREATE TABLE public.reporte_historial (
  id_reporte integer NOT NULL DEFAULT nextval('reporte_historial_id_reporte_seq'::regclass),
  tipo character varying NOT NULL,
  nombre character varying NOT NULL,
  fecha_generacion timestamp without time zone NOT NULL,
  configuracion_json text,
  periodo_desde character varying,
  periodo_hasta character varying,
  url_archivo text,
  tamanio_bytes bigint,
  estado character varying NOT NULL DEFAULT 'GENERADO'::character varying,
  id_hato uuid NOT NULL,
  CONSTRAINT reporte_historial_pkey PRIMARY KEY (id_reporte),
  CONSTRAINT reporte_historial_id_hato_fkey FOREIGN KEY (id_hato) REFERENCES public.hato(id_hato)
);
CREATE TABLE public.usuario (
  id_auth uuid UNIQUE,
  nombre text,
  apellido text,
  correo text UNIQUE,
  celular character varying,
  estado boolean NOT NULL DEFAULT true,
  fecha_creacion date NOT NULL DEFAULT now(),
  id_usuario uuid NOT NULL DEFAULT gen_random_uuid(),
  rol character varying NOT NULL DEFAULT 'USER'::character varying,
  CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario)
);
CREATE TABLE public.valorreferenciaganado (
  id_valor_referencia integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  id_raza integer NOT NULL,
  id_categoria integer NOT NULL,
  valor_promedio real,
  region character varying,
  anio integer,
  CONSTRAINT valorreferenciaganado_pkey PRIMARY KEY (id_valor_referencia),
  CONSTRAINT fk_valrefgan_raza FOREIGN KEY (id_raza) REFERENCES public.raza(id_raza),
  CONSTRAINT fk_valrefgan_categoria FOREIGN KEY (id_categoria) REFERENCES public.categoria_ganado(id_categoria)
);
CREATE TABLE public.ventaleche (
  id_ventaleche uuid NOT NULL DEFAULT gen_random_uuid(),
  id_registro uuid NOT NULL,
  fecha date,
  precio_litro real,
  litros_vendidos real,
  CONSTRAINT ventaleche_pkey PRIMARY KEY (id_ventaleche),
  CONSTRAINT ventaleche_id_registro_fkey FOREIGN KEY (id_registro) REFERENCES public.registrofinanciero(id_registro)
);