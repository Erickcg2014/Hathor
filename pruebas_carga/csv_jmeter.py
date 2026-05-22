import requests
import logging
import json
import csv
import time
import os 
from dotenv import load_dotenv

#  CONFIGURACIÓN
load_dotenv()
SUPABASE_URL = os.getenv("SUPABASE_URL")
SERVICE_ROLE_KEY = os.getenv("SERVICE_ROLE_KEY")
API_BASE = os.getenv("API_BASE") 
ANON_KEY = os.getenv("ANON_KEY")
PASSWORD         = "LoadTest123!"
ARCHIVO_USUARIOS = "usuarios_prueba.json"
ARCHIVO_JMETER   = "usuarios_jmeter.csv"

if not SUPABASE_URL or not SERVICE_ROLE_KEY:
    raise ValueError("Error: Falta configurar las variables de Supabase en el archivo .env")
print(f"Conectando a: {SUPABASE_URL}")

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("jmeter_csv_log.txt", encoding="utf-8")
    ]
)
log = logging.getLogger(__name__)

#  PASO 1: Leer usuarios_prueba.json

def cargar_usuarios_json() -> list[dict]:
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  PASO 1 — Leyendo usuarios desde %s", ARCHIVO_USUARIOS)
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    try:
        with open(ARCHIVO_USUARIOS, "r", encoding="utf-8") as f:
            usuarios = json.load(f)
        log.info("  %d usuarios cargados", len(usuarios))
        return usuarios
    except FileNotFoundError:
        log.error("  No se encontró %s. Corre create_users_and_seed.py primero.", ARCHIVO_USUARIOS)
        return []
    except json.JSONDecodeError as e:
        log.error("  Error leyendo el JSON: %s", e)
        return []
    except Exception as e:
        log.error("  Error inesperado: %s", e)
        return []

#  PASO 2: Login → JWT token

def login(email: str, idx: int, total: int) -> str | None:
    log.info("[%02d/%02d] Iniciando sesión con: %s ...", idx, total, email)
    try:
        res = requests.post(
            f"{SUPABASE_URL}/auth/v1/token?grant_type=password",
            headers={
                "apikey":       ANON_KEY,
                "Content-Type": "application/json",
            },
            json={"email": email, "password": PASSWORD},
            timeout=10,
        )
        res.raise_for_status()
        token = res.json().get("access_token")

        if not token:
            log.warning("[%02d/%02d] Login sin token para %s", idx, total, email)
            return None

        log.info("[%02d/%02d] Token obtenido correctamente", idx, total)
        return token

    except requests.exceptions.HTTPError as e:
        log.error("[%02d/%02d] Error HTTP en login de %s: %s | Respuesta: %s",
                  idx, total, email, e, res.text)
    except requests.exceptions.ConnectionError:
        log.error("[%02d/%02d] No se pudo conectar a Supabase Auth.", idx, total)
    except requests.exceptions.Timeout:
        log.error("[%02d/%02d] Timeout en login de %s", idx, total, email)
    except Exception as e:
        log.error("[%02d/%02d] Error inesperado en login de %s: %s", idx, total, email, e)
    return None

#  PASO 3: Obtener idHato desde GET /Hato/me

def obtener_id_hato(token: str, email: str, idx: int, total: int) -> str | None:
    log.info("[%02d/%02d] Obteniendo idHato para: %s ...", idx, total, email)
    try:
        res = requests.get(
            f"{API_BASE}/Hato/me",
            headers={
                "Authorization": f"Bearer {token}",
                "Content-Type":  "application/json",
            },
            timeout=10,
        )
        res.raise_for_status()
        hatos = res.json()

        if not hatos or len(hatos) == 0:
            log.warning("[%02d/%02d] No se encontraron hatos para %s", idx, total, email)
            return None

        id_hato = hatos[0].get("idHato")
        nombre  = hatos[0].get("nombreHato", "sin nombre")

        if not id_hato:
            log.warning("[%02d/%02d] idHato no encontrado en respuesta para %s", idx, total, email)
            return None

        log.info("[%02d/%02d] idHato obtenido | '%s' → %s", idx, total, nombre, id_hato)
        return id_hato

    except requests.exceptions.HTTPError as e:
        log.error("[%02d/%02d] Error HTTP obteniendo hato de %s: %s | Respuesta: %s",
                  idx, total, email, e, res.text)
    except requests.exceptions.ConnectionError:
        log.error("[%02d/%02d] No se pudo conectar al backend. ¿Está corriendo en %s?",
                  idx, total, API_BASE)
    except requests.exceptions.Timeout:
        log.error("[%02d/%02d] Timeout obteniendo hato de %s", idx, total, email)
    except Exception as e:
        log.error("[%02d/%02d] Error inesperado obteniendo hato de %s: %s",
                  idx, total, email, e)
    return None

#  PASO 4: Guardar CSV para JMeter

def guardar_csv(filas: list[dict]):
    log.info("")
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  PASO 4 — Guardando %s", ARCHIVO_JMETER)
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    try:
        with open(ARCHIVO_JMETER, "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=["email", "token", "idHato"])
            writer.writeheader()
            writer.writerows(filas)
        log.info("  CSV guardado con %d filas en: %s", len(filas), ARCHIVO_JMETER)
    except Exception as e:
        log.error("  Error guardando CSV: %s", e)


def main():
    log.info("╔══════════════════════════════════════════════════╗")
    log.info("║   GENERAR CSV PARA JMETER                        ║")
    log.info("║   Backend: %-38s ║", API_BASE)
    log.info("╚══════════════════════════════════════════════════╝")
    log.info("")

    usuarios = cargar_usuarios_json()
    if not usuarios:
        log.error("No hay usuarios. Abortando.")
        return

    total         = len(usuarios)
    filas_csv     = []
    exitosos      = 0
    fallidos_login = 0
    fallidos_hato  = 0

    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  PASOS 2 y 3 — Login y obtención de idHato")
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    for idx, usuario in enumerate(usuarios, 1):
        email = usuario["email"]
        log.info("")
        log.info("┌─ Usuario %02d/%02d ──────────────────────────────────", idx, total)
        log.info("│  Email: %s", email)
        log.info("└─────────────────────────────────────────────────────")

        token = login(email, idx, total)
        if not token:
            log.warning("[%02d/%02d] Saltando por fallo en login.", idx, total)
            fallidos_login += 1
            continue

        id_hato = obtener_id_hato(token, email, idx, total)
        if not id_hato:
            log.warning("[%02d/%02d] Saltando por fallo al obtener idHato.", idx, total)
            fallidos_hato += 1
            continue

        filas_csv.append({
            "email":  email,
            "token":  token,
            "idHato": id_hato,
        })
        exitosos += 1

        time.sleep(0.2)

    if filas_csv:
        guardar_csv(filas_csv)
    else:
        log.error("No hay datos para guardar en el CSV.")

    log.info("")
    log.info("╔══════════════════════════════════════════════════╗")
    log.info("║                  RESUMEN FINAL                  ║")
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Usuarios procesados:   %02d / %02d                 ║", total, total)
    log.info("║  Filas en CSV:          %02d                       ║", exitosos)
    log.info("║  Fallos en login:       %02d                       ║", fallidos_login)
    log.info("║  Fallos al obtener hato:%02d                       ║", fallidos_hato)
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Archivo generado: %-30s ║", ARCHIVO_JMETER)
    log.info("║  Log guardado en:  jmeter_csv_log.txt           ║")
    log.info("╚══════════════════════════════════════════════════╝")

if __name__ == "__main__":
    main()