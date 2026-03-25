param([Parameter(Mandatory = $true)] [String] $sipFolder,
  [Parameter(Mandatory = $true)] [String] $installPathWinSCP, 
  [Parameter(Mandatory = $true)] [String] $HostName, 
  [Parameter(Mandatory = $true)] [String] $User , 
  [Parameter(Mandatory = $true)] [String] $passwordContractorEnc,
  [Parameter(Mandatory = $true)] [String] $destResponse,
  [Parameter(Mandatory = $false)] [String] $proxyHost,
  [Parameter(Mandatory = $false)] [String] $proxyPort,
  [Parameter(Mandatory = $false)] [String] $proxyUser,
  [Parameter(Mandatory = $false)] [String] $asswordProxyEnc,
  [Parameter(Mandatory = $false)] [String] $keyBase64);
    
#$sipFolder="D:\DANRW\SIP-Builder-Erweiterung\SIP\"
#$installPathWinSCP="D:\workspace\ci\DNSCore\SIP-Builder\src\main\winscp\WinSCP-6.5.5-Automation\"
#$HostName="da-nrw-q.lvr.de"
#$User="LVR-Test"
#$password="gigaset55a"
#$destResponse="D:\DANRW\SIP-Builder-Erweiterung\response\"
#$proxyHost="fproxy.lvrintern.lvr.de"
#$proxyPort="8080"
#$proxyUser="SVCPXYZ1334006CI"
#$proxyPassword="z2z25NoL0a6qBbaWlUh!yI%25NI0V"
#$logDir = "D:\workspace\ci\DNSCore\SIP-Builder\src\main\winscp\log\"
$logDir= $installPathWinSCP

$passwordPlain = ConvertTo-SecureString $password -AsPlainText -Force
#$securePassword2 = ConvertTo-SecureString $plainPassword2 -AsPlainText -Force

if (!(Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
}

$logFile = Join-Path $logDir ("winscp_" + (Get-Date -Format "yyyy-MM-dd") + ".log")

function Write-Log {

    param (
      [string]$Message,
      [ValidateSet("INFO","WARNING","ERROR")]
      [string]$Level = "INFO"
    )

    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logEntry = "$timestamp [$Level] $Message"
    
    Add-Content -Path $logFile -Value logEntry

    # Konsole
    switch ($Level) {
        "INFO"    { Write-Host $logEntry }
        "WARNING" { Write-Host $logEntry -ForegroundColor Yellow }
        "ERROR"   { Write-Host $logEntry -ForegroundColor Red }
    }
}

function Unprotect-AesString {
    param(
        [Parameter(Mandatory = $true)]
        [string]$CipherTextBase64,

        [Parameter(Mandatory = $true)]
        [string]$KeyBase64
    )

    $fullCipher = [Convert]::FromBase64String($CipherTextBase64)
    $key = [Convert]::FromBase64String($KeyBase64)

    if ($fullCipher.Length -lt 17) {
        throw "Ungültiger Ciphertext."
    }

    $iv = New-Object byte[] 16
    [Array]::Copy($fullCipher, 0, $iv, 0, 16)

    $cipherBytes = New-Object byte[] ($fullCipher.Length - 16)
    [Array]::Copy($fullCipher, 16, $cipherBytes, 0, $cipherBytes.Length)

    $aes = [System.Security.Cryptography.Aes]::Create()
    $aes.Mode = [System.Security.Cryptography.CipherMode]::CBC
    $aes.Padding = [System.Security.Cryptography.PaddingMode]::PKCS7
    $aes.Key = $key
    $aes.IV = $iv

    try {
        $decryptor = $aes.CreateDecryptor()
        $plainBytes = $decryptor.TransformFinalBlock($cipherBytes, 0, $cipherBytes.Length)
        return [System.Text.Encoding]::UTF8.GetString($plainBytes)
    }
    finally {
        $aes.Dispose()
    }
}

#-- Needed Params
$installPath = "\target\scripts" 

$remotePath = "/incoming/"
$responseRemotePath = "/outgoing/"

# SecureString -> Klartext nur für das Erzeugen des Session-URLs im RAM
$plainContractorPwd = Unprotect-AesString -CipherTextBase64 $passwordContractorEnc -KeyBase64 $KeyBase64
$plainProxyPwd =  Unprotect-AesString -CipherTextBase64 $asswordProxyEnc -KeyBase64 $KeyBase64


# Beispielausgabe nur zur Kontrolle
Write-Host "plainContractorPwd: $plainContractorPwd"
Write-Host "plainProxyPwd Länge: $plainProxyPwd"


$securePasswordContractor = ConvertTo-SecureString $plainContractorPwd -AsPlainText -Force
$securePasswordProxy = ConvertTo-SecureString $plainProxyPwd -AsPlainText -Force
Write-Host "securePasswordContractor: $securePasswordContractor"
Write-Host "securePasswordProxy: $securePasswordProxy" 
	    
#Optional Params
#-----------------------------------------------------------------


#$destResponse = $installPath + "response"

Get-ChildItem "D:\workspace\ci\DNSCore\SIP-Builder\src\main\winscp\WinSCP-6.5.5-Automation\" -Recurse | Unblock-File
Add-Type -Path "D:\workspace\ci\DNSCore\SIP-Builder\src\main\winscp\WinSCP-6.5.5-Automation\WinSCPnet.dll"

$s="{0:yyyy-MM-dd HH:mm.ss}" -f (get-date)


Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$sessionOptions = New-Object WinSCP.SessionOptions -Property @{

    Protocol =  [WinSCP.Protocol]::Ftp
    HostName = $HostName
    PortNumber = 21
    UserName = $User
    Password = $plainContractorPwd
}

$sessionOptions.AddRawSettings("ProxyMethod", "3");
$sessionOptions.AddRawSettings("ProxyHost", $proxyHost);
$sessionOptions.AddRawSettings("ProxyPort", $proxyPort);
$sessionOptions.AddRawSettings("ProxyUsername", $proxyUser);
$sessionOptions.AddRawSettings("ProxyPassword", $plainProxyPwd);

$session = New-Object WinSCP.Session
$session.SessionLogPath = $logFile
    try
    {
    Write-Log "Verbinde mit $HostName" 
    $session.Open($sessionOptions)

    Write-Log "Verbindung erfolgreich." 
    

    # Transferoptionen
    $transferOptions = New-Object WinSCP.TransferOptions
    $transferOptions.TransferMode = [WinSCP.TransferMode]::Binary

    # Upload von Dateien

    Write-Log "Starte Upload..." 
   
   $files = Get-ChildItem $sipFolder -Filter *.tgz


    foreach ($file in $files) {
        
        Write-Log "Gefunden Dateien: $file.FullName" 

        $zielDatei = Join-Path $remotePath $file.Name
        Write-Host "Ziel Datei: $zielDatei"

        if (Test-Path $zielDatei) {
            Write-Host "Datei existiert bereits im Ziel: $zielDatei" 
            Write-Log "Datei existiert bereits im Ziel: $zielDatei" "WARNING"
            continue
        }

        $transferResult = $session.PutFiles($file.FullName, $remotePath, $false, $transferOptions)

       # Prüfen ob Fehler aufgetreten sind
        $transferResult.Check()
    }
    Write-Log "Upload erfolgreich abgeschlossen."

}
catch {
    Write-Log "FEHLER:" "ERROR"
    Write-Log  $_.Exception.Message "ERROR"
}
finally {
    # Session sauber schließen
    $session.Dispose()
    Write-Log "Session geschlossen."
    
}






