# Windows Post Installation Tool
 Application for custom adjust deployment to specific system environment.


## Project Implementation Causes

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
 client circumstances, it was not possible.
 
 The program resumes itself in "some steps who can perform and deploy DOS scripts".
 
 In FerramentaDePosInstalacaoDeImagem\example I left a example of file structure and
 folder who will receive the resources that are vital to the program.
 In example\C\ImgPosInst\src\core\DRV\W7E\DellLatitude3470
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


## 05 / 27 / 2019

 Project creation.


## 08 / 28 / 2019

 Project Publishing. Program is funcional.
 
 Next Steps: 
 
 - Create a JSON file containing script information for ComboBox listing, CMD
 commands etc and use it efficiently in program;
 - Revalidate commands by text instead of using bat file;
 - Discovering why Intel HD Graphics is the only driver who isn't being installed
 correctly;
 - Discovering why NETDOM doesn't validate computer into Domain (a possible Windows
 incompatibility?)


## 10 / 16 / 2019
 
 - Exclusion of unused scripts and files.
 - Creation of a single file (not JSON yet, but better than nothing) called core.cfg.

## 10 / 17 / 2019
 
 - Alteration in core.cfg and apply.bat for both versions of OSes.
 
In main Controller:

- New method for load script file
- New method for execute commands by script line
- New command for installing independent video driver
- Excluded commanding for delete temporary files and included into initialization windows batch (apply.bat)
- Method doCommands() removed

## 10 / 25 / 2019
 
 Still don't know why video drivers wasnt being installed correctly using batch command.
 
 - Now program uses Powershell with -Invoke and -Credentials to connect a Admin capable machine and run
 remotely a ps1 file who adds machine to Domain;
 - Changing ProcessBuilder() from main command (runCMD(List<String> list)) by Runtime.getRuntime().exec();
 - Removed Thread.sleep from various lines (trusting in Process.wait() to interrupt thread;
 - Program version now in Window Title

## v2.0.1-20191025-27
 
- Changed folder struture
- Fixed apply.bat for Windows 7
- Added specific performance tweaks for Windows 7

## v3.0.1-20191114-187
 
A bunch of changes here:

In core.cfg:
- Fixed bug when performance tweaks for 7 may program
pause waiting for response in cmd because of lack of the
"/f" parameter
- Name of remote PC who will run PowerShell for join machine
into domain isn't necessary, since a new form of joining it
was implemented (below).

In core.ps1:
- Changed core.ps1 to change Hostname (using wmic wasn't working)
and join to Domain soon after (seems unstable; if a machine was
added, trying remove it from Domain and using core.ps1 seems not
work).

In apply.bat (W7E and WXE):
- Fix bug which "ImgPosInst" folder wasnt deleted
- Implemented a validation: If in last restart the machine wasnt
joined in Domain, the techinitian will login into Administrator
local account. Apply.bat will do the rest, running the core.ps1 again.

In main package:
- Created Class Exceptions to handle exceptions and errors, in order to
show them to the techinitian during the process.

In ImgPosInst.java:
- The warning about the corefile (core.cfg) missing and the question for
insert the resource folder was removed and implemented in a standalone program,
called "Windows Post Installation Tool Starter"
- Changed the alert type only for trying to make a better showing alert.

In ImgPosInst.fxml:
- Created a new label to serve as a status label about the progress detail.

In ImgPosInstFXMLController.java:
- Put boolean variables to lock the dialog messages (Idk the dialogs simply don't
WAIT TO THE USER TO CLICK ON BUTTON!)
- Created the label linkage (@FXML reference)
- Transformed the Main Thread in a Main Task using Future Task.
- Created a method (replaceInFile) to read the core.ps1 file and change the values of its
variables accordingly.
- Created Dialog Messages about the Domain Connection (now tested after main button pressed)
and quit the program (now it is possible)
- Created a method (isProcessOpen) to check whether SEP was installed already at machine or not
(instead of simply waiting for x minutes to let the program run again)
- Changed the code to run the cmd depending of the list in order to run in a separated Thread.
- Created a separated Thread only for driver install.

## v4.0.0-20191204-242
 
I created a Class for reading the Script File as a new format. You know, using line per line reading can be a bother, specially when removing some item from a part of list (the numbers change the order so isn't nice). That's why I created the Class ScriptFileReader, so I can read the Script File using brackets and dividing the lists in keywords.


## v4.0.12-20200129-254

 
I made a bunch of changes since last update, and gonna try to put them out here.

- Added reg file to disable Windows Store, since it isn't disable by Windows Image (DISM) anymore.
I decided make this since some apps (i. e., bankline applications) could be useful and needs to be
installed but I want do avoid users installing games at the same time. So I created a reg file to
Disable Windows Store and another regfile (isn't in the project) to undo this change.

- Changed apply.bat (both Windows 7 and 10 versions) for to do not using a IF sentence to try to
apply PowerShell Script again - if the machine doesn't integrate the Domain while the program running,
the technitian need to do it by himself. Added a command from prevent Skype for Business open automatically
at logon.

- Removed version.txt file for both Windows versions. Unecessairy.

- Created igxpin.bat, to install video driver using Intel Driver Installer (setup.exe / setup.msi), since upnputil
wasn't working at all.

- Fixed some bugs at warning message boxes. And I just don't know why some Alert Boxes just doesn't appear. I needed to
use a Future Task with Platform RunLater and create a loop to lock main thread and this is disgusting, but I don't have any other options.
Check ImgPosInstFXMLController, at lines 1063-1068.

- Created a Dialog Box asking for system restart. This solves the problems when the Antivirus wasn't
installed but the System is rebooting already.
