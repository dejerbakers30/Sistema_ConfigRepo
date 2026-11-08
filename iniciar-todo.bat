@echo off
chcp 65001 >nul
title Lanzador de Microservicios - Spring Cloud Config
color 0F

echo =====================================================================
echo       INICIADOR AUTOMATIZADO DE MICROSERVICIOS (SPRING CLOUD)
echo =====================================================================
echo.
echo Ubicacion: %~dp0
echo.

echo [1/4] Iniciando CONFIG-SERVER en el puerto 8888...
start "1. CONFIG-SERVER [Puerto 8888]" cmd /k "title CONFIG-SERVER (8888) && color 0A && cd /d "%~dp0config-server" && call mvnw.cmd spring-boot:run"

echo.
echo Esperando 10 segundos para que el Config Server levante y clone desde Git...
timeout /t 10 /nobreak >nul

echo.
echo [2/4] Iniciando MS-CATALOGO en el puerto 8081...
start "2. MS-CATALOGO [Puerto 8081]" cmd /k "title MS-CATALOGO (8081) && color 0E && cd /d "%~dp0ms-catalogo" && call mvnw.cmd spring-boot:run"

echo.
echo [3/4] Iniciando MS-PRECIOS en el puerto 8082...
start "3. MS-PRECIOS [Puerto 8082]" cmd /k "title MS-PRECIOS (8082) && color 0D && cd /d "%~dp0ms-precios" && call mvnw.cmd spring-boot:run"

echo.
echo [4/4] Iniciando MS-NOTIFICACIONES en el puerto 8083...
start "4. MS-NOTIFICACIONES [Puerto 8083]" cmd /k "title MS-NOTIFICACIONES (8083) && color 0B && cd /d "%~dp0ms-notificaciones" && call mvnw.cmd spring-boot:run"

echo.
echo =====================================================================
echo  ¡Los 4 microservicios han sido lanzados en ventanas independientes!
echo =====================================================================
echo.
echo  * Config Server:    http://localhost:8888/ms-catalogo/default
echo  * MS Catalogo:      http://localhost:8081/catalogo
echo  * MS Precios:       http://localhost:8082/precios
echo  * MS Notif:         http://localhost:8083/logs
echo.
echo Puedes comenzar a probar las peticiones en Postman cuando cada consola
echo indique: 'Started Application in X.XXX seconds'.
echo.
pause

