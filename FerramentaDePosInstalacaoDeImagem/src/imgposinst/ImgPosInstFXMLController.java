/**
 * Main interface Class.
 */
package imgposinst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;


/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 27/05/2019
 * @version 1.0.2-20190828-22
 * 
 * Main Class. Implements interface.
 */
public class ImgPosInstFXMLController implements Initializable
{
  
  //----------------------------------------------------------------
  // Log generation variables.
  //----------------------------------------------------------------
  
  /**  Set Date/time format for log entry. */
  DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  /**  Get date/time for log entry. */
  String time = df.format(Calendar.getInstance().getTime());
  /**  Set Date/time format for log file. */
  DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd--HH.mm.ss");
  /**  Get date/time for log file. */
  String time2 = df2.format(Calendar.getInstance().getTime());
  /** Set log file name based on time/date. */
  File logname = new File(time2 + ".txt");
  /** Set log file location. */
  File logfile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "logs" + File.separator + logname);
  /** Line separator for text concatenation. */
  final String line = System.getProperty("line.separator");
  
  
  //----------------------------------------------------------------
  // CMD script files and related variables.
  // (In future, this will be changed to a single JSON file.)
  // Note: At first, I used CMD script files (.cfg) but I needed to change it
  // to batch files (.bat) because I was encountering many errors while running
  // the commands. The intention is to use a single JSON file at future.
  //----------------------------------------------------------------
  
  /** 
   * File cointaining list of Computer models listed on ComboBox.
   * @see #comboboxmachine
   */
  File machinelistfile = new File ("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "core" + File.separator + "machinelist.cfg");
  /** 
   * File cointaining list of Computer types listed on ComboBox.
   * @see #comboboxmachinetype
   */
  File machinetypelistfile = new File ("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "core" + File.separator + "machinetype.cfg");
  /** 
   * File cointaining list of sites (Facilities, Offices etc) listed on ComboBox.
   * @see #comboboxsite
   */
  File sitelistfile = new File ("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "core" + File.separator + "sitelist.cfg");
  /** Driver files root directory. */
  File drvroot;
  /** Zip file cointaining drivers. */
  File zipdrvfile;
  /** Power Plan adjust script file. */
//  File powerplan;
  /** Windows Services adjustments script file. */
//  File services;
/** Remote Connections tweaks script file. */
//  File remotecon;
/** Windows Store and App Shortcuts disablement script file. */
//  File windowsstore;
/** Performance settings, custom programs and other deployments script file. */
//  File performance;
/** Office 365 automatic updates disablement script file. */
//  File o365;
/** Windows Activation script file. */
//  File activation;
  
  /** String containing AlertBox Message. */
  String message;
  /** IP Address for Domain verification. */
  String ip1 = "10.102.13.9";
  /** IP Address for Domain verification. */
  String ip2 = "10.102.13.8";
  /** IP Address for Domain verification. */
  String ip3 = "172.17.29.58";
  /* Confirm if computer has connection to Domain. */
  boolean domainconnected = false;
  /** Gets OS version. */
  String osver;
  /** Gets technician user ID logged. */
  String user;
  /** Gets technician password logged. */
  String pass;
  /** 
   * Gets computer selected by combobox. 
   * @see #comboboxmachine
   */
  String machinevalue;
  /** 
   * If machine selected in "machinevalue" is "other",
   * set installdrivers to "false". 
   */ 
  boolean installdrivers = true;
  /** 
   * Numeric ID from combobox machine selection. 
   * @see #comboboxmachine
   */
  int machineid;
  /** Hostname suffix (aka Serial Number from machine). */
  String hostsuffix;
  /** 
   * Corporation Site selected in combobox. 
   * @see #comboboxsite
   */
  String sitevalue;
  /** 
   * Numeric ID from combobox site selection. 
   * @see #comboboxsite
   */
  int siteid;
  /** 
   * Gets type of computer selected in ComboBox. 
   * @see #comboboxmachinetype
   */
  String machinetype;
  /** TAG related to machine type. */
  String machinetag;
  /** Hostname prefix (aka depends of the computer site). */
  String hostprefix;
  /** Computer hostname. */
  String hostname;
  /** Old hostname before changing. */
  String oldhostname;
  
  //----------------------------------------------------------------
  // FXML Variables
  //----------------------------------------------------------------
  
  /**
   * Object interface. Shows ComboBox for select type
   * of machine/computer.
   */
  @FXML
  private ComboBox<String> comboboxmachinetype;
  
  /**
   * Object interface. Shows ComboBox for select
   * machine brand/model.
   */
  @FXML
  private ComboBox<String> comboboxmachine;
  
  /**
   * Object interface. Shows ComboBox for select
   * site (factory, office etc).
   */
  @FXML
  private ComboBox<String> comboboxsite;

  /**
   * Object interface. Shows Label for program
   * status/progress.
   */
  @FXML
  private Label labelstatus;
  
  /**
   * Object interface. Shows Label for simple message.
   */
  @FXML
  private Label labelstatus2;

  /**
   * Object interface. Progress Bar.
   */
  @FXML
  private ProgressIndicator progressbar;
  
  /**
   * Object interface. "Start" button.
   */
  @FXML
  private Button buttoniniciar;
  
  /**
   * Object interface. Field to type 
   * technician username.
   */
  @FXML
  private TextField textfielduser;
  
  /**
   * Object interface. Field to type 
   * technician password.
   */
  @FXML
  private PasswordField passwordfieldpass;
  
  
  /**
   * Event for pressing "start" button.
   * @see #buttoniniciar
   * @throws IOException in a common failure
   */
  @FXML
  void acaobuttoniniciar() throws IOException 
  {
    try {log(null, "INFO", "Botão pressionado");} catch (FileNotFoundException e){}
    
    buttoniniciar.setDisable(true);
    comboboxsite.setDisable(true);
    comboboxmachine.setDisable(true);
    comboboxmachinetype.setDisable(true);
    progressbar.setVisible(true);
    labelstatus.setVisible(true);
    labelstatus2.setVisible(true);
    textfielduser.setDisable(true);
    passwordfieldpass.setDisable(true);
    
    user = textfielduser.getText();
    pass = passwordfieldpass.getText();
    
    if (user.isEmpty() | pass.isEmpty())
    {
      log(null, "ERRO", "USUÁRIO OU SENHA NULOS OU INVÁLIDOS.");
      message = "nocredentials";
      FutureTask<String> nocredentials = new FutureTask<>(new Dialog());
      Platform.runLater(nocredentials);
      textfielduser.setDisable(false);
      passwordfieldpass.setDisable(false);
      buttoniniciar.setDisable(false);
    }
    else
    {
      log(null, "INFO","ID do Técnico: " + user);
      log(null, "INFO","Senha do Técnico: " + pass);
      Thread Thread1 = new Thread (new MainTask());
      Thread1.setDaemon(true);
      Thread1.start();
      try {log(null, "INFO", "Iniciando Thread1 - MainTask");} catch (FileNotFoundException e){}
    }
  }
  
  /**
   * Write log in console and in file at same time.
   * 
   * @param throwable - exception throwable (if necessary)
   * @param logtype - "INFO" for common info and "ERRO" for errors
   * @param logmsg - The message to be written/logged into file
   * @throws IOException - If encounters errors while manipulating the log file
   */
  public void log (Throwable throwable, String logtype, String logmsg) throws IOException
  {
    time = df.format(Calendar.getInstance().getTime());
    logtype = "[" + logtype + "]: ";
    if (throwable != null) { logmsg = (line + logmsg + line + getStackTrace(throwable)); }
    System.out.println(time + logtype + logmsg);
    writeStringToFile(logfile, time + logtype + logmsg + line, "UTF-8", true);
  }
  
  
  /**
   * Move, copy, or delete a File object, writing to a log.
   * 
   * @param operation - "copiado" for copy, "movido" for move,
   * "excluído" for delete.
   * @param source - File or source
   * @param destination - File or destination. In delete, use "null" for definition
   * @throws IOException when file does not exist
   */
  public void FileIOAndLog (String operation, File source, File destination) throws IOException
  {
    String a;
    if(source.isDirectory()){a = "Diretório";} else {a = "Arquivo";}
    if (source.exists())
    {
      if (("excluído".equals(operation)) && (destination == null))
      {
        if (source.exists())
        {
          try {log(null, "INFO", a + " \"" + source.toString()+ " encontrado(a)");} catch (IOException ex){}
          FileUtils.deleteQuietly(source);
          if (!source.exists()){try{log(null, "INFO", a + " \"" + source.toString() + " " + operation);} catch (IOException ex){}}
        }
        else if (!source.exists()) {try {log(null, "INFO", a + " \"" + source.toString() + "\" não foi encontrado(a)");} catch (IOException ex){}}
        if (source.exists()) {try {log (null, "ERRO", a + " \"" + source.toString() + "\" AINDA EXISTE");} catch (IOException ex){}}
      }
      else
      {
        if ("copiado".equals(operation))
        {
          if(source.isDirectory()){FileUtils.copyDirectory(source,destination);}
          else 
          {
            if (destination.isDirectory()) {FileUtils.copyFileToDirectory(source,destination);} 
            else {FileUtils.copyFile(source,destination);}
          }
        }
        else if ("movido".equals(operation))
        {
          if(source.isDirectory()){moveDirectoryToDirectory(source, destination, true);}
          else {moveFile(source, destination);}
        }
        log(null, "INFO", a + " \"" + source.toString() + "\" " + operation + " para \"" + destination.toString() + "\"");
      }
    }
    else {{try {log (null, "ERRO", a + " \"" + source.toString() + "\" NÃO FOI ENCONTRADO.");} catch (IOException ex){}}}
  }
  
  /**
   * Initialization method.
   * 
   * @param url
   * @param rb
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    try {log(null, "INFO", "Logger inicializado. Janela aberta.");} catch (FileNotFoundException ex) {try {log(null, "ERRO", "FALHA AO INICIALIZAR log()");} catch (IOException ex1){}} catch (IOException ex){}
    
    progressbar.setVisible(false);
    labelstatus.setVisible(false);
    buttoniniciar.setDisable(true);
    comboboxmachine.setDisable(true);
    comboboxsite.setDisable(true);
    comboboxsite.setVisibleRowCount(8);
    comboboxsite.setTooltip(new Tooltip("Selecione a Unidade/Site onde o Equipamento será alocado."));
    comboboxmachine.setTooltip(new Tooltip("Selecione de acordo com a Marca/Modelo do equipamento para realizar a instalação de Drivers."));
    comboboxmachinetype.setTooltip(new Tooltip("Selecione o tipo de Equipamento."));
    textfielduser.setTooltip(new Tooltip("Insira o ID do técnico autorizado."));
    passwordfieldpass.setTooltip(new Tooltip("Insira a senha do técnico autorizado."));
    
    try
    {
      List<String> machinetypelist = FileUtils.readLines(machinetypelistfile, "UTF-8");
      comboboxmachinetype.getItems().addAll(machinetypelist);
      comboboxmachinetype.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() 
      { 

        @Override
        public void changed(ObservableValue ov, Number value, Number new_value) 
        {
          machinetype = comboboxmachinetype.getItems().get((int) new_value);
          try {log(null, "INFO", "Tipo de Equipamento \"" + machinetype + "\" de ID " + new_value.intValue() + " selecionado");} catch (FileNotFoundException ex){} catch (IOException ex){}
          if (new_value.intValue() == 0){machinetag = "D";}
          else if (new_value.intValue() == 1){machinetag = "N";}
          System.out.println(machinetag);
          comboboxmachine.setDisable(false);
        }
      });
      List<String> machinelist = FileUtils.readLines(machinelistfile, "UTF-8");
      comboboxmachine.getItems().addAll(machinelist);
      comboboxmachine.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() 
      { 

        @Override
        public void changed(ObservableValue ov, Number value, Number new_value) 
        {
          machinevalue = comboboxmachine.getItems().get((int) new_value);
          try {log(null, "INFO", "Equipamento \"" + machinevalue + "\" de ID " + new_value.intValue() + " selecionado");} catch (FileNotFoundException ex){} catch (IOException ex){}
          if ((new_value.intValue()) == 0) {installdrivers = false;}
          comboboxsite.setDisable(false);
        }
      });
      List<String> sitelist = FileUtils.readLines(sitelistfile, "UTF-8");
      comboboxsite.getItems().addAll(sitelist);
      comboboxsite.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() 
      {

        @Override
        public void changed(ObservableValue ov, Number value, Number new_value) 
        {
          sitevalue = comboboxsite.getItems().get((int) new_value);
          if (sitevalue.contains("("))
          {
            int startIndex1 = sitevalue.indexOf("(");
            int endIndex1 = sitevalue.indexOf(")");
            hostprefix = sitevalue.substring(startIndex1+1, endIndex1);
            {try {log (null, "INFO", "Prefixo da Unidade/Site: " + hostprefix);} catch (IOException ex){}}
          }
          else
          {
            hostprefix = "Nenhum";
            {try {log (null, "INFO", "Não foi escolhida uma Unidade/Site");} catch (IOException ex){}}
          }
          try {log(null, "INFO", "Unidade \"" + sitevalue + "\" de ID " + new_value.intValue() + " selecionada");} catch (FileNotFoundException ex){} catch (IOException ex){}
          buttoniniciar.setDisable(false);
          textfielduser.setDisable(false);
          passwordfieldpass.setDisable(false);
        }
      });
      log(null, "INFO", "Checando Sistema Operacional...");
      ProcessBuilder os = new ProcessBuilder("cmd", "/c", "ver");
      String osget = IOUtils.toString(os.start().getInputStream(), "UTF-8");
      if (osget.contains("Microsoft Windows [versão 10") | osget.contains("Microsoft Windows [vers�o 10"))
      {
        osver = "WXE";
        log(null, "INFO", osget);
        log(null, "INFO", "Windows 10 Enterprise");
      } 
      else 
      {
        osver = "W7E";
        log(null, "INFO", osget);
        log(null, "INFO", "Windows 7 Enterprise");
      }
      log(null, "INFO", "Checando conexão com o Domínio...");
      domainconnected = (testConnect(ip1) == true) && (testConnect(ip2) == true) && (testConnect(ip3) == true);
      if (domainconnected == false)
      {
        message = "nonetwork";
        log(null, "ERRO", "NÃO HÁ CONEXÃO DE REDE OU O DOMÍNIO NÃO PODE SER ACESSADO.");
        FutureTask<String> nonetwork = new FutureTask<>(new Dialog());
        Platform.runLater(nonetwork);
      }
    }
    catch (IOException ex){ try {log(ex, "ERRO", "ARQUIVOS NÃO ENCONTRADOS OU ENTRADA INVÁLIDA NA LISTA.");} catch (IOException ex1){} }
  }
  
  /**
   * Change label and logs it at the same time.
   * 
   * @param text - Text to change/log.
   */
  public void changeStatus(String text) 
  {
    Platform.runLater(() -> 
    {
      labelstatus.setText(text);
      try {log(null, "INFO", text);} catch (IOException ex1){}
    });
  }

  /**
   * Run DOS command contained in script file.
   * 
   * @param scriptfile - the script file containing commands
   * @throws java.io.IOException
   * @throws java.lang.InterruptedException
   */
  public void doCommands(File scriptfile) throws IOException, InterruptedException
  {
    short id = 0;
    LineIterator it = FileUtils.lineIterator(scriptfile, "UTF-8");
    while (it.hasNext()) 
    {
      id += 1;
      String cmdline = it.nextLine();
      log(null, "INFO", "Processando comando " + cmdline + " na linha " + id + " do arquivo de script " + scriptfile.getName());
      new ProcessBuilder("cmd", "/c", cmdline).start();
    }
    Thread.sleep(500);
    changeStatus("Concluído.");
  }
  
  /**
   * Test a network connection using IP verification in 5s timeout.
   * 
   * @param ip - IP for queue
   * @throws UnknownHostException when the address can not be reached
   * @throws IOException when log cannot be written.
   * @return false when there is not connection with IP
   */
  public boolean testConnect(String ip) throws UnknownHostException, IOException
  {
    InetAddress iptest = InetAddress.getByName(ip); 
    log(null, "INFO", "Testando conexão com o endereço IP " + ip + "...");
    if (iptest.isReachable(5000))
    {
      log(null, "INFO", "Conexão bem sucedida");
      return true;
    }
    else
    {
      log(null, "ERRO", "FALHA DE CONEXÃO AO ENDEREÇO IP " + ip);
      return false;
    }
  }
  
  /** Common generic Alert Box. */
  class Dialog implements Callable
  {

    @Override
    public Dialog call() throws Exception
    {
      Alert dialog = new Alert(Alert.AlertType.ERROR);
      dialog.initModality(Modality.APPLICATION_MODAL);
      Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
      stage.setAlwaysOnTop(true);
      stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
      stage.setOnCloseRequest((e) -> {e.consume();});
      if ("nonetwork".equals(message))
      {
        dialog.setTitle("Erro de Conexão de Rede");
        dialog.setHeaderText("Falha no Acesso ao Servidor de Domínio ou ao Serviço de Rede");
        dialog.setContentText("Não foi detectada uma conexão de rede com o Servidor de Domínio.\n"
                              + "Verifique a conexão de rede antes de continuar.\n"
                              + "Evite usar conexões sem fio, desligue o Wi-Fi e utilize um cabo com rede para a conexão.\n\n"
                              + "Será necessário ingressar manualmente ao Domínio após a reinicialização do sistema caso você continue sem validar uma conexão de rede.\n\n"
                              + "Ao clicar em \"OK\" o programa continuará mesmo se você não tiver conectado uma rede.");
      }
      else if ("nocredentials".equals(message))
      {
        dialog.setTitle("Erro de Credencial");
        dialog.setHeaderText("Usuário ou Senha nulos ou inválidos");
        dialog.setContentText("Verifique a senha e o usuário digitados.");
      }
      labelstatus.setVisible(false);
      labelstatus2.setVisible(false);
      progressbar.setVisible(false);
      dialog.showAndWait();
      message = null;
      log(null, "INFO", "Dialog() finalizado");
      return null;
    }
  }
  
  
  /**
   * Main Thread triggered when "iniciar" button
   * is pressed.
   * @see #acaobuttoniniciar()
   */
  public class MainTask implements Runnable
  {

    @Override
    public void run()
    {
      try
      {
        Thread.sleep(500);
        changeStatus("Iniciando...");
        Thread.sleep(500);
        changeStatus("Instalando o Symantec Endpoint Protection. Aguarde...");
        Thread.sleep(500);
        new ProcessBuilder("cmd", "/c", "sep.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core")).start();
        Thread.sleep(300000);
        changeStatus("Concluído");
        Thread.sleep(500);
        changeStatus("Ajustando perfil de Energia...");
        new ProcessBuilder("cmd", "/c", "powerplan.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        Thread.sleep(500);
        changeStatus("Configurando Serviços...");
        new ProcessBuilder("cmd", "/c", "services.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        Thread.sleep(500);
        changeStatus("Ajustando Configurações de Acesso Remoto...");
        new ProcessBuilder("cmd", "/c", "windowsstore.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        Thread.sleep(500);
        if ("WXE".equals(osver))
        {
          changeStatus("Desativando a Windows Store...");
          new ProcessBuilder("cmd", "/c", "windowsstore.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        }
        changeStatus("Realizando ajustes de performance...");
        new ProcessBuilder("cmd", "/c", "performance.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        Thread.sleep(500);
        changeStatus("Configurando o Office 365...");
        new ProcessBuilder("cmd", "/c", "o365.bat >NUL").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        Thread.sleep(500);
        drvroot = new File ("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "core" + File.separator + "DRV");
        if (installdrivers == true)
        {
          changeStatus("Listando drivers a serem instalados...");
          machinevalue = machinevalue.replaceAll("\\s", "");
          log(null, "INFO", "Diretório de driver selecionado: " + machinevalue);
          File drvdir = new File(drvroot + File.separator + osver + File.separator + machinevalue);
          zipdrvfile = new File (drvdir + File.separator + "zip.zip");
          ZipFile zipdrv = new ZipFile(drvdir + File.separator + "zip.zip");
          changeStatus("Descompactando os arquivos necessários...");
          zipdrv.extractAll(drvdir.toString());
          log(null, "INFO","Arquivo de pacote de driver extraído");
          changeStatus("Iniciando instalação de driver. Aguarde...");
          try {FileIOAndLog("copiado", new File (drvroot + File.separator + osver + File.separator + "pnputil.exe"), new File(drvdir + File.separator + "pnputil.exe"));} catch (IOException ex){}
          try {FileIOAndLog("copiado", new File (drvroot + File.separator + osver + File.separator + "drv.bat"), new File(drvdir + File.separator + "drv.bat"));} catch (IOException ex){}
          new ProcessBuilder("cmd", "/c", "drv.bat >NUL").directory(drvdir).start();
          Thread.sleep(300000);
          log(null, "INFO","Comando recursivo concluído");
        }
        Thread.sleep(500);
        changeStatus("Concluído.");
        Thread.sleep(500);
        changeStatus("Ativando o Windows...");
        Thread.sleep(500);
        new ProcessBuilder("cmd", "/c", "activation.bat").directory(new File("C:\\ImgPosInst\\src\\core\\" + osver)).start();
        if (domainconnected == true)
        {
          changeStatus("Inserindo Máquina ao Domínio...");
          log(null, "INFO","Verificando Hostname atual...");
          ProcessBuilder host = new ProcessBuilder("cmd", "/c", "hostname");
          oldhostname = IOUtils.toString(host.start().getInputStream(), "UTF-8");
          log(null, "INFO","Hostname atribuído pelo Sistema Operacional: " + oldhostname);
          ProcessBuilder sn = new ProcessBuilder("cmd", "/c", "wmic bios get serialnumber");
          hostsuffix = IOUtils.toString(sn.start().getInputStream(), "UTF-8");
          File hostsuffixfile = new File ("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "sn.cfg");
          FileUtils.writeStringToFile(hostsuffixfile, hostsuffix, "UTF-8");
          List<String> hostcontents = FileUtils.readLines(hostsuffixfile, "UTF-8");
          String[] hostarray = new String[hostcontents.size()];
          hostarray = hostcontents.toArray(hostarray);
          for(String serialnumber : hostarray) 
          {
            if ((!serialnumber.isEmpty()) && (!serialnumber.contains("SerialNumber")))
            {
              hostsuffix = serialnumber;
              hostsuffix = hostsuffix.trim();
            }
          }
          log(null, "INFO", "Número de Série: " + hostsuffix);
          if (!"Nenhum".equals(hostprefix)) {hostname = (hostprefix + machinetag + hostsuffix);} else {hostname = "LACTALIS";}
          log(null, "INFO","Hostname: " + hostname);
          new ProcessBuilder("cmd", "/c", "netdom join " + oldhostname + " /Domain:lactalisbra.local /UserD:" + user + " /PasswordD:" + pass + " /REBoot && netdom RENAMECOMPUTER OLDNAME /NewName:" + hostname + " /UserD:" + user + " /PasswordD:" + pass + " /Force /REBoot").start();
          Thread.sleep(15000);
        }
        changeStatus("Concluindo e Reiniciando...");
        Thread.sleep(1000);
        try {FileIOAndLog("excluído", new File ("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "core" + File.separator + "SEP"), null);} catch (IOException ex){}
        if (installdrivers == true) { try {FileIOAndLog("excluído", zipdrvfile, null);} catch (IOException ex){} }
        try {FileIOAndLog("excluído", drvroot, null);} catch (IOException ex){}
        new ProcessBuilder("cmd", "/c", "shutdown -r -t 3").start();
        Thread.sleep(1000);
        try {log(null, "INFO", "Thread1 finalizada");} catch (FileNotFoundException e){}
      }
      catch (InterruptedException x){try {log (x, "ERRO","THREAD INTERROMPIDA");} catch (IOException ex){}} catch (IOException |ZipException ex){}
    }
  }
}
