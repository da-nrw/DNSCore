
##  Transfer Script Eugen Terbunski, Gaby Bender LVR-InfoKom

$s="{0:yyyy-MM-dd HH:mm.ss}" -f (get-date)
Write-Host $s


#------------------ SIP TRANSFER AND RESPONSE

$passwords = Get-Content $installPath"password.txt" | ConvertTo-SecureString  # Must be SecureString encrypted
$credential = New-Object System.Management.Automation.PSCredential($User,  $passwords)
$BSTR =[System.Runtime.InteropServices.Marshal]::SecureStringToBST($credential.Password)
 
	if ([System.IO.File]::Exists($installPath + "transfer.txt")) {
		Remove-item -Force $installPath"transfer.txt"
	}
	$fileNames = Get-ChildItem -Path $installPath"sips\"  -Recurse -Include *.tgz
	for ($i=0; $i -lt $fileNames.Count; $i++) {
		$fullname = $fileNames[$i].FullName
		$basename = $fileNames[$i].BaseName
		$fullname | Out-File -Append $installPath"transfer.txt"
		New-Item $installPath"sips\"$basename.rdy -Force -ItemType file
		$ct = $installPath + "sips\" + $basename + ".rdy"
		$ct | Out-File -Append $installPath"transfer.txt"
	}
	Add-Type -Path $installPathWinSCP
    Write-Host "### vor sessionOptions ###"
    Write-Host "installPathWinSCP : $installPathWinSCP"
    Write-Host "BSTR: $BSTR"

	$sessionOptions = New-Object WinSCP.SessionOptions -Property @{
		Protocol = [WinSCP.Protocol]::Ftp
        HostName = $HostName
		PortNumber = 21
        UserName = $credential.UserName
        Password =  [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
		FtpSecure = [WinSCP.FtpSecure]::Explicit
		TlsHostCertificateFingerprint = $TlsHostKeyFingerprint
		
    }
     Write-Host "sessionOptions : $sessionOptions"

    $session = New-Object WinSCP.Session
    try
    {
    $session.Open($sessionOptions)
	# --------------- FREE SPACE 
	try {
		$session.GetFiles($remotePath + "freeSpace.txt", $destResponse + "\freeSpace.txt").Check()
		$strFs = Get-Content -Path $destResponse"\freeSpace.txt" | Out-String
		$int64 = [int64]$strFs.Replace('G','')
		Write-Host "$int64 GB frei!"
		if ([int64]$minFreeSpaceG -gt $int64) {
			Write-Host "ZU WENIG FREIER Speicher auf Zieldevice $remotePath !"
			exit 1			
		}
		
	} catch {
		Write-Host "Ermittlung von freiem Speicherplatz auf Ziel-LW fehlgeschlagen: Error: $($_.Exception.Message)"
	}
	
	# --------------- MAIN Transfer
	$skip = 0
	
	$lines = Get-Content -ErrorAction SilentlyContinue "transfer.txt"
	foreach ($line in $lines)
	{
		$file = Get-ChildItem $line
		Write-Host "Hochladen von $line ..."
		if ( $file.Extension.Contains("tgz" )) {	
			$cmd = "Select count(*) from sips where origName='"+ $file.BaseName + "';"
			$count = `.\sqlite\sqlite3.exe .\sqlite\database.db $cmd`
			if ($count -eq "1") {
				Write-Host "SIP ist schon mal abgegeben worden"
				$cmd = "Select count(*) from sips where origName='" + $file.BaseName + "' and archived in ('D','F');"
				$count = `.\sqlite\sqlite3.exe .\sqlite\database.db $cmd`
				if ($count -eq "1") {
					Write-host "SIP WAR NOCH NICHT ERFOLGREICH ARCHIVIERT: $file -- > ABBRUCH !" 
					$skip = 1
				}
				$cmd = "Select count(*) from sips where origName='" + $file.BaseName + "' and archived='T';"
				$count = `.\sqlite\sqlite3.exe .\sqlite\database.db $cmd`
				if ($count -eq "1") {
					Write-Host "SIP IST EIN DELTA"
					$cmd = "update sips set archived='D' where origName='" + $file.BaseName + "' and archived='T';"
					$count = `.\sqlite\sqlite3.exe .\sqlite\database.db $cmd`
					$skip = 0
				} 
			} else {
			$skip = 0
			.\sqlite\insertJob.ps1 $file.BaseName
			}
		} 
		if (($skip -eq 0 ) -or ($SipTransferAnyway)) {
			$session.GetFiles($remotePath + "freeSpace.txt", $destResponse + "\freeSpace.txt").Check()
			$strFs = Get-Content -Path $destResponse"\freeSpace.txt" | Out-String
			$int64 = [int64]$strFs.Replace('G','')
			Write-Host "$int64 GB frei!"
			if ([int64]$minFreeSpaceG -gt $int64) {
				Write-Host "ZU WENIG FREIER Speicher auf Zielgerät $remotePath !"
				exit 1			
			}	
			$session.PutFiles($line, $remotePath).Check()
			Remove-item $file
		}
		
	}
	
	# -------------- REPORT DELIVERY AND RESPONSE IF ANY
	$transferOptions = New-Object WinSCP.TransferOptions 
	$transferOptions.FilePermissions = New-Object WinSCP.FilePermissions
	$transferOptions.FilePermissions.Octal = "0666"
	$transferOptions.TransferMode.Automatic
	Write-Host "Erzeuge Abgabeliste und lade in den Eingangsordner hoch"
	.\sqlite\exportJobs.ps1 | Out-File -Append -Encoding "ascii" $installPath"report.csv"
	$session.PutFiles($installPath + "report.csv", $remotePath, $transferOptions).Check()
	try {
		$session.GetFiles($responseRemotePath + "report.csv", $destResponse + "\" + (Get-Date -Format "yyyMMdd")+ "_report.csv_tmp").Check()
		Write-Host "Reportdatei aus DNS-Ausgangsordner heruntergeladen!"
		$update = $destResponse + "\" + (Get-Date -Format "yyyMMdd")+ "_report.csv_tmp"
		$md5 = New-Object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
		$hash = [System.BitConverter]::ToString($md5.ComputeHash([System.IO.File]::ReadAllBytes($update)))
		$fileNames = Get-ChildItem -Path $destResponse -Recurse -Include *.csv
		$upd = 1
		for ($i=0; $i -lt $fileNames.Count; $i++) {
			$md5 = New-Object -TypeName System.Security.Cryptography.MD5CryptoServiceProvider
			$hash2 = [System.BitConverter]::ToString($md5.ComputeHash([System.IO.File]::ReadAllBytes($fileNames[$i])))
			if ($hash -eq $hash2) {
				Write-Host "Dieser DNS Report ist nicht neu und wurde bereits als : $fileNames[$i] heruntergeladen (Der DNS Systemevent ist noch nicht gelaufen?)"
				$upd = 0
			}
		}
		if ($upd -eq 1) {
			.\sqlite\updateJobCSV.ps1 $update
			Copy-Item $update $update.TrimEnd("_tmp")
		}
	}
	catch {
		Write-Host "Keine Reportdatei gefunden! Error: $($_.Exception.Message)"
	}
    }
    finally
    {
        # Disconnect, clean up
        $session.Dispose()
    }
	
	$s="{0:yyyy-MM-dd HH:mm.ss}" -f (get-date)
	Write-Host $s
    exit 0
 
