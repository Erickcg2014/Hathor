import requests
import random
import logging
import time
import json
import os
from dotenv import load_dotenv
from datetime import date, timedelta


#  CONFIGURACIÓN

load_dotenv()
SUPABASE_URL = os.getenv("SUPABASE_URL")
SERVICE_ROLE_KEY = os.getenv("SERVICE_ROLE_KEY")
API_BASE = os.getenv("API_BASE")
ANON_KEY = os.getenv("ANON_kEY")

NUM_USUARIOS     = 50
PASSWORD         = "LoadTest123!"
EMAIL_DOMINIO    = "loadtest.com"

if not SUPABASE_URL or not SERVICE_ROLE_KEY:
    raise ValueError("Error: Falta configurar las variables de Supabase en el archivo .env")
print(f"Conectando a: {SUPABASE_URL}")

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("seed_log.txt", encoding="utf-8")
    ]
)
log = logging.getLogger(__name__)

CIUDADES = [
    {
        "departamento": "Antioquia",
        "ciudad":       "Rionegro",
        "tropico":      "FRIO",
        "altitud":      2150.0,
        "latitud":      6.1543,
        "longitud":     -75.3742,
    },
    {
        "departamento": "Santander",
        "ciudad":       "Bucaramanga",
        "tropico":      "CALIDO",
        "altitud":      959.0,
        "latitud":      7.1193,
        "longitud":     -73.1227,
    },
    {
        "departamento": "Cundinamarca",
        "ciudad":       "Bogotá",
        "tropico":      "FRIO",
        "altitud":      2640.0,
        "latitud":      4.711,
        "longitud":     -74.0721,
    },
    {
        "departamento": "Valle del Cauca",
        "ciudad":       "Cali",
        "tropico":      "TEMPLADO",
        "altitud":      1018.0,
        "latitud":      3.4516,
        "longitud":     -76.532,
    },
]

PREFIJOS_HATO = ["Hacienda", "Finca", "Rancho", "El Hato", "Granja"]
NOMBRES_HATO  = [
    "La Esperanza", "El Porvenir", "Santa Rosa", "Los Alpes",
    "El Paraíso",   "La Montaña",  "San José",   "Villa Verde",
    "El Retiro",    "Las Palmas",  "El Progreso", "La Bonanza",
    "Los Pinos",    "El Edén",     "La Colina",
]

TIPOS_HATO = ["Lechería", "Doble propósito", "Cría"]
ESCALAS    = ["PEQUEÑA", "MEDIANA", "GRANDE"]
RAZAS      = ["Holstein-Friesian", "Normando", "Gyr Lechero", "Simmental"]
SISTEMAS   = ["MECANICO", "MANUAL"]
DESTINOS   = ["INDUSTRIA", "QUESERÍA", "VENTA DIRECTA"]

CATEGORIAS_GANADO = [
    # (categoria, edadMin, edadMax, valorMin, valorMax)
    ("Vaca de producción", 36, 60, 4_000_000, 5_000_000),
    ("Vaca seca",          48, 72, 3_500_000, 4_500_000),
    ("Novilla",            12, 24, 2_000_000, 3_200_000),
    ("Ternera",             2,  8,   600_000, 1_000_000),
]

MESES_FINANZAS = ["2025-10", "2025-11", "2025-12"]


def rnd_float(a, b, decimales=1):
    return round(random.uniform(a, b), decimales)

def rnd_int(a, b):
    return random.randint(a, b)

def rnd_monto(minimo, maximo):
    return round(random.randint(minimo // 1000, maximo // 1000) * 1000, 0)

def nombre_hato():
    return f"{random.choice(PREFIJOS_HATO)} {random.choice(NOMBRES_HATO)}"

def direccion_aleatoria():
    veredas = [
        "Vereda El Chagualo", "Vereda La Unión", "Vereda El Porvenir",
        "Vereda Santa Bárbara", "Corregimiento El Carmen",
    ]
    return f"{random.choice(veredas)} km {rnd_int(1, 10)}"

def generar_payload(user_id: str) -> dict:
    ciudad    = random.choice(CIUDADES)
    vacas_ord = rnd_int(10, 45)
    total_vac = vacas_ord + rnd_int(3, 12)
    prod_dia  = round(vacas_ord * rnd_float(14, 20), 1)
    area_hato = rnd_float(20, 100)

    hato = {
        "nombreHato":               nombre_hato(),
        "tipoHato":                 random.choice(TIPOS_HATO),
        "departamento":             ciudad["departamento"],
        "ciudad":                   ciudad["ciudad"],
        "direccion":                direccion_aleatoria(),
        "tropico":                  ciudad["tropico"],
        "escala":                   random.choice(ESCALAS),
        "areaHato":                 area_hato,
        "areaPastoreo":             rnd_float(area_hato * 0.5, area_hato * 0.85),
        "altitud":                  ciudad["altitud"],
        "latitud":                  ciudad["latitud"],
        "longitud":                 ciudad["longitud"],
        "cantCorrales":             rnd_int(2, 6),
        "cantSalasOrdenio":         rnd_int(1, 2),
        "capacidadAlmacenarLeche":  float(rnd_int(200, 800)),
        "cantEmpleadosPermanentes": rnd_int(1, 5),
        "cantEmpleadosTemporales":  rnd_int(0, 3),
        "gastoMensualNomina":       rnd_monto(1_500_000, 6_000_000),
        "gastoMensualAlimentacion": rnd_monto(1_000_000, 4_000_000),
    }

    perfil_productivo = {
        "vacasEnOrdenio":         vacas_ord,
        "totalVacas":             total_vac,
        "produccionDiariaLitros": prod_dia,
        "precioLitroPromedio":    rnd_float(1300, 1650, 0),
        "diasLactancia":          rnd_int(240, 305),
        "frecuenciaOrdenio":      random.choice([1, 2]),
        "destinoLeche":           random.choice(DESTINOS),
        "razaPredominante":       random.choice(RAZAS),
        "sistemaOrdenio":         random.choice(SISTEMAS),
    }

    inventario_ganado = []
    for cat, edad_min, edad_max, val_min, val_max in CATEGORIAS_GANADO:
        cant = vacas_ord if cat == "Vaca de producción" else rnd_int(1, 8)
        inventario_ganado.append({
            "nombreCategoria":   cat,
            "nombreRaza":        random.choice(RAZAS),
            "cantidad":          cant,
            "edadPromedioMeses": rnd_int(edad_min, edad_max),
            "valorUnitario":     float(rnd_monto(val_min, val_max)),
        })

    finanzas = []
    ingreso_leche_base = prod_dia * 30 * perfil_productivo["precioLitroPromedio"]
    for mes in MESES_FINANZAS:
        variacion = rnd_float(0.92, 1.08)
        finanzas.append({
            "mes": mes,
            "ingresos": [
                {"nombreCategoria": "VENTA DE LECHE",
                 "monto": round(ingreso_leche_base * variacion, 0)},
                {"nombreCategoria": "BONIFICACIONES Y PREMIOS",
                 "monto": rnd_monto(200_000, 600_000)},
            ],
            "egresos": [
                {"nombreCategoria": "CONCENTRADO",
                 "monto": rnd_monto(1_800_000, 3_500_000)},
                {"nombreCategoria": "SAL MINERALIZADA",
                 "monto": rnd_monto(200_000, 450_000)},
                {"nombreCategoria": "SALARIOS",
                 "monto": hato["gastoMensualNomina"]},
                {"nombreCategoria": "SERVICIOS VETERINARIOS",
                 "monto": rnd_monto(100_000, 400_000)},
                {"nombreCategoria": "COMBUSTIBLE",
                 "monto": rnd_monto(100_000, 300_000)},
                {"nombreCategoria": "ENERGÍA ELÉCTRICA",
                 "monto": rnd_monto(80_000, 200_000)},
            ],
        })

    produccion = []
    inicio = date(2025, 10, 1)
    for i in range(31):
        dia       = inicio + timedelta(days=i)
        vacas_dia = vacas_ord if random.random() > 0.15 else vacas_ord - rnd_int(1, 2)
        produccion.append({
            "fecha":            dia.isoformat(),
            "litrosProducidos": round(vacas_dia * rnd_float(14, 20), 1),
            "vacasOrdenadas":   vacas_dia,
        })

    return {
        "idUsuarioAuth":     user_id,
        "perfil":            random.choice(["BUENO", "REGULAR", "EXCELENTE"]),
        "hato":              hato,
        "perfilProductivo":  perfil_productivo,
        "inventarioGanado":  inventario_ganado,
        "inventarioGeneral": [],
        "finanzas":          finanzas,
        "produccion":        produccion,
        "practicas":         [],
    }

#  FASE 1: Crear usuarios en Supabase

def crear_usuarios() -> list[dict]:
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  FASE 1 — Creando %d usuarios en Supabase Auth", NUM_USUARIOS)
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    usuarios = []

    for i in range(1, NUM_USUARIOS + 1):
        email = f"loadtest_{i:02d}@{EMAIL_DOMINIO}"
        log.info("[%02d/%02d] Creando usuario: %s ...", i, NUM_USUARIOS, email)

        try:
            res = requests.post(
                f"{SUPABASE_URL}/auth/v1/admin/users",
                headers={
                    "apikey":        SERVICE_ROLE_KEY,
                    "Authorization": f"Bearer {SERVICE_ROLE_KEY}",
                    "Content-Type":  "application/json",
                },
                json={
                    "email":         email,
                    "password":      PASSWORD,
                    "email_confirm": True,
                },
                timeout=10,
            )
            res.raise_for_status()
            data    = res.json()
            user_id = data.get("id")

            if not user_id:
                log.warning("[%02d/%02d] Sin ID en respuesta para %s → %s",
                            i, NUM_USUARIOS, email, data)
                continue

            usuarios.append({"email": email, "password": PASSWORD, "id": user_id})
            log.info("[%02d/%02d] Usuario creado correctamente | id: %s",
                     i, NUM_USUARIOS, user_id)

        except requests.exceptions.HTTPError as e:
            log.error("[%02d/%02d] Error HTTP al crear %s: %s | Respuesta: %s",
                      i, NUM_USUARIOS, email, e, res.text)
        except requests.exceptions.ConnectionError:
            log.error("[%02d/%02d] No se pudo conectar a Supabase. ¿La URL es correcta?",
                      i, NUM_USUARIOS)
        except requests.exceptions.Timeout:
            log.error("[%02d/%02d] Timeout al crear %s", i, NUM_USUARIOS, email)
        except Exception as e:
            log.error("[%02d/%02d] Error inesperado al crear %s: %s",
                      i, NUM_USUARIOS, email, e)

    log.info("")
    log.info("  Fase 1 completada → Usuarios creados: %d / %d", len(usuarios), NUM_USUARIOS)
    return usuarios

def registrar_usuario_backend(usuario: dict, idx: int, total: int) -> bool:
    email = usuario["email"]
    log.info("[%02d/%02d] Registrando usuario en el backend ...", idx, total)
    try:
        res = requests.post(
            f"{API_BASE}/Usuario",
            headers={"Content-Type": "application/json"},
            json={
                "idAuth":   usuario["id"],
                "nombre":   f"LoadTest",
                "apellido": f"{idx:02d}",
                "correo":   email,
                "celular":  "3000000000"
            },
            timeout=10,
        )
        res.raise_for_status()
        log.info("[%02d/%02d] Usuario registrado en el backend correctamente", idx, total)
        return True
    except requests.exceptions.HTTPError as e:
        log.error("[%02d/%02d] Error HTTP registrando en backend %s: %s | Respuesta: %s",
                  idx, total, email, e, res.text)
    except requests.exceptions.ConnectionError:
        log.error("[%02d/%02d] No se pudo conectar al backend. ¿Está corriendo en %s?",
                  idx, total, API_BASE)
    except requests.exceptions.Timeout:
        log.error("[%02d/%02d] Timeout registrando en backend %s", idx, total, email)
    except Exception as e:
        log.error("[%02d/%02d] Error inesperado registrando en backend %s: %s",
                  idx, total, email, e)
    return False

#  FASE 2: Login → JWT token

def login(email: str, password: str, idx: int, total: int) -> str | None:
    log.info("[%02d/%02d] Iniciando sesión con: %s ...", idx, total, email)
    try:
        res = requests.post(
            f"{SUPABASE_URL}/auth/v1/token?grant_type=password",
            headers={
                "apikey":       ANON_KEY,
                "Content-Type": "application/json",
            },
            json={"email": email, "password": password},
            timeout=10,
        )
        res.raise_for_status()
        token = res.json().get("access_token")

        if not token:
            log.warning("[%02d/%02d] ⚠ Login sin token para %s → %s",
                        idx, total, email, res.json())
            return None

        log.info("[%02d/%02d] ✓ Token obtenido correctamente", idx, total)
        return token

    except requests.exceptions.HTTPError as e:
        log.error("[%02d/%02d] ✗ Error HTTP en login de %s: %s | Respuesta: %s",
                  idx, total, email, e, res.text)
    except requests.exceptions.ConnectionError:
        log.error("[%02d/%02d] ✗ No se pudo conectar a Supabase Auth.", idx, total)
    except requests.exceptions.Timeout:
        log.error("[%02d/%02d] ✗ Timeout en login de %s", idx, total, email)
    except Exception as e:
        log.error("[%02d/%02d] ✗ Error inesperado en login de %s: %s",
                  idx, total, email, e)

    return None

# ══════════════════════════════════════════════════════════════════
#  FASE 3: Generar payload y llamar /seed/hato
# ══════════════════════════════════════════════════════════════════

def sembrar_hato(usuario: dict, token: str, idx: int, total: int) -> bool:
    email   = usuario["email"]
    user_id = usuario["id"]

    try:
        payload      = generar_payload(user_id)
        nombre       = payload["hato"]["nombreHato"]
        ciudad       = payload["hato"]["ciudad"]
        departamento = payload["hato"]["departamento"]
        vacas        = payload["perfilProductivo"]["vacasEnOrdenio"]
        prod         = payload["perfilProductivo"]["produccionDiariaLitros"]

        log.info("[%02d/%02d] Cargando información del hato ...", idx, total)
        log.info("[%02d/%02d]   → Nombre:        %s", idx, total, nombre)
        log.info("[%02d/%02d]   → Ubicación:     %s, %s", idx, total, ciudad, departamento)
        log.info("[%02d/%02d]   → Vacas ordeño:  %d | Producción/día: %.1f L",
                 idx, total, vacas, prod)
        inicio = time.time() 
        res = requests.post(
            f"{API_BASE}/seed/hato",
            headers={
                "Authorization": f"Bearer {token}",
                "Content-Type":  "application/json",
            },
            json=payload,
            timeout=180
        )

        duracion = time.time() - inicio
        res.raise_for_status()
        log.info("[%02d/%02d] ✓ Hato '%s' creado exitosamente [HTTP %d] — %.1fs",
                 idx, total, nombre, res.status_code, duracion)
        return True

    except requests.exceptions.HTTPError as e:
        duracion = time.time() - inicio
        log.error("[%02d/%02d] ✗ Error HTTP en /seed/hato para %s: %s | Respuesta: %s | %.1fs",
                  idx, total, email, e, res.text, duracion)
    except requests.exceptions.Timeout:
        log.error("[%02d/%02d] ✗ Timeout en /seed/hato para %s (>180s)", idx, total, email)
    except Exception as e:
        log.error("[%02d/%02d] ✗ Error inesperado en /seed/hato para %s: %s",
                  idx, total, email, e)

    return False

def guardar_usuarios_json(usuarios: list[dict]):
    with open("usuarios_prueba.json", "w", encoding="utf-8") as f:
        json.dump(usuarios, f, indent=2, ensure_ascii=False)
    log.info("Usuarios guardados en: usuarios_prueba.json")

# ══════════════════════════════════════════════════════════════════
#  MAIN
# ══════════════════════════════════════════════════════════════════

def main():
    log.info("╔══════════════════════════════════════════════════╗")
    log.info("║      SEED MASIVO — %d usuarios de prueba        ║", NUM_USUARIOS)
    log.info("║      Backend: %-34s ║", API_BASE)
    log.info("╚══════════════════════════════════════════════════╝")
    log.info("")

    exitosos_seed  = 0
    fallidos_backend  = 0
    fallidos_login = 0
    fallidos_seed  = 0

    # ── Fase 1 ────────────────────────────────────────────────────
    usuarios = crear_usuarios()
    if not usuarios:
        log.error("✗ No se creó ningún usuario. Abortando proceso.")
        return
    guardar_usuarios_json(usuarios)

    # ── Fases 2 + 3 ───────────────────────────────────────────────
    log.info("")
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  FASE 2 + 3 — Login y carga de datos por usuario")
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    total = len(usuarios)
    for idx, usuario in enumerate(usuarios, 1):
        log.info("")
        log.info("┌─ Usuario %02d/%02d ──────────────────────────────────", idx, total)
        log.info("│  Email: %s", usuario["email"])
        log.info("│  ID:    %s", usuario["id"])
        log.info("└─────────────────────────────────────────────────────")

        # Fase 2: Registrar usuario en el backend
        backend_ok = registrar_usuario_backend(usuario, idx, total)
        if not backend_ok:
            log.warning("[%02d/%02d] ⚠ Saltando seed por fallo en registro del backend.", idx, total)
            fallidos_backend += 1
            continue

        # Fase 3: Login → obtener token
        token = login(usuario["email"], usuario["password"], idx, total)
        if not token:
            log.warning("[%02d/%02d] ⚠ Saltando carga de datos por fallo en login.", idx, total)
            fallidos_login += 1
            continue

        # Fase 4: Seed del hato
        ok = sembrar_hato(usuario, token, idx, total)
        if ok:
            exitosos_seed += 1
        else:
            fallidos_seed += 1

        time.sleep(0.3)  # Pausa para no saturar el rate limit de Supabase Auth

    # ── Resumen final ─────────────────────────────────────────────
    log.info("")
    log.info("╔══════════════════════════════════════════════════╗")
    log.info("║                  RESUMEN FINAL                  ║")
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Usuarios creados:     %02d / %02d                  ║", len(usuarios), NUM_USUARIOS)
    log.info("║  Hatos cargados:       %02d                        ║", exitosos_seed)
    log.info("║  Fallos en login:      %02d                        ║", fallidos_login)
    log.info("║  Fallos en seed:       %02d                        ║", fallidos_seed)
    log.info("║  Fallos en registro backend: %02d                  ║", fallidos_backend)
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Log guardado en: seed_log.txt                  ║")
    log.info("╚══════════════════════════════════════════════════╝")

if __name__ == "__main__":
    main()