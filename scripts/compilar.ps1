$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$src = Join-Path $root "src\main\java"
$out = Join-Path $root "target\classes"
$lombok = Join-Path $root "lib\lombok-1.18.34.jar"

if (-not (Test-Path $lombok)) {
    New-Item -ItemType Directory -Force -Path (Join-Path $root "lib") | Out-Null
    Write-Host "Baixando Lombok..."
    Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/org/projectlombok/lombok/1.18.34/lombok-1.18.34.jar" -OutFile $lombok
}

New-Item -ItemType Directory -Force -Path $out | Out-Null
$files = Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object { $_.FullName }

$javac = if (Test-Path "C:\Program Files\Java\jdk-21\bin\javac.exe") {
    "C:\Program Files\Java\jdk-21\bin\javac.exe"
} else {
    "javac"
}

& $javac -encoding UTF-8 -cp $lombok -d $out "-processorpath" $lombok $files
Write-Host "Compilação concluída com sucesso."
