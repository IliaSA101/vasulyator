# Нарезка адаптивной иконки из design/icon-foreground.png (знак на прозрачном фоне).
#
# Исходник получен так:
#   rembg i -m birefnet-general candidates/icon-a2.png icon-foreground.png
#
# Запуск из корня репозитория:  pwsh design/make-icons.ps1
# Скрипт обрезает пустые поля, вписывает знак в безопасную зону адаптивной иконки
# (круг 66 dp из 108 dp) и пишет mipmap-*/ic_launcher_foreground.png + превью 512 px.

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$root = Split-Path $PSScriptRoot -Parent
$source = Join-Path $PSScriptRoot 'icon-foreground.png'
$res = Join-Path $root 'app/src/main/res'
$background = [Drawing.ColorTranslator]::FromHtml('#283593')
# Доля стороны холста 108 dp, которую занимает знак. 0.50 оставляет запас до границы безопасной зоны (0.61).
$glyphShare = 0.50

$src = [Drawing.Bitmap]::new($source)

# Границы непрозрачной части.
$minX = $src.Width; $minY = $src.Height; $maxX = 0; $maxY = 0
for ($y = 0; $y -lt $src.Height; $y += 2) {
    for ($x = 0; $x -lt $src.Width; $x += 2) {
        if ($src.GetPixel($x, $y).A -gt 16) {
            if ($x -lt $minX) { $minX = $x }; if ($x -gt $maxX) { $maxX = $x }
            if ($y -lt $minY) { $minY = $y }; if ($y -gt $maxY) { $maxY = $y }
        }
    }
}
$glyph = [Drawing.Rectangle]::FromLTRB($minX, $minY, $maxX + 1, $maxY + 1)

function New-Canvas([int]$size, [Nullable[Drawing.Color]]$fill) {
    $bmp = [Drawing.Bitmap]::new($size, $size, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.CompositingQuality = [Drawing.Drawing2D.CompositingQuality]::HighQuality
    if ($fill) { $g.Clear($fill) } else { $g.Clear([Drawing.Color]::Transparent) }
    $bmp, $g
}

function Draw-Glyph([Drawing.Graphics]$g, [int]$size) {
    $scale = $size * $glyphShare / [Math]::Max($glyph.Width, $glyph.Height)
    $w = $glyph.Width * $scale; $h = $glyph.Height * $scale
    $dest = [Drawing.RectangleF]::new(($size - $w) / 2, ($size - $h) / 2, $w, $h)
    $g.DrawImage($src, $dest, [Drawing.RectangleF]$glyph, [Drawing.GraphicsUnit]::Pixel)
}

$densities = [ordered]@{ mdpi = 108; hdpi = 162; xhdpi = 216; xxhdpi = 324; xxxhdpi = 432 }
foreach ($d in $densities.GetEnumerator()) {
    $dir = Join-Path $res "mipmap-$($d.Key)"
    New-Item -ItemType Directory -Force $dir | Out-Null
    $bmp, $g = New-Canvas $d.Value $null
    Draw-Glyph $g $d.Value
    $bmp.Save((Join-Path $dir 'ic_launcher_foreground.png'), [Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose(); $bmp.Dispose()
}

# Превью для README: та же геометрия, что у лаунчера, на фирменном фоне.
$bmp, $g = New-Canvas 512 $background
Draw-Glyph $g 512
$bmp.Save((Join-Path $PSScriptRoot 'icon-512.png'), [Drawing.Imaging.ImageFormat]::Png)
$g.Dispose(); $bmp.Dispose()

$src.Dispose()
"Glyph bounds in source: $glyph"
