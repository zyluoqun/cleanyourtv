$ErrorActionPreference = 'Stop'
$OutputEncoding = New-Object System.Text.UTF8Encoding $false
$repo = 'C:\Users\Administrator\cleanyourtv'
$py = Join-Path $repo 'tools\venv\Scripts\python.exe'
$env:TV_HOST = '192.168.0.117'
$list = Join-Path $repo 'examples\skyworth-9r66-a7-cn\debloat.txt'

# Packages already recorded as before-state in the recovery record; hide them one by one.
$pkgs = Get-Content $list | ForEach-Object { ($_ -replace '#.*$','').Trim() } | Where-Object { $_ } | ForEach-Object { ($_ -split '\s+')[0] }
$pkgs = $pkgs | Where-Object { $_ -ne 'com.coocaa.activecenter' }  # already disabled
Write-Host "Will hide $($pkgs.Count) packages:"
$pkgs | ForEach-Object { Write-Host "  $_" }

# Build one batch: hide each, then report hidden state.
$cmds = @()
foreach ($p in $pkgs) { $cmds += "pm hide $p" }
$cmds += 'echo ---STATE---'
$cmds += 'dumpsys package com.tianci.ad | grep -m1 "User 0"'
$cmds += 'pm list packages | grep -c coocaa'
$cmds -join "`n" | & $py -I (Join-Path $repo 'tools\tv.py') --batch
