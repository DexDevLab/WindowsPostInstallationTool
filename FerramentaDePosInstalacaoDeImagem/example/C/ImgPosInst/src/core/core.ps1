$Server = ''
$Dom = ''
$Hostname = ''
$Username = ''
$Password = ''
$pass = ConvertTo-SecureString -AsPlainText $Password -Force
$Cred = New-Object System.Management.Automation.PSCredential -ArgumentList $Username,$pass
Invoke-Command -ComputerName $Server -Credential $Cred -ScriptBlock { Add-Computer -ComputerName $Hostname -Domain $Dom }