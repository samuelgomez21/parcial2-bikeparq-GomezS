# SEGUNDO PARCIAL — UNIDAD 2

**CORPORACIÓN UNIVERSITARIA EMPRESARIAL ALEXANDER VON HUMBOLDT**

Facultad de Ingenierías · Ingeniería de Software · Pruebas de Software

## Pruebas automatizadas, TDD, BDD, análisis estático y CI/CD

| | |
|---|---|
| **Programa** | Ingeniería de Software · V semestre |
| **Fecha** | Martes 17 de noviembre de 2026 |
| **Docente** | Jose Alfredo Ramírez Espinosa |
| **Duración** | 120 minutos |
| **Modalidad** | Individual, práctico, en computador |
| **Lenguaje** | Uno a elección: **Python**, **Java** o **PHP** |
| **Entrega** | URL del repositorio público de GitHub en la tarea de Moodle, antes de que termine la sesión |

---

## Instrucciones generales

1. Elijan **un** lenguaje: Python, Java o PHP.
2. Creen en su cuenta de GitHub un repositorio **público** llamado `parcial2-bikeparq-<primer-apellido>` (por ejemplo `parcial2-bikeparq-gomez`).
3. Descarguen de Moodle el proyecto base de su lenguaje (`bikeparq-python.zip`, `bikeparq-java.zip` o `bikeparq-php.zip`) o copien los archivos del anexo correspondiente. **Respeten la estructura de carpetas exacta.**
4. Hagan el primer commit con el proyecto base **sin cambios** (mensaje: `estado inicial`) y súbanlo. En la pestaña **Actions** verán que el job `analisis-estatico` falla (en PHP también `cobertura`): **es intencional**, ustedes lo arreglan durante el parcial.
5. Resuelvan los 5 puntos. Hagan commits pequeños y frecuentes; el historial de commits se revisa.
6. Al final, peguen la URL del repositorio en la tarea de Moodle. **Se califica el último commit subido antes del cierre de la tarea.** Los commits posteriores no se tienen en cuenta.

**Reglas**

- El pipeline (`.github/workflows/ci-cd.yml`) ya está hecho. **No lo modifiquen.**
- No modifiquen los archivos marcados con `NO MODIFIQUE ESTE ARCHIVO`.
- Pueden consultar la documentación oficial de las herramientas y los proyectos vistos en clase.
- No se permite el uso de asistentes de inteligencia artificial ni la comunicación con compañeros.

| Punto | Tema | Valor |
|---|---|---|
| 1 | Pruebas unitarias de 3 casos dados | 20 % (1,0) |
| 2 | Recargo nocturno con TDD (rojo, verde, refactor) | 25 % (1,25) |
| 3 | BDD en español: 3 escenarios del recargo con sus pasos | 20 % (1,0) |
| 4 | Análisis estático y Clean Code | 20 % (1,0) |
| 5 | Cobertura y pipeline en verde | 15 % (0,75) |
| | **Total** | **100 % (5,0)** |

---

## Contexto: parqueadero BikeParq

BikeParq cobra a carros y motos según el tiempo que permanecen. El cálculo **ya está programado** en el proyecto base (función `calcular_tarifa` / `calcularTarifa`) con estas reglas:

| Regla | Valor |
|---|---|
| Tiempo de gracia | Los primeros **10 minutos** no se cobran (10 minutos o menos → $0) |
| Cobro por minuto (después de la gracia) | Carro **$60**, moto **$30**, por cada minuto que pase de 10 |
| Tope diario | Carro **$25.000**, moto **$12.000** (nunca se cobra más que el tope) |
| Rango válido de minutos | De **0** a **1440**. Fuera de ese rango → error |
| Tipos válidos | `"carro"` y `"moto"`. Cualquier otro → error |

Ejemplos: un carro que permanece 60 minutos paga (60 − 10) × 60 = **$3.000**. Una moto que permanece 11 minutos paga 1 × 30 = **$30**.

**Lo que ya trae el proyecto base**

| Archivo | Qué es | ¿Lo modifican? |
|---|---|---|
| Tarifa (`tarifa.py` / `Tarifa.java` / `Tarifa.php`) | Cálculo del cobro, ya funciona | No |
| Reportes (`reportes.py` / `Reportes.java` / `Reportes.php`) | Resumen de caja del día. Funciona, pero está **mal escrito a propósito** | Sí, en el Punto 4 |
| Pruebas de reportes | Pruebas que ya pasan | **No** |
| Pasos del BDD y `cobro.feature` | Tres pasos que funcionan y un escenario de ejemplo | Sí, en el Punto 3: agregan escenarios y pasos nuevos, sin cambiar los que ya existen |
| Prueba de tarifa (`test_tarifa.py` / `TarifaTest.java`) | Archivo para el Punto 1 | Sí |
| `.github/workflows/ci-cd.yml` | Pipeline con 4 jobs y el despliegue simulado | **No** |

---

## Punto 1 — Pruebas unitarias (20 %)

Automaticen **exactamente estos 3 casos** de la función que calcula la tarifa. Escriban una prueba por caso.

| ID | Categoría | Entrada (minutos, tipo) | Resultado esperado |
|---|---|---|---|
| CP-01 | Válido | 60, `"carro"` | 3000 |
| CP-02 | Límite | 10, `"moto"` | 0 (último minuto de gracia) |
| CP-03 | Inválido | 60, `"bicicleta"` | Lanza error |

**Dónde van**

| Lenguaje | Archivo | Error que se espera en CP-03 |
|---|---|---|
| Python | `tests/test_tarifa.py` | `ValueError` (use `pytest.raises`) |
| Java | `src/test/java/bikeparq/TarifaTest.java` | `IllegalArgumentException` (use `assertThrows`) |
| PHP | `tests/TarifaTest.php` (lo crean ustedes, ver plantilla abajo) | `InvalidArgumentException` (use `expectException`) |

Plantilla para PHP (`tests/TarifaTest.php`):

```php
<?php

declare(strict_types=1);

namespace BikeParq\Tests;

use BikeParq\Tarifa;
use InvalidArgumentException;
use PHPUnit\Framework\TestCase;

final class TarifaTest extends TestCase
{
    // PUNTO 1: escriba aquí las pruebas CP-01, CP-02 y CP-03.
    // Cada método debe empezar por "test", por ejemplo: public function testCp01...(): void
}
```

Commit sugerido: `test: casos CP-01 a CP-03 de la tarifa`.

---

## Punto 2 — Recargo nocturno con TDD (25 %)

BikeParq quiere cobrar un **recargo nocturno**. Desarrollen con TDD una función nueva que recibe la tarifa ya calculada y la hora de entrada, y devuelve el total a pagar.

**Reglas del recargo**

| # | Regla |
|---|---|
| R1 | Si la hora de entrada es de **20 a 23** o de **0 a 5**, se suman **$2.000** a la tarifa. |
| R2 | Si la hora de entrada es de **6 a 19**, la tarifa no cambia. |
| R3 | Si la tarifa es **0** (estuvo en tiempo de gracia), no hay recargo aunque sea de noche. |
| R4 | Si la hora está fuera de **0 a 23**, se lanza un error. |

**Ejemplos para sus pruebas**

| Tarifa | Hora de entrada | Resultado |
|---|---|---|
| 3000 | 22 | 5000 |
| 3000 | 10 | 3000 |
| 0 | 23 | 0 |
| 3000 | 20 | 5000 (límite: empieza la noche) |
| 3000 | 19 | 3000 (límite) |
| 3000 | 5 | 5000 (límite) |
| 3000 | 6 | 3000 (límite: empieza el día) |
| 3000 | 24 | Error |

**Nombres que deben usar**

| Lenguaje | Código | Prueba | Firma |
|---|---|---|---|
| Python | `src/bikeparq/recargo.py` | `tests/test_recargo.py` | `aplicar_recargo_nocturno(tarifa: int, hora_entrada: int) -> int` — error: `ValueError` |
| Java | `src/main/java/bikeparq/Recargo.java` | `src/test/java/bikeparq/RecargoTest.java` | `public static int aplicarRecargoNocturno(int tarifa, int horaEntrada)` — error: `IllegalArgumentException` |
| PHP | `src/Recargo.php` | `tests/RecargoTest.php` | `public static function aplicarRecargoNocturno(int $tarifa, int $horaEntrada): int` — error: `InvalidArgumentException` |

**Cómo se trabaja (obligatorio)**

1. **Rojo:** escriban una prueba que falle (la función todavía no existe o no cumple la regla). Commit con mensaje que empiece por `rojo:`.
2. **Verde:** escriban el código mínimo para que la prueba pase. Commit que empiece por `verde:`.
3. **Refactor:** mejoren el código sin cambiar su comportamiento (constantes con nombre en lugar de números sueltos, una función auxiliar como `es_horario_nocturno`, nombres claros). Las pruebas siguen pasando. Commit que empiece por `refactor:`.

Repitan el ciclo regla por regla. Al final deben tener **al menos 4 pruebas** (una por regla) y en el historial deben verse los commits `rojo:` → `verde:` → `refactor:` **en ese orden**, al menos un ciclo completo.

Ejemplo de historial esperado:

```text
refactor: constantes para el recargo y las horas de la noche
verde: no se cobra recargo en tiempo de gracia
rojo: tarifa 0 de noche no tiene recargo
verde: hora fuera de rango lanza error
rojo: hora 24 debe lanzar error
verde: recargo de 2000 en horario nocturno
rojo: tarifa 3000 a las 22 debe dar 5000
```

> Es normal que el pipeline salga en rojo en el commit `rojo:`. Lo que se califica es que el **último** commit esté en verde.

---

## Punto 3 — BDD en español (20 %)

Este punto usa la función del recargo nocturno del Punto 2, así que háganlo después de ese punto.

**1. Escriban los escenarios.** En `cobro.feature` (Python: `tests/features/`, Java: `src/test/resources/features/`, PHP: `features/`), debajo del comentario `# PUNTO 3`, escriban **3 escenarios**, cada uno con su propio `Escenario:` (sin tablas):

| Escenario | Qué debe describir |
|---|---|
| 1 | Un vehículo que entró de noche paga el recargo de $2.000 |
| 2 | Un vehículo que entró de día no paga recargo |
| 3 | Con tarifa 0 (tiempo de gracia) no hay recargo, aunque haya entrado de noche |

Cada escenario lleva un título que diga qué comportamiento prueba y usa estas tres frases. Cambien `<...>` por un número que ustedes elijan (sin los signos `<` `>`) y calculen el total esperado con las reglas del Punto 2:

```text
Dado una tarifa de <tarifa> pesos
Cuando el vehículo entró a las <hora> horas
Entonces el total a pagar es de <total> pesos
```

**2. Programen los 3 pasos nuevos.** Estas frases todavía no existen en el proyecto, así que al principio los escenarios fallan por "paso sin definir". Agréguenlos en el archivo de pasos, debajo del comentario `PUNTO 3`. El paso `Dado` guarda la tarifa, el paso `Cuando` llama a la función del recargo y el paso `Entonces` verifica el total. Copien la forma de los pasos que ya están en el archivo y **no cambien esos pasos**.

| Lenguaje | Archivo de pasos | Cómo se declara un paso |
|---|---|---|
| Python | `tests/test_cobro_bdd.py` | `@given(parsers.parse("una tarifa de {tarifa:d} pesos"), target_fixture="cuenta")` |
| Java | `src/test/java/bikeparq/PasosCobro.java` | `@Given("una tarifa de {int} pesos")` |
| PHP | `features/bootstrap/FeatureContext.php` | `#[Given('una tarifa de :tarifa pesos')]` (el número llega como texto: conviértanlo con `(int)`) |

Para ejecutar solo el BDD en su máquina: Python `uv run pytest tests/test_cobro_bdd.py -v`, Java `mvn test -Dtest=RunCucumberTest`, PHP `vendor/bin/behat --strict`.

Commit sugerido: `bdd: escenarios y pasos del recargo nocturno`.

---

## Punto 4 — Análisis estático y Clean Code (20 %)

El archivo de reportes (`reportes.py` / `Reportes.java` / `Reportes.php`) funciona y sus pruebas pasan, pero está escrito con descuido. Por eso el job `analisis-estatico` falla.

1. Ejecuten el analizador en su máquina y lean los errores.
2. Corrijan el código hasta que el analizador no reporte **ningún** error.
3. Además del analizador, apliquen Clean Code: nombres que digan qué guardan, sin código repetido, sin importaciones que no se usan, llaves en todos los `if` y `for`, comparaciones estrictas.
4. **No modifiquen las pruebas de reportes.** Deben seguir pasando sin cambios. Tampoco cambien el nombre de las funciones públicas (`resumen_del_dia` / `resumenDelDia` y `mayor_cobro` / `mayorCobro`).

| Lenguaje | Comandos | Ayuda automática |
|---|---|---|
| Python | `uv run ruff check .` y `uv run ruff format --check .` | `uv run ruff format .` arregla el formato |
| Java | `mvn checkstyle:check` | — |
| PHP | `vendor/bin/phpstan analyse` y `vendor/bin/phpcs` | `vendor/bin/phpcbf` arregla parte del estilo |

Commit sugerido: `refactor: reportes sin errores de análisis estático`.

---

## Punto 5 — Cobertura y pipeline en verde (15 %)

1. La cobertura de líneas debe ser de **mínimo 90 %** (Python y Java) o **95 %** (PHP). Si no llegan, agreguen pruebas (por ejemplo, minutos fuera de rango).
2. En el **último commit**, los 5 jobs del pipeline deben estar en verde: `analisis-estatico`, `pruebas-unitarias`, `pruebas-bdd`, `cobertura` y `despliegue`.
3. Peguen la URL del repositorio en la tarea de Moodle.

| Lenguaje | Ver la cobertura en su máquina |
|---|---|
| Python | `uv run pytest --cov --cov-report=term-missing` |
| Java | `mvn verify` y abran `target/site/jacoco/index.html` |
| PHP | Necesita PCOV o Xdebug: `XDEBUG_MODE=coverage vendor/bin/phpunit --coverage-text`. Si no lo tienen instalado, revisen el job `cobertura` en GitHub Actions |

---

## Rúbrica

| Punto | Criterio | Valor |
|---|---|---|
| 1 | Las 3 pruebas existen, pasan y cada una verifica el resultado del caso (0,2 c/u) | 0,6 |
| 1 | Nombres descriptivos y estructura preparar–ejecutar–verificar | 0,2 |
| 1 | CP-03 verifica el tipo de error, no solo que "algo falle" | 0,2 |
| 2 | Historial con commits `rojo:` → `verde:` → `refactor:` en orden | 0,4 |
| 2 | Al menos 4 pruebas que cubren R1 a R4, incluidos los límites 5/6 o 19/20 | 0,45 |
| 2 | La función cumple las 4 reglas | 0,25 |
| 2 | Refactor real: constantes con nombre o función auxiliar, sin cambiar el comportamiento | 0,15 |
| 3 | Los 3 escenarios en español, bien escritos y con títulos que describen el comportamiento (noche, día y tarifa 0) | 0,3 |
| 3 | Los 3 pasos nuevos están programados, llaman a la función del recargo y verifican el total; los pasos que ya existían no se cambiaron | 0,5 |
| 3 | Valores esperados correctos y los escenarios pasan | 0,2 |
| 4 | El analizador estático no reporta errores | 0,4 |
| 4 | Pruebas de reportes sin modificar y pasando | 0,2 |
| 4 | Clean Code: nombres, sin duplicación, sin importaciones sin uso, llaves, comparaciones estrictas | 0,4 |
| 5 | Cobertura mínima alcanzada (90 % o 95 % en PHP) | 0,35 |
| 5 | Los 5 jobs en verde en el último commit | 0,3 |
| 5 | URL entregada en Moodle, repositorio público y con el nombre pedido | 0,1 |
| | **Total** | **5,0** |

## Lista de chequeo antes de entregar

- [ ] El repositorio es público y se llama `parcial2-bikeparq-<primer-apellido>`.
- [ ] Hay commits `rojo:`, `verde:` y `refactor:` en orden.
- [ ] No modifiqué el pipeline, ni las pruebas de reportes, ni los pasos del BDD que ya existían.
- [ ] En **Actions**, el último commit tiene los 5 jobs en verde.
- [ ] Pegué la URL del repositorio en Moodle.

---

# Anexos: proyectos base

Cada anexo tiene la estructura de carpetas, los requisitos, los comandos y el contenido de cada archivo. Los mismos archivos están en los `.zip` de Moodle.

## Anexo A — Python (uv, pytest, pytest-bdd, Ruff, pytest-cov)

**Requisitos:** Python 3.12 o superior y [uv](https://docs.astral.sh/uv/).

```text
bikeparq-python/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── .gitignore
├── .python-version
├── pyproject.toml
├── src/
│   └── bikeparq/
│       ├── __init__.py
│       ├── reportes.py        ← Punto 4
│       └── tarifa.py
│       (recargo.py lo crean en el Punto 2)
└── tests/
    ├── features/
    │   └── cobro.feature      ← Punto 3
    ├── test_cobro_bdd.py      ← Punto 3 (pasos nuevos)
    ├── test_reportes.py       (no modificar)
    └── test_tarifa.py         ← Punto 1
    (test_recargo.py lo crean en el Punto 2)
```

**Comandos**

```bash
uv sync                                          # instala dependencias
uv run pytest                                    # todas las pruebas
uv run pytest tests/test_cobro_bdd.py -v         # solo BDD
uv run ruff check .                              # análisis estático
uv run ruff format .                             # da formato al código
uv run pytest --cov --cov-report=term-missing    # cobertura
```

**`.python-version`**

```text
3.13
```

**`pyproject.toml`**

```toml
[project]
name = "bikeparq"
version = "0.1.0"
description = "Cobro del parqueadero BikeParq - Segundo parcial de Pruebas de Software"
requires-python = ">=3.12"
dependencies = []

[dependency-groups]
dev = [
    "pytest>=8.4",
    "pytest-bdd>=8.1",
    "pytest-cov>=7.0",
    "ruff>=0.14",
]

[tool.uv]
package = false

[tool.pytest.ini_options]
pythonpath = ["src"]
testpaths = ["tests"]
bdd_features_base_dir = "tests/features"

[tool.coverage.run]
source = ["src"]

[tool.ruff]
line-length = 100
target-version = "py312"

[tool.ruff.lint]
select = ["E", "W", "F", "N", "B", "SIM", "UP", "C90", "PLR5501"]

[tool.ruff.lint.mccabe]
max-complexity = 5
```

**`.gitignore`**

```text
.venv/
__pycache__/
.pytest_cache/
.ruff_cache/
.coverage
htmlcov/
```

**`.github/workflows/ci-cd.yml`**

```yaml
name: CI/CD BikeParq

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  analisis-estatico:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: astral-sh/setup-uv@v10.3.3
      - run: uv sync
      - run: uv run ruff check .
      - run: uv run ruff format --check .

  pruebas-unitarias:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: astral-sh/setup-uv@v10.3.3
      - run: uv sync
      - run: uv run pytest --ignore=tests/test_cobro_bdd.py -v

  pruebas-bdd:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: astral-sh/setup-uv@v10.3.3
      - run: uv sync
      - run: uv run pytest tests/test_cobro_bdd.py -v

  cobertura:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: astral-sh/setup-uv@v10.3.3
      - run: uv sync
      - run: uv run pytest --cov --cov-report=term-missing --cov-fail-under=90

  despliegue:
    runs-on: ubuntu-latest
    needs: [analisis-estatico, pruebas-unitarias, pruebas-bdd, cobertura]
    steps:
      - run: echo "Desplegado"
```

**`src/bikeparq/__init__.py`** — archivo vacío (créelo sin contenido).

**`src/bikeparq/tarifa.py`**

```python
PRECIO_POR_MINUTO = {"carro": 60, "moto": 30}
TOPE_DIARIO = {"carro": 25_000, "moto": 12_000}
MINUTOS_DE_GRACIA = 10
MINUTOS_MAXIMOS = 1440


def calcular_tarifa(minutos: int, tipo: str) -> int:
    """Devuelve lo que se cobra por `minutos` de permanencia de un vehículo `tipo`."""
    if tipo not in PRECIO_POR_MINUTO:
        raise ValueError(f"Tipo de vehículo no válido: {tipo}")
    if minutos < 0 or minutos > MINUTOS_MAXIMOS:
        raise ValueError("Los minutos deben estar entre 0 y 1440")
    if minutos <= MINUTOS_DE_GRACIA:
        return 0
    valor = (minutos - MINUTOS_DE_GRACIA) * PRECIO_POR_MINUTO[tipo]
    return min(valor, TOPE_DIARIO[tipo])
```

**`src/bikeparq/reportes.py`**

```python
import os, sys
from math import *


def resumen_del_dia(l):
    Total=0
    c=0; m=0
    for i in range(len(l)):
        r = l[i]
        if r["tipo"]=="carro":
            c=c+1
            Total=Total+r["valor"]
        else:
            if r["tipo"]=="moto":
                m=m+1
                Total=Total+r["valor"]
    return {"total":Total,"carros":c,"motos":m}


def mayor_cobro(l):
    x = None
    for r in l:
        if x == None or r["valor"] > x:
            x = r["valor"]
    if x == None:
        return 0
    return x
```

**`tests/test_tarifa.py`**

```python
# PUNTO 1: escriba aquí las pruebas CP-01, CP-02 y CP-03 del enunciado.
```

**`tests/test_reportes.py`**

```python
# NO MODIFIQUE ESTE ARCHIVO. Estas pruebas deben seguir pasando después del Punto 4.
from bikeparq.reportes import mayor_cobro, resumen_del_dia

REGISTROS = [
    {"tipo": "carro", "valor": 3000},
    {"tipo": "moto", "valor": 1500},
    {"tipo": "carro", "valor": 600},
    {"tipo": "bicicleta", "valor": 999},
]


def test_resumen_suma_solo_carros_y_motos():
    assert resumen_del_dia(REGISTROS) == {"total": 5100, "carros": 2, "motos": 1}


def test_resumen_de_un_dia_sin_registros():
    assert resumen_del_dia([]) == {"total": 0, "carros": 0, "motos": 0}


def test_mayor_cobro_del_dia():
    assert mayor_cobro(REGISTROS) == 3000


def test_mayor_cobro_sin_registros_es_cero():
    assert mayor_cobro([]) == 0
```

**`tests/test_cobro_bdd.py`**

```python
# Pasos del BDD. Los pasos que ya están aquí funcionan: no los cambie.
from pytest_bdd import given, parsers, scenarios, then, when

from bikeparq.tarifa import calcular_tarifa

scenarios("cobro.feature")


@given(parsers.parse('un vehículo tipo "{tipo}"'), target_fixture="vehiculo")
def vehiculo(tipo):
    return {"tipo": tipo}


@when(parsers.parse("permanece {minutos:d} minutos"))
def permanece(vehiculo, minutos):
    vehiculo["cobro"] = calcular_tarifa(minutos, vehiculo["tipo"])


@then(parsers.parse("el cobro es de {cobro:d} pesos"))
def cobro_esperado(vehiculo, cobro):
    assert vehiculo["cobro"] == cobro


# PUNTO 3: agregue aquí los pasos de sus escenarios del recargo nocturno.
```

**`tests/features/cobro.feature`**

```gherkin
# language: es
Característica: Cobro por permanencia en el parqueadero BikeParq
  Como cajero del parqueadero
  quiero que el sistema calcule el cobro
  para no hacer cuentas a mano

  Escenario: Un carro permanece una hora
    Dado un vehículo tipo "carro"
    Cuando permanece 60 minutos
    Entonces el cobro es de 3000 pesos

  # PUNTO 3: escriba aquí sus 3 escenarios del recargo nocturno.
```

---

## Anexo B — Java (Maven, JUnit 6, Cucumber 8, Checkstyle, JaCoCo)

**Requisitos:** JDK 21 y Maven 3.9 o superior.

```text
bikeparq-java/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── .gitignore
├── checkstyle.xml
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── bikeparq/
    │           ├── Registro.java
    │           ├── Reportes.java       ← Punto 4
    │           ├── Resumen.java
    │           └── Tarifa.java
    │           (Recargo.java lo crean en el Punto 2)
    └── test/
        ├── java/
        │   └── bikeparq/
        │       ├── PasosCobro.java     ← Punto 3 (pasos nuevos)
        │       ├── ReportesTest.java   (no modificar)
        │       ├── RunCucumberTest.java
        │       └── TarifaTest.java     ← Punto 1
        │       (RecargoTest.java lo crean en el Punto 2)
        └── resources/
            ├── features/
            │   └── cobro.feature       ← Punto 3
            └── junit-platform.properties
```

**Comandos**

```bash
mvn test                                          # todas las pruebas
mvn test -Dtest='*Test,!RunCucumberTest'          # solo unitarias
mvn test -Dtest=RunCucumberTest                   # solo BDD
mvn checkstyle:check                              # análisis estático
mvn verify                                        # pruebas + cobertura (target/site/jacoco/index.html)
```

En PowerShell, escriban las comillas así: `mvn test "-Dtest=*Test,!RunCucumberTest"`.

**`pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>co.edu.cue</groupId>
    <artifactId>bikeparq</artifactId>
    <version>0.1.0</version>
    <name>BikeParq</name>
    <description>Cobro del parqueadero BikeParq - Segundo parcial de Pruebas de Software</description>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.junit</groupId>
                <artifactId>junit-bom</artifactId>
                <version>6.1.3</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-bom</artifactId>
                <version>8.0.4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-suite</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.15.0</version>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.6</version>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>3.6.0</version>
                <configuration>
                    <configLocation>checkstyle.xml</configLocation>
                    <consoleOutput>true</consoleOutput>
                    <failOnViolation>true</failOnViolation>
                    <violationSeverity>warning</violationSeverity>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.15</version>
                <executions>
                    <execution>
                        <id>preparar-agente</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>reporte</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>verificar-cobertura</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.90</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

**`checkstyle.xml`**

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="error"/>

    <module name="LineLength">
        <property name="max" value="100"/>
    </module>

    <module name="TreeWalker">
        <module name="AvoidStarImport"/>
        <module name="UnusedImports"/>
        <module name="TypeName"/>
        <module name="MethodName"/>
        <module name="ConstantName"/>
        <module name="MemberName"/>
        <module name="ParameterName">
            <property name="format" value="^[a-z][a-zA-Z0-9]{2,}$"/>
        </module>
        <module name="LocalVariableName">
            <property name="format" value="^[a-z][a-zA-Z0-9]{2,}$"/>
            <property name="allowOneCharVarInForLoop" value="true"/>
        </module>
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
        <module name="OneStatementPerLine"/>
        <module name="MultipleVariableDeclarations"/>
        <module name="StringLiteralEquality"/>
        <module name="EmptyStatement"/>
    </module>
</module>
```

**`.gitignore`**

```text
target/
.idea/
*.iml
.vscode/
```

**`.github/workflows/ci-cd.yml`**

```yaml
name: CI/CD BikeParq

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  analisis-estatico:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B checkstyle:check

  pruebas-unitarias:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B test -Dtest='*Test,!RunCucumberTest'

  pruebas-bdd:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B test -Dtest=RunCucumberTest

  cobertura:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B verify

  despliegue:
    runs-on: ubuntu-latest
    needs: [analisis-estatico, pruebas-unitarias, pruebas-bdd, cobertura]
    steps:
      - run: echo "Desplegado"
```

**`src/main/java/bikeparq/Tarifa.java`**

```java
package bikeparq;

import java.util.Map;

public final class Tarifa {

    private static final Map<String, Integer> PRECIO_POR_MINUTO = Map.of("carro", 60, "moto", 30);
    private static final Map<String, Integer> TOPE_DIARIO = Map.of("carro", 25_000, "moto", 12_000);
    private static final int MINUTOS_DE_GRACIA = 10;
    private static final int MINUTOS_MAXIMOS = 1440;

    private Tarifa() {
    }

    public static int calcularTarifa(int minutos, String tipo) {
        if (!PRECIO_POR_MINUTO.containsKey(tipo)) {
            throw new IllegalArgumentException("Tipo de vehículo no válido: " + tipo);
        }
        if (minutos < 0 || minutos > MINUTOS_MAXIMOS) {
            throw new IllegalArgumentException("Los minutos deben estar entre 0 y 1440");
        }
        if (minutos <= MINUTOS_DE_GRACIA) {
            return 0;
        }
        int valor = (minutos - MINUTOS_DE_GRACIA) * PRECIO_POR_MINUTO.get(tipo);
        return Math.min(valor, TOPE_DIARIO.get(tipo));
    }
}
```

**`src/main/java/bikeparq/Registro.java`**

```java
package bikeparq;

/** Un cobro registrado en caja: tipo de vehículo y valor cobrado. */
public record Registro(String tipo, int valor) {
}
```

**`src/main/java/bikeparq/Resumen.java`**

```java
package bikeparq;

/** Resumen de caja del día. */
public record Resumen(int total, int carros, int motos) {
}
```

**`src/main/java/bikeparq/Reportes.java`**

```java
package bikeparq;

import java.util.*;
import java.util.stream.Collectors;

public class Reportes {

    public static Resumen resumenDelDia(List<Registro> l) {
        int Total = 0, c = 0, m = 0;
        for (int i = 0; i < l.size(); i++) {
            Registro r = l.get(i);
            if (r.tipo() == "carro") {
                c++; Total += r.valor();
            }
            else if (r.tipo() == "moto") {
                m++; Total += r.valor();
            }
        }
        return new Resumen(Total, c, m);
    }

    public static int mayorCobro(List<Registro> l) {
        int x = 0;
        for (Registro r : l) if (r.valor() > x) x = r.valor();
        return x;
    }
}
```

**`src/test/java/bikeparq/TarifaTest.java`**

```java
package bikeparq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TarifaTest {

    // PUNTO 1: escriba aquí las pruebas CP-01, CP-02 y CP-03 del enunciado.
}
```

**`src/test/java/bikeparq/ReportesTest.java`**

```java
package bikeparq;

// NO MODIFIQUE ESTE ARCHIVO. Estas pruebas deben seguir pasando después del Punto 4.

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReportesTest {

    private static final List<Registro> REGISTROS = List.of(
            new Registro("carro", 3000),
            new Registro("moto", 1500),
            new Registro("carro", 600),
            new Registro("bicicleta", 999));

    @Test
    void resumenSumaSoloCarrosYMotos() {
        assertEquals(new Resumen(5100, 2, 1), Reportes.resumenDelDia(REGISTROS));
    }

    @Test
    void resumenDeUnDiaSinRegistros() {
        assertEquals(new Resumen(0, 0, 0), Reportes.resumenDelDia(List.of()));
    }

    @Test
    void mayorCobroDelDia() {
        assertEquals(3000, Reportes.mayorCobro(REGISTROS));
    }

    @Test
    void mayorCobroSinRegistrosEsCero() {
        assertEquals(0, Reportes.mayorCobro(List.of()));
    }
}
```

**`src/test/java/bikeparq/PasosCobro.java`**

```java
package bikeparq;

// Pasos del BDD. Los pasos que ya están aquí funcionan: no los cambie.

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PasosCobro {

    private String tipo;
    private int cobro;

    @Given("un vehículo tipo {string}")
    public void unVehiculoTipo(String tipo) {
        this.tipo = tipo;
    }

    @When("permanece {int} minutos")
    public void permanece(int minutos) {
        cobro = Tarifa.calcularTarifa(minutos, tipo);
    }

    @Then("el cobro es de {int} pesos")
    public void elCobroEsDe(int esperado) {
        assertEquals(esperado, cobro);
    }

    // PUNTO 3: agregue aquí los pasos de sus escenarios del recargo nocturno.
}
```

**`src/test/java/bikeparq/RunCucumberTest.java`**

```java
package bikeparq;

// Ejecuta los archivos .feature de src/test/resources/features. No necesita modificarlo.

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "bikeparq")
public class RunCucumberTest {
}
```

**`src/test/resources/junit-platform.properties`**

```properties
cucumber.junit-platform.discovery.as-root-engine=false
cucumber.publish.quiet=true
```

**`src/test/resources/features/cobro.feature`**

```gherkin
# language: es
Característica: Cobro por permanencia en el parqueadero BikeParq
  Como cajero del parqueadero
  quiero que el sistema calcule el cobro
  para no hacer cuentas a mano

  Escenario: Un carro permanece una hora
    Dado un vehículo tipo "carro"
    Cuando permanece 60 minutos
    Entonces el cobro es de 3000 pesos

  # PUNTO 3: escriba aquí sus 3 escenarios del recargo nocturno.
```

---

## Anexo C — PHP (Composer, PHPUnit 12, Behat, PHPStan, PHP_CodeSniffer)

**Requisitos:** PHP 8.3 o superior con las extensiones `dom`, `mbstring` y `xml`, y Composer. Para ver la cobertura en su máquina necesitan PCOV o Xdebug (opcional; el pipeline ya la mide).

```text
bikeparq-php/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── .gitignore
├── bin/
│   └── verificar-cobertura.php
├── composer.json
├── features/
│   ├── bootstrap/
│   │   └── FeatureContext.php    ← Punto 3 (pasos nuevos)
│   └── cobro.feature             ← Punto 3
├── phpcs.xml
├── phpstan.neon
├── phpunit.xml
├── src/
│   ├── Reportes.php              ← Punto 4
│   └── Tarifa.php
│   (Recargo.php lo crean en el Punto 2)
└── tests/
    └── ReportesTest.php          (no modificar)
    (TarifaTest.php lo crean en el Punto 1 y RecargoTest.php en el Punto 2)
```

**Comandos**

```bash
composer install                     # instala dependencias (suban también composer.lock)
vendor/bin/phpunit                   # pruebas unitarias
vendor/bin/behat --strict            # BDD
vendor/bin/phpstan analyse           # análisis estático de tipos
vendor/bin/phpcs                     # estilo PSR-12 y nombres
vendor/bin/phpcbf                    # corrige parte del estilo automáticamente
```

En Windows usen `vendor\bin\phpunit` (con barra invertida) si la otra forma no funciona.

**`composer.json`**

```json
{
    "name": "cue/bikeparq",
    "description": "Cobro del parqueadero BikeParq - Segundo parcial de Pruebas de Software",
    "type": "project",
    "license": "MIT",
    "require": {
        "php": ">=8.3"
    },
    "require-dev": {
        "behat/behat": "^3.34",
        "phpstan/phpstan": "^2.1",
        "phpunit/phpunit": "^12.5",
        "squizlabs/php_codesniffer": "^4.0"
    },
    "autoload": {
        "psr-4": {
            "BikeParq\\": "src/"
        }
    },
    "autoload-dev": {
        "psr-4": {
            "BikeParq\\Tests\\": "tests/"
        }
    },
    "config": {
        "sort-packages": true
    }
}
```

**`phpunit.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<phpunit xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="vendor/phpunit/phpunit/phpunit.xsd"
         bootstrap="vendor/autoload.php"
         colors="true"
         cacheDirectory=".phpunit.cache">
    <testsuites>
        <testsuite name="unitarias">
            <directory>tests</directory>
        </testsuite>
    </testsuites>
    <source>
        <include>
            <directory>src</directory>
        </include>
    </source>
</phpunit>
```

**`phpstan.neon`**

```yaml
parameters:
    level: 6
    paths:
        - src
    reportUnmatchedIgnoredErrors: false
    ignoreErrors:
        - identifier: missingType.iterableValue
```

**`phpcs.xml`**

```xml
<?xml version="1.0"?>
<ruleset name="BikeParq">
    <description>Estilo PSR-12 y nombres de variables en camelCase</description>
    <file>src</file>
    <arg name="colors"/>
    <arg value="sp"/>
    <rule ref="PSR12"/>
    <rule ref="Squiz.NamingConventions.ValidVariableName.NotCamelCaps"/>
</ruleset>
```

**`.gitignore`**

```text
/vendor/
/.phpunit.cache/
coverage.xml
```

**`.github/workflows/ci-cd.yml`**

```yaml
name: CI/CD BikeParq

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  analisis-estatico:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: shivammathur/setup-php@v2
        with:
          php-version: '8.4'
          coverage: none
      - run: composer install --no-interaction --no-progress
      - run: vendor/bin/phpstan analyse --no-progress
      - run: vendor/bin/phpcs

  pruebas-unitarias:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: shivammathur/setup-php@v2
        with:
          php-version: '8.4'
          coverage: none
      - run: composer install --no-interaction --no-progress
      - run: vendor/bin/phpunit

  pruebas-bdd:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: shivammathur/setup-php@v2
        with:
          php-version: '8.4'
          coverage: none
      - run: composer install --no-interaction --no-progress
      - run: vendor/bin/behat --strict

  cobertura:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - uses: shivammathur/setup-php@v2
        with:
          php-version: '8.4'
          coverage: pcov
      - run: composer install --no-interaction --no-progress
      - run: vendor/bin/phpunit --coverage-text --coverage-clover coverage.xml
      - run: php bin/verificar-cobertura.php coverage.xml 95

  despliegue:
    runs-on: ubuntu-latest
    needs: [analisis-estatico, pruebas-unitarias, pruebas-bdd, cobertura]
    steps:
      - run: echo "Desplegado"
```

**`bin/verificar-cobertura.php`**

```php
<?php

declare(strict_types=1);

// Uso: php bin/verificar-cobertura.php coverage.xml 95
$archivo = $argv[1] ?? 'coverage.xml';
$minimo = (float) ($argv[2] ?? 95);

if (!is_file($archivo)) {
    fwrite(STDERR, "No se encontró {$archivo}. ¿Se ejecutó PHPUnit con --coverage-clover?\n");
    exit(1);
}

$xml = simplexml_load_file($archivo);
$metricas = $xml->project->metrics;
$lineas = (int) $metricas['statements'];
$cubiertas = (int) $metricas['coveredstatements'];
$porcentaje = $lineas === 0 ? 0.0 : $cubiertas * 100 / $lineas;

printf("Cobertura de líneas: %.2f %% (%d de %d). Mínimo exigido: %.0f %%\n", $porcentaje, $cubiertas, $lineas, $minimo);
exit($porcentaje >= $minimo ? 0 : 1);
```

**`src/Tarifa.php`**

```php
<?php

declare(strict_types=1);

namespace BikeParq;

use InvalidArgumentException;

final class Tarifa
{
    private const PRECIO_POR_MINUTO = ['carro' => 60, 'moto' => 30];
    private const TOPE_DIARIO = ['carro' => 25000, 'moto' => 12000];
    private const MINUTOS_DE_GRACIA = 10;
    private const MINUTOS_MAXIMOS = 1440;

    public static function calcularTarifa(int $minutos, string $tipo): int
    {
        if (!array_key_exists($tipo, self::PRECIO_POR_MINUTO)) {
            throw new InvalidArgumentException("Tipo de vehículo no válido: {$tipo}");
        }
        if ($minutos < 0 || $minutos > self::MINUTOS_MAXIMOS) {
            throw new InvalidArgumentException('Los minutos deben estar entre 0 y 1440');
        }
        if ($minutos <= self::MINUTOS_DE_GRACIA) {
            return 0;
        }
        $valor = ($minutos - self::MINUTOS_DE_GRACIA) * self::PRECIO_POR_MINUTO[$tipo];

        return min($valor, self::TOPE_DIARIO[$tipo]);
    }
}
```

**`src/Reportes.php`**

```php
<?php

namespace BikeParq;

class Reportes
{
    public static function resumenDelDia($l)
    {
        $Total = 0; $c = 0; $m = 0;
        for ($i = 0; $i < count($l); $i++) {
            $r = $l[$i];
            if ($r['tipo'] == 'carro') {
                $c = $c + 1;
                $Total = $Total + $r['valor'];
            } else {
                if ($r['tipo'] == 'moto') {
                    $m = $m + 1;
                    $Total = $Total + $r['valor'];
                }
            }
        }
        return ['total' => $Total, 'carros' => $c, 'motos' => $m];
    }

    public static function mayorCobro($l)
    {
        $x = null;
        foreach ($l as $r) {
            if ($x == null || $r['valor'] > $x) $x = $r['valor'];
        }
        if ($x == null) return 0;
        return $x;
    }
}
```

**`tests/ReportesTest.php`**

```php
<?php

// NO MODIFIQUE ESTE ARCHIVO. Estas pruebas deben seguir pasando después del Punto 4.

declare(strict_types=1);

namespace BikeParq\Tests;

use BikeParq\Reportes;
use PHPUnit\Framework\TestCase;

final class ReportesTest extends TestCase
{
    private const REGISTROS = [
        ['tipo' => 'carro', 'valor' => 3000],
        ['tipo' => 'moto', 'valor' => 1500],
        ['tipo' => 'carro', 'valor' => 600],
        ['tipo' => 'bicicleta', 'valor' => 999],
    ];

    public function testResumenSumaSoloCarrosYMotos(): void
    {
        $this->assertSame(
            ['total' => 5100, 'carros' => 2, 'motos' => 1],
            Reportes::resumenDelDia(self::REGISTROS)
        );
    }

    public function testResumenDeUnDiaSinRegistros(): void
    {
        $this->assertSame(['total' => 0, 'carros' => 0, 'motos' => 0], Reportes::resumenDelDia([]));
    }

    public function testMayorCobroDelDia(): void
    {
        $this->assertSame(3000, Reportes::mayorCobro(self::REGISTROS));
    }

    public function testMayorCobroSinRegistrosEsCero(): void
    {
        $this->assertSame(0, Reportes::mayorCobro([]));
    }
}
```

**`features/cobro.feature`**

```gherkin
# language: es
Característica: Cobro por permanencia en el parqueadero BikeParq
  Como cajero del parqueadero
  quiero que el sistema calcule el cobro
  para no hacer cuentas a mano

  Escenario: Un carro permanece una hora
    Dado un vehículo tipo "carro"
    Cuando permanece 60 minutos
    Entonces el cobro es de 3000 pesos

  # PUNTO 3: escriba aquí sus 3 escenarios del recargo nocturno.
```

**`features/bootstrap/FeatureContext.php`**

```php
<?php

// Pasos del BDD. Los pasos que ya están aquí funcionan: no los cambie.

declare(strict_types=1);

use Behat\Behat\Context\Context;
use Behat\Step\Given;
use Behat\Step\Then;
use Behat\Step\When;
use BikeParq\Tarifa;

final class FeatureContext implements Context
{
    private string $tipo = '';
    private int $cobro = 0;

    #[Given('un vehículo tipo :tipo')]
    public function unVehiculoTipo(string $tipo): void
    {
        $this->tipo = $tipo;
    }

    #[When('permanece :minutos minutos')]
    public function permanece(string $minutos): void
    {
        $this->cobro = Tarifa::calcularTarifa((int) $minutos, $this->tipo);
    }

    #[Then('el cobro es de :cobro pesos')]
    public function elCobroEsDe(string $cobro): void
    {
        if ($this->cobro !== (int) $cobro) {
            throw new RuntimeException("Se esperaba {$cobro} y el sistema cobró {$this->cobro}");
        }
    }

    // PUNTO 3: agregue aquí los pasos de sus escenarios del recargo nocturno.
}
```
