# Script robusto para iniciar el ecosistema Spring Cloud Config Server
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Iniciando ecosistema Spring Cloud Config Server..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
if (-not $baseDir) { $baseDir = Get-Location }

# 1. Iniciar Config Server
Write-Host "[1/4] Iniciando config-server en el puerto 8888..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$baseDir\config-server'; Write-Host '--- CONFIG SERVER (8888) ---' -ForegroundColor Green; .\mvnw.cmd spring-boot:run"

# 2. Esperar activamente a que Config Server responda en http://localhost:8888
Write-Host "Esperando a que Config Server termine de compilar y responda en http://localhost:8888..." -ForegroundColor Yellow

$ready = $false
$attempts = 0
while (-not $ready -and $attempts -lt 30) {
    Start-Sleep -Seconds 2
    $attempts++
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8888/ms-catalogo/default" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $ready = $true
        }
    } catch {
        Write-Host "." -NoNewline -ForegroundColor Yellow
    }
}

if ($ready) {
    Write-Host "`n¡Config Server LISTO y respondiendo configuracion desde Git!" -ForegroundColor Green
} else {
    Write-Host "`nContinuando con el inicio de los microservicios..." -ForegroundColor Yellow
}

# 3. Lanzar los 3 microservicios
Write-Host "[2/4] Iniciando ms-catalogo (Puerto 8081)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$baseDir\ms-catalogo'; Write-Host '--- MS CATALOGO (8081) ---' -ForegroundColor Yellow; .\mvnw.cmd spring-boot:run"

Start-Sleep -Seconds 2

Write-Host "[3/4] Iniciando ms-precios (Puerto 8082)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$baseDir\ms-precios'; Write-Host '--- MS PRECIOS (8082) ---' -ForegroundColor Magenta; .\mvnw.cmd spring-boot:run"

Start-Sleep -Seconds 2

Write-Host "[4/4] Iniciando ms-notificaciones (Puerto 8083)..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$baseDir\ms-notificaciones'; Write-Host '--- MS NOTIFICACIONES (8083) ---' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "¡Los 4 procesos han sido lanzados!" -ForegroundColor Green
Write-Host "Abre Postman y ejecuta las peticiones cuando las consolas esten listas." -ForegroundColor White
Write-Host "==========================================================" -ForegroundColor Cyan
