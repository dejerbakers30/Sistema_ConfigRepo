# Sistema_ConfigRepo - Demo Spring Cloud Config Server

**Ciclo VIII - Arquitectura de Microservicios (Semana 11)**  
Implementación de un ecosistema distribuido con **Spring Cloud Config Server**, **Spring Boot 3.3.5** y **Java 17**, utilizando bases de datos en memoria **H2** y desacoplamiento total de configuraciones a través de Git.

---

## 🏗️ Arquitectura de la Solución

```
                                  ┌──────────────────────────────────────────────┐
                                  │      GitHub Repository (config-repo/)        │
                                  │  https://github.com/dejerbakers30/...        │
                                  └──────────────────────┬───────────────────────┘
                                                         │ (git pull / fetch)
                                                         ▼
                                          ┌─────────────────────────────┐
                                          │        config-server        │
                                          │         Puerto: 8888        │
                                          └──────┬────────┬─────────────┘
                                                 │        │
                   ┌─────────────────────────────┼────────┴──────────────────────────┐
                   ▼                             ▼                                   ▼
        ┌─────────────────────┐       ┌─────────────────────┐             ┌─────────────────────┐
        │     ms-catalogo     │       │     ms-precios      │             │  ms-notificaciones  │
        │    (Puerto 8081)    │       │    (Puerto 8082)    │             │    (Puerto 8083)    │
        │  H2: catalogodb     │       │  H2: preciosdb      │             │  H2: notificaciones │
        └─────────────────────┘       └─────────────────────┘             └─────────────────────┘
```

---

## 📂 Estructura de Directorios

```text
Sistema_ConfigRepo/
 ┣ 📂 config-repo/            # Repositorio Git central de propiedades (.yml)
 ┃ ┣ 📄 application.yml       # Configuración global compartida
 ┃ ┣ 📄 ms-catalogo.yml       # Puerto 8081, H2 catalogodb, items-por-pagina
 ┃ ┣ 📄 ms-precios.yml        # Puerto 8082, H2 preciosdb, impuesto (0.18)
 ┃ ┗ 📄 ms-notificaciones.yml # Puerto 8083, H2 notifdb, canal: consola, modo-simulado
 ┣ 📂 config-server/          # Spring Cloud Config Server (@EnableConfigServer)
 ┣ 📂 ms-catalogo/            # Microservicio de catálogo con CRUD de productos
 ┣ 📂 ms-precios/             # Microservicio de precios y cálculo dinámico de impuestos
 ┣ 📂 ms-notificaciones/      # Microservicio de registro y despacho simulado de alertas
 ┣ 📄 .gitignore              # Exclusión de carpetas target/, IDEs y temporales
 ┣ 📄 pruebas.http            # Archivo de pruebas HTTP listo para VS Code / IntelliJ
 ┗ 📄 README.md               # Documentación general del proyecto
```

---

## 📋 Reglas Arquitectónicas Cumplidas

1. **Java 17 y Spring Boot 3.3.5** con compatibilidad total para Spring Cloud **2023.0.3** (Train Leyton).
2. **H2 en memoria (`ddl-auto: create-drop`)**: Los microservicios no persisten datos en disco físico ni requieren motores de bases de datos externos. Al detener el servicio, la memoria se libera limpiamente.
3. **`application.yml` Local Mínimo**:
   ```yaml
   spring:
     application:
       name: <nombre-del-microservicio>
     profiles:
       active: default
     config:
       import: "optional:configserver:http://localhost:8888"
   ```
4. **Configuración 100% Externalizada en Git**: Los puertos, URLs de conexión H2, credenciales y variables de negocio se obtienen dinámicamente desde la carpeta `config-repo`.

---

## 🚀 Guía de Puesta en Marcha

### Paso 1: Subir cambios al repositorio de GitHub
```bash
git add .
git commit -m "feat: arquitectura completa de config server y microservicios"
git push -u origin main
```

### Paso 2: Iniciar los Servicios en Orden

1. **Config Server (Terminal 1):**
   ```bash
   cd config-server
   .\mvnw.cmd spring-boot:run
   ```
   *Verificar en el navegador:* `http://localhost:8888/ms-catalogo/default`

2. **Microservicio Catálogo (Terminal 2):**
   ```bash
   cd ms-catalogo
   .\mvnw.cmd spring-boot:run
   ```

3. **Microservicio Precios (Terminal 3):**
   ```bash
   cd ms-precios
   .\mvnw.cmd spring-boot:run
   ```

4. **Microservicio Notificaciones (Terminal 4):**
   ```bash
   cd ms-notificaciones
   .\mvnw.cmd spring-boot:run
   ```

---

## 🗄️ Acceso a la Consola Web de H2

Cada microservicio expone su consola H2 habilitada por Config Server:

| Servicio | Consola Web H2 | JDBC URL | User | Password |
| :--- | :--- | :--- | :---: | :---: |
| **ms-catalogo** | `http://localhost:8081/h2-console` | `jdbc:h2:mem:catalogodb` | `sa` | *(vacío)* |
| **ms-precios** | `http://localhost:8082/h2-console` | `jdbc:h2:mem:preciosdb` | `sa` | *(vacío)* |
| **ms-notificaciones** | `http://localhost:8083/h2-console` | `jdbc:h2:mem:notificacionesdb` | `sa` | *(vacío)* |

---

## 🧪 Pruebas Rápidas de Endpoints

Puedes abrir y ejecutar directamente el archivo [`pruebas.http`](pruebas.http) en VS Code (con la extensión *REST Client*) o usar los siguientes comandos `curl`:

```bash
# 1. Catálogo de productos:
curl http://localhost:8081/catalogo

# 2. Precio calculado con IGV 18% para Laptop PROD-001 (1200 -> 1416):
curl http://localhost:8082/precios/PROD-001

# 3. Enviar notificación simulada:
curl -X POST http://localhost:8083/notificacion \
  -H "Content-Type: application/json" \
  -d '{"destinatario": "alumno@unt.edu.pe", "mensaje": "Demo de Microservicios exitosa"}'

# 4. Ver logs de notificaciones:
curl http://localhost:8083/logs
```

