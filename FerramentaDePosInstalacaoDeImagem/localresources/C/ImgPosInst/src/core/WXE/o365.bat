reg add "HKEY_LOCAL_MACHINE\software\policies\Microsoft\office\16.0\common\officeupdate" /v EnableAutomaticUpdates /t REG_DWORD /d 0 /f 
schtasks.exe /delete /f /tn "\Microsoft\Office\Office Automatic Updates" 
schtasks.exe /delete /f /tn "\Microsoft\Office\ClickToRun Service Monitor" 
schtasks.exe /delete /f /tn "\Microsoft\Office\OfficeTelemetryAgentLogOn2016" 
schtasks.exe /delete /f /tn "\Microsoft\Office\OfficeTelemetryAgentFallBack2016"
reg add HKLM\SOFTWARE\Policies\Microsoft\Windows\WindowsUpdate\AU /v AUOptions /t REG_DWORD /d 1