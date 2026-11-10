$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$out = Join-Path $root "target\classes"
$lombok = Join-Path $root "lib\lombok-1.18.34.jar"

if (-not (Test-Path (Join-Path $out "br\com\lab\banco\Main.class"))) {
    & (Join-Path $PSScriptRoot "compilar.ps1")
}

$java = if (Test-Path "C:\Program Files\Java\jdk-21\bin\java.exe") {
    "C:\Program Files\Java\jdk-21\bin\java.exe"
} else {
    "java"
}

& $java -cp "$out;$lombok" br.com.lab.banco.Main
