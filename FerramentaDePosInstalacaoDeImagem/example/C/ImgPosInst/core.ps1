$ComputerName = "HOSTNAME"
Remove-ItemProperty -path "HKLM:\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters" -name "Hostname" 
Remove-ItemProperty -path "HKLM:\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters" -name "NV Hostname" 
New-PSDrive -name HKU -PSProvider "Registry" -Root "HKEY_USERS"
Set-ItemProperty -path "HKLM:\SYSTEM\CurrentControlSet\Control\Computername\Computername" -name "Computername" -value $ComputerName
Set-ItemProperty -path "HKLM:\SYSTEM\CurrentControlSet\Control\Computername\ActiveComputername" -name "Computername" -value $ComputerName
Set-ItemProperty -path "HKLM:\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters" -name "Hostname" -value $ComputerName
Set-ItemProperty -path "HKLM:\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters" -name "NV Hostname" -value  $ComputerName
Set-ItemProperty -path "HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Winlogon" -name "AltDefaultDomainName" -value $ComputerName
Set-ItemProperty -path "HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Winlogon" -name "DefaultDomainName" -value $ComputerName
#Set-ItemProperty -path "HKU:\.Default\Software\Microsoft\Windows Media\WMSDK\General" -name "Computername" -value $ComputerName

$Dom = "DOMAIN"
$Usr = "USER"
$Pwd = "PASSWORD" | ConvertTo-SecureString -asPlainText -Force
$username = "$Dom\$Usr"
$credential = New-Object System.Management.Automation.PSCredential($username,$Pwd)
Add-Computer -DomainName $Dom -Credential $credential