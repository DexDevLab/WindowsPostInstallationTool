# Windows Post Installation Tool
 Application for custom adjust deployment to specific system environment.


# Project Implementation Causes

 While working at IT Infrastructure, I discovered that System Customization is
 vital for production. Customize and personalize a System makes it more efficient
 in enviroments who require specific resources at hand. Also, it's important to
 remove useless resources and postpone background tasks who doesn't match to
 the job.
 It's very known for IT Analysts and System Engineers the OS customization. So,
 using Microsoft resources and documentation to provide an optimized Windows setting,
 I create the Post-Windows Installation Tool, who configures vital programs and makes
 proper tweaks to machine.
 
 PS: I know MDT and WDS from Microsoft could do this in a more efficient way, but in my
 client circumstances, it's not possible.
 
 
 The program resumes itself in "some steps who can perform and deploy DOS scripts". We can
 take the example with the "src/core/WXE/activation.cfg":
 
 changepk.exe /ProductKey WXXXX-TXXXX-7XXXX-TXXXX-JXXXX
 
 This file just have 1 command line for DOS. Again, I'll change it (probably to a single JSON
 file containing all the commands, who will be deployed one at a time).
 
 In FerramentaDePosInstalacaoDeImagem\example I left a example of file structure and
 folder who will receive the resources that are vital to the program.
 In D:\Dropbox\DEV\Coding\Git\Repositories\Windows-Post-Installation-Tool
 \FerramentaDePosInstalacaoDeImagem\example\C\ImgPosInst\src\core\DRV\W7E\DellLatitude3470
 I left a template for the zip file containing the drivers. You can assume all other folders must
 have a zip file just like that.

 
# Logbook

 To be honest, I didn't find any problems in planning this project. At the beginning, I
 knew fast and easy everything I needed. The problem came when I faced the features.
 Deploying commands using Process Builder Class shouldn't be so hard, but sometimes
 the command acted random. Even now, I couldn't find exactly what happened, so I had
 to change: instead of reading a config file, now ProcessBuilder runs a batch file.
 Obviously, that isn't what I want, and I will work in this project again to change it
 as soon as I have a dummy machine for experiments (I can't run some DOS commands from
 scripts or else I'll mess my System Configuration).
 
 
 05 / 27 / 2019
 
 Project creation.
 ----------------------------------------------------------------------------------------
 08 / 28 / 2019
 
 Project Publishing. Program is funcional.
 Next Steps: 
 -Create a JSON file containing script information for ComboBox listing, CMD
 commands etc and use it efficiently in program;
 -Revalidate commands by text instead of using bat file;
 -Discovering why Intel HD Graphics is the only driver who isn't being installed
 correctly;
 -Discovering why NETDOM doesn't validate computer into Domain (a possible Windows
 incompatibility?).
 -----------------------------------------------------------------------------------------
 10 / 16 / 2019
 
 -Exclusion of unused scripts and files.
 -Creation of a single file (not JSON yet, but better than nothing) called core.cfg.
 -----------------------------------------------------------------------------------------
 10 / 17 / 2019
 
 -Alteration in core.cfg and apply.bat for both versions of OSes.
In main Controller:
-New method for load script file
-New method for execute commands by script line
-New command for installing independent video driver
-Excluded commanding for delete temporary files and included into initialization windows batch (apply.bat)
-Method doCommands() removed
 -------------------------
 10 / 25 / 2019
 
 Still don't know why video drivers wasnt being installed correctly using batch command.
 -Now program uses Powershell with -Invoke and -Credentials to connect a Admin capable machine and run
 remotely a ps1 file who adds machine to Domain;
 -Changing ProcessBuilder() from main command (runCMD(List<String> list)) by Runtime.getRuntime().exec();
 -Removed Thread.sleep from various lines (trusting in Process.wait() to interrupt thread;
 -Program version now in Window Title;
 
 



