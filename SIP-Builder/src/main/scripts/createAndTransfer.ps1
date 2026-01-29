param([Parameter(Mandatory = $true)] [String] $sipFolder,
  [Parameter(Mandatory = $true)] [String] $installPathWinSCP, 
  [Parameter(Mandatory = $true)] [String] $HostName, 
  [Parameter(Mandatory = $true)] [String] $User , 
  [Parameter(Mandatory = $true)] [String] $password)
 #  [Parameter(Mandatory = $true)] [String] $destResponse)

  Write-Host "sipFolder=$sipFolder"
  Write-Host "installPathWinSCP=$installPathWinSCP"
  Write-Host "HostName=$HostName"
  Write-Host "USer=$User"
  Write-Host "password=$password"
  Write-Host "destResponse=$destResponse"

#-- Needed Params
$installPath = "\target\scripts"
Write-Host "installPath=$installPath"

$remotePath = "/incoming/"
$responseRemotePath = "/outgoing/"
##$SshHostKeyFingerprint = "ssh-rsa 2048 ec:f6:6e:2a:3f:a9:b0:3b:03:2c:54:63:e7:5d:78:c3"
#TLS / FTPES
#$TlsHostKeyFingerprint = "63:6d:51:c1:b2:52:9a:a3:2e:66:d5:17:6a:cd:08:36:01:1c:1b:d0"
#$TlsClientCertificatePath ="C:\Users\z13wg06\Documents\publicKey.key"
$minFreeSpaceG = 60
	    
#Optional Params
#-----------------------------------------------------------------

#$destResponse = $installPath + "response"

$s="{0:yyyy-MM-dd HH:mm.ss}" -f (get-date)
Write-Host $s

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# WinSCP.com Pfad bestimmen: du übergibst offenbar den Installationspfad (Ordner)
$winScpCom = Join-Path $installPathWinSCP "WinSCPPortable.exe"
#"WinSCP.com"
Write-Host "winScpCom=$winScpCom"

if (!(Test-Path -LiteralPath $winScpCom)) {
  # Falls du statt Ordner direkt den Pfad zur EXE übergibst:
  if (Test-Path -LiteralPath $installPathWinSCP) {
    $winScpCom = $installPathWinSCP
  } else {
    throw "WinSCP.com nicht gefunden. installPathWinSCP=$installPathWinSCP"
  }
}


if (!(Test-Path -LiteralPath $sipFolder)) {
  throw "sipFolder existiert nicht: $sipFolder"
}

# Alle Dateien aus sipFolder sammeln (oder passe es an: eine konkrete Datei)

$files = @(Get-ChildItem -LiteralPath $sipFolder -File -Recurse -Include *.tgz)
if ($files.Count -eq 0) {
  throw "Keine Dateien in sipFolder gefunden: $sipFolder"
}

$logPath = Join-Path $env:TEMP ("winscp_" + [guid]::NewGuid().ToString() + ".log")
$cmdPath = Join-Path $env:TEMP ("winscp_" + [guid]::NewGuid().ToString() + ".txt")

# --- PROTOKOLL WAHL ---
# Kollege nutzt FTP => wir machen FTP.
# Wenn du FTPS oder SFTP brauchst, unten umstellen (siehe Kommentare).

# FTP:
$openLine = "open ftp://$($User):$($password)@$($HostName)/"

# FTPS (explicit TLS) Beispiel:
# $openLine = "open ftpes://$($User):$($password)@$($HostName)/"

# SFTP Beispiel:
# $openLine = "open sftp://$($User):$($password)@$($HostName):22/ -hostkey=""ssh-ed25519 255 <fingerprint>"""

@"
option batch abort
option confirm off
option transfer binary
$openLine
cd "$destResponse"
"@ | Set-Content -LiteralPath $cmdPath -Encoding ASCII

# PUT-Befehle anhängen (alle Dateien)
foreach ($f in $files) {
  ("put `"{0}`"" -f $f.FullName) | Add-Content -LiteralPath $cmdPath -Encoding ASCII
}

"exit" | Add-Content -LiteralPath $cmdPath -Encoding ASCII

try {
  $p = Start-Process -FilePath $winScpCom `
    -ArgumentList @("/ini=nul", "/log=`"$logPath`"", "/script=`"$cmdPath`"") `
    -NoNewWindow -Wait -PassThru

  if ($p.ExitCode -ne 0) {
    $tail = ""
    if (Test-Path $logPath) { $tail = (Get-Content $logPath -Tail 120) -join "`n" }
    throw "Upload fehlgeschlagen. ExitCode=$($p.ExitCode)`n--- WinSCP Log (Tail) ---`n$tail"
  }

  Write-Output ("Upload erfolgreich: {0} Datei(en) -> {1}:{2}" -f $files.Count, $HostName, $destResponse)
}
finally {
  Remove-Item -LiteralPath $cmdPath -Force -ErrorAction SilentlyContinue
  # Log behalten zum Debuggen; bei Erfolg kannst du es auch löschen:
  # Remove-Item -LiteralPath $logPath -Force -ErrorAction SilentlyContinue
}

