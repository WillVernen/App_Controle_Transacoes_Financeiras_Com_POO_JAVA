$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$images = Join-Path $root "images"
$out = Join-Path $root "target\classes"
$lombok = Join-Path $root "lib\lombok-1.18.34.jar"
$javaHome = "C:\Program Files\Java\jdk-21"
$javac = Join-Path $javaHome "bin\javac.exe"
$java = Join-Path $javaHome "bin\java.exe"

& (Join-Path $PSScriptRoot "compilar.ps1")
New-Item -ItemType Directory -Force -Path $images | Out-Null

function New-ConsolePng {
    param([string]$Text, [string]$OutputPath, [string]$Title)

    $font = New-Object System.Drawing.Font("Consolas", 12, [System.Drawing.FontStyle]::Regular)
    $titleFont = New-Object System.Drawing.Font("Segoe UI", 11, [System.Drawing.FontStyle]::Bold)
    $padding = 20
    $lineHeight = 20
    $lines = @(($Text -replace "`r", "").Split("`n") | Where-Object { $_ -ne $null })
    if ($lines.Count -eq 0) { $lines = @("(sem conteudo)") }
    $width = 1040
    $height = [Math]::Max(360, ($lines.Count * $lineHeight) + 70)

    $bmp = New-Object System.Drawing.Bitmap $width, $height
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.Clear([System.Drawing.Color]::FromArgb(18, 18, 18))
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

    $g.FillRectangle((New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(30, 30, 30))), 0, 0, $width, 34)
    $g.FillEllipse((New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(46, 160, 67))), 14, 11, 11, 11)
    $g.FillEllipse((New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(218, 165, 32))), 32, 11, 11, 11)
    $g.FillEllipse((New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(220, 70, 70))), 50, 11, 11, 11)

    $white = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(230, 230, 230))
    $green = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(120, 220, 140))
    $cyan = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(120, 200, 255))
    $g.DrawString($Title, $titleFont, $white, 74, 7)

    $y = 46
    foreach ($line in $lines) {
        $brush = $white
        if ($line -match "====|CONTROLE DE TRANSA") { $brush = $cyan }
        elseif ($line -match "sucesso|COMPROVANTE|realizada|Taxa de|Investimento criado|Dep[oó]sito realizado|TESTE_OK|Cheque especial") { $brush = $green }
        $safe = if ($line.Length -gt 120) { $line.Substring(0, 117) + "..." } else { $line }
        $g.DrawString($safe, $font, $brush, $padding, $y)
        $y += $lineHeight
    }

    $bmp.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose(); $bmp.Dispose(); $font.Dispose(); $titleFont.Dispose()
    Write-Host "Gerado: $OutputPath"
}

# Garante DemoFluxo compilado
$files = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -cp $lombok -d $out "-processorpath" $lombok $files

$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = $java
$psi.Arguments = "-Dfile.encoding=UTF-8 -cp `"$out;$lombok`" br.com.lab.banco.DemoFluxo"
$psi.RedirectStandardOutput = $true
$psi.RedirectStandardError = $true
$psi.UseShellExecute = $false
$psi.CreateNoWindow = $true
$psi.StandardOutputEncoding = [System.Text.UTF8Encoding]::new($false)
$psi.StandardErrorEncoding = [System.Text.UTF8Encoding]::new($false)

$proc = [System.Diagnostics.Process]::Start($psi)
$output = $proc.StandardOutput.ReadToEnd()
$err = $proc.StandardError.ReadToEnd()
$proc.WaitForExit()

$log = Join-Path $images "demo-output.txt"
[System.IO.File]::WriteAllText($log, $output, [System.Text.UTF8Encoding]::new($false))

if ($output -notmatch "TESTE_OK") {
    throw "Demo falhou. STDERR=$err`nOUTPUT=$output"
}
Write-Host "OK: fluxo de teste concluido com sucesso"

$titles = @{
    "01-menu-principal" = "01 - Menu Principal"
    "02-cadastro-cliente" = "02 - Cadastro de Cliente"
    "03-abertura-contas" = "03 - Abertura de Contas e PIX"
    "04-depositos" = "04 - Depósitos"
    "05-transferencia-pix" = "05 - Transferência PIX"
    "06-investimento" = "06 - Investimento"
    "07-historico-transacoes" = "07 - Histórico de Transações"
    "08-polimorfismo-contas" = "08 - Polimorfismo e Contas"
}

$parts = [regex]::Split($output, "@@@SCREEN:([a-z0-9\-]+)@@@")
# Split yields: preamble, id1, body1, id2, body2...
for ($i = 1; $i -lt $parts.Count - 1; $i += 2) {
    $id = $parts[$i]
    if ($id -eq "FIM") { continue }
    $body = $parts[$i + 1]
    $body = ($body -split "@@@SCREEN:")[0].Trim()
    if (-not $titles.ContainsKey($id)) { continue }
    $file = Join-Path $images ($id + ".png")
    New-ConsolePng -Title $titles[$id] -Text $body -OutputPath $file
}

$gitkeep = Join-Path $images ".gitkeep"
if (Test-Path $gitkeep) { Remove-Item $gitkeep -Force }

Get-ChildItem $images -Filter *.png | Select-Object Name, Length
Write-Host "Screenshots prontas em images/"
