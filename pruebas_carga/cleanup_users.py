import requests
import logging
import json
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

if not SUPABASE_URL or not SERVICE_ROLE_KEY:
    raise ValueError("Error: Falta configurar las variables de Supabase en el archivo .env")
print(f"Conectando a: {SUPABASE_URL}")

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("cleanup_log.txt", encoding="utf-8")
    ]
)
log = logging.getLogger(__name__)

#  PASO 1: Leer usuarios desde JSON
def cargar_usuarios_json() -> list[dict]:
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  PASO 1 — Leyendo usuarios desde %s", ARCHIVO_USUARIOS)
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    try:
        with open(ARCHIVO_USUARIOS, "r", encoding="utf-8") as f:
            usuarios = json.load(f)
        log.info("  %d usuarios cargados desde el archivo", len(usuarios))
        return usuarios

    except FileNotFoundError:
        log.error("  No se encontró el archivo: %s", ARCHIVO_USUARIOS)
        log.error("    Asegúrate de haber corrido create_users_and_seed.py primero.")
        return []
    except json.JSONDecodeError as e:
        log.error("  Error leyendo el JSON: %s", e)
        return []
    except Exception as e:
        log.error("  Error inesperado leyendo el archivo: %s", e)
        return []

def eliminar_usuario_backend(usuario: dict, token: str, idx: int, total: int) -> bool:
    email = usuario["email"]
    log.info("[%02d/%02d] Eliminando datos del backend para: %s ...", idx, total, email)
    try:
        res = requests.delete(
            f"{API_BASE}/Usuario/me",
            headers={"Authorization": f"Bearer {token}"},
            timeout=30,
        )
        res.raise_for_status()
        log.info("[%02d/%02d] Datos eliminados del backend correctamente", idx, total)
        return True
    except requests.exceptions.HTTPError as e:
        log.warning("[%02d/%02d] Error HTTP en backend para %s: %s | Respuesta: %s",
                    idx, total, email, e, res.text)
        log.warning("[%02d/%02d] Continuando con eliminación en Supabase Auth...", idx, total)
    except Exception as e:
        log.warning("[%02d/%02d] Error eliminando del backend %s: %s", idx, total, email, e)
        log.warning("[%02d/%02d] Continuando con eliminación en Supabase Auth...", idx, total)
    return False


def eliminar_usuario_supabase(usuario: dict, idx: int, total: int) -> bool:
    email   = usuario["email"]
    user_id = usuario["id"]
    log.info("[%02d/%02d] Eliminando usuario de Supabase Auth: %s ...", idx, total, email)
    try:
        res = requests.delete(
            f"{SUPABASE_URL}/auth/v1/admin/users/{user_id}",
            headers={
                "apikey":        SERVICE_ROLE_KEY,
                "Authorization": f"Bearer {SERVICE_ROLE_KEY}",
            },
            timeout=10,
        )
        res.raise_for_status()
        log.info("[%02d/%02d] Usuario eliminado de Supabase Auth correctamente", idx, total)
        return True
    except requests.exceptions.HTTPError as e:
        log.error("[%02d/%02d] Error HTTP eliminando %s de Supabase: %s | Respuesta: %s",
                  idx, total, email, e, res.text)
    except Exception as e:
        log.error("[%02d/%02d] Error eliminando %s de Supabase: %s", idx, total, email, e)
    return False


# ══════════════════════════════════════════════════════════════════
#  PASO 2: Login → JWT token (necesario para llamar al backend)
# ══════════════════════════════════════════════════════════════════

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
            log.warning("[%02d/%02d] Login sin token para %s → %s",
                        idx, total, email, res.json())
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
        log.error("[%02d/%02d] Error inesperado en login de %s: %s",
                  idx, total, email, e)

    return None

def main():
    log.info("╔══════════════════════════════════════════════════╗")
    log.info("║      LIMPIEZA POST-JMETER — Eliminar usuarios    ║")
    log.info("║      Backend: %-34s ║", API_BASE)
    log.info("╚══════════════════════════════════════════════════╝")
    log.info("")

    # ── Paso 1: Leer JSON ─────────────────────────────────────────
    usuarios = cargar_usuarios_json()
    if not usuarios:
        log.error("✗ No hay usuarios para eliminar. Abortando.")
        return

    total = len(usuarios)

    print(f"  Archivo leído: {ARCHIVO_USUARIOS}")
    print(f"  Esto borrará los datos del hato en el backend Y los usuarios en Supabase.")
    confirmacion = input("\n¿Deseas continuar? (escribe 'si' para confirmar): ").strip().lower()

    if confirmacion != "si":
        log.info("Operación cancelada por el usuario.")
        return

    log.info("Confirmación recibida. Iniciando limpieza...")
    log.info("")

    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    log.info("  PASOS 2, 3 y 4 — Login, borrar hato y borrar usuario")
    log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    exitosos_hato     = 0
    exitosos_supabase = 0
    fallidos_login    = 0
    fallidos_hato     = 0
    fallidos_supabase = 0

    exitosos_backend  = 0
    fallidos_backend  = 0
    fallidos_supabase = 0

    exitosos  = 0
    fallidos_eliminar = 0

    for idx, usuario in enumerate(usuarios, 1):
        log.info("")
        log.info("┌─ Usuario %02d/%02d ──────────────────────────────────", idx, total)
        log.info("│  Email: %s", usuario["email"])
        log.info("│  ID:    %s", usuario["id"])
        log.info("└─────────────────────────────────────────────────────")

        token = login(usuario["email"], idx, total)
        if not token:
            log.warning("[%02d/%02d] ⚠ Sin token — intentando borrar solo de Supabase Auth.", idx, total)
            fallidos_login += 1
            ok = eliminar_usuario_supabase(usuario, idx, total)
            if ok:
                exitosos_supabase += 1
            else:
                fallidos_supabase += 1
            continue

        backend_ok = eliminar_usuario_backend(usuario, token, idx, total)
        if backend_ok:
            exitosos_backend += 1
        else:
            fallidos_backend += 1

        supabase_ok = eliminar_usuario_supabase(usuario, idx, total)
        if supabase_ok:
            exitosos_supabase += 1
        else:
            fallidos_supabase += 1

        time.sleep(0.2)
    # ── Resumen ───────────────────────────────────────────────────
    log.info("")
    log.info("╔══════════════════════════════════════════════════╗")
    log.info("║                  RESUMEN FINAL                  ║")
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Usuarios procesados:       %02d / %02d              ║", total - fallidos_login, total)
    log.info("║  Hatos eliminados:          %02d                    ║", exitosos_hato)
    log.info("║  Usuarios eliminados Auth:  %02d                    ║", exitosos_supabase)
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Backend eliminados:    %02d / %02d                 ║", exitosos_backend, total)
    log.info("║  Supabase eliminados:   %02d / %02d                 ║", exitosos_supabase, total)
    log.info("║  Fallos en login:       %02d                       ║", fallidos_login)
    log.info("║  Fallos en backend:     %02d                       ║", fallidos_backend)
    log.info("║  Fallos en Supabase:    %02d                       ║", fallidos_supabase)
    log.info("╠══════════════════════════════════════════════════╣")
    log.info("║  Log guardado en: cleanup_log.txt               ║")
    log.info("╚══════════════════════════════════════════════════╝")

    if fallidos_hato > 0 or fallidos_supabase > 0:
        log.warning("Revisar cleanup_log.txt.")

if __name__ == "__main__":
    main()