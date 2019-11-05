/**
 * Main interface Class.
 */
package imgposinst;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
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
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;


/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 07/25/2019
 * @version 3.0.0-20191031-32
 *
 * Main Class. Implements interface.
 */
public class ImgPosInstFXMLController implements Initializable
{

  //----------------------------------------------------------------
  // Log generation variables.
  //----------------------------------------------------------------
  /**
   * Set Date/time format for log entry.
   */
  DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  /**
   * Get date/time for log entry.
   */
  String time = df.format(Calendar.getInstance().getTime());
  /**
   * Set Date/time format for log file.
   */
  DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd--HH.mm.ss");
  /**
   * Get date/time for log file.
   */
  String time2 = df2.format(Calendar.getInstance().getTime());
  /**
   * Set log file name based on time/date.
   */
  File logname = new File(time2 + ".txt");
  /**
   * Set log file location.
   */
  File logfile = new File("C:" + File.separator + "ImgPosInstlogs" + File.separator + logname);
  /**
   * Line separator for text concatenation.
   */
  final String line = System.getProperty("line.separator");

  /**
   * Corefile containing all info about UI and custom tweaks for the System.
   */
  File corefile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "core.cfg");
  /**
   * File containing the powershell commands to include machine to Domain.
   */
  File pshellfile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "core.ps1");
  //------------------------------------------------------------------
  // List variables containing corefile entries
  //------------------------------------------------------------------
  List<String> machinetypelist =  new ArrayList<>(); //Machines type List
  List<String> machinelist =  new ArrayList<>(); //Machines model list
  List<String> sitelist =  new ArrayList<>(); //Site list
  List<String> IPs =  new ArrayList<>(); //IP Address list for Domain connection test
  List<String> SEPlist =  new ArrayList<>(); //SEP Antivirus commands list
  List<String> powerplan =  new ArrayList<>(); //Power Plan adjust commands list
  List<String> services =  new ArrayList<>(); //Services tweaks commands list
  List<String> remotecon =  new ArrayList<>(); //Remote Connection adjust list
  List<String> winstore =  new ArrayList<>(); //Windows Store command list
  List<String> performance =  new ArrayList<>(); //General tweaks command list
  List<String> O365 =  new ArrayList<>(); //Office 365 ajusts command list
  List<String> activation =  new ArrayList<>(); //Windows Activation commands

  /**
   * Driver files root directory.
   */
  File drvroot;
  /**
   * Zip file cointaining drivers.
   */
  File zipdrvfile;
  /**
   * String containing AlertBox Message.
   */
  String message;
  /* Confirm if computer has connection to Domain. */
  boolean domainconnected;
  /**
   * Gets OS version.
   */
  String osver;
  /**
   * Gets technician user ID logged.
   */
  String user;
  /**
   * Gets technician password logged.
   */
  String pass;
  /**
   * Get Computer which will run PowerShell remotely (Server).
   */
  String server;
  /**
   * Get Domain name.
   */
  String dom;

  /**
   * Gets computer selected by combobox.
   *
   * @see #comboboxmachine
   */
  String machinevalue;
  /**
   * If machine selected in "machinevalue" is "other", set installdrivers to "false".
   */
  boolean installdrivers = true;
  /**
   * Numeric ID from combobox machine selection.
   *
   * @see #comboboxmachine
   */
  int machineid;
  /**
   * Hostname suffix (aka Serial Number from machine).
   */
  String hostsuffix;
  /**
   * Corporation Site selected in combobox.
   *
   * @see #comboboxsite
   */
  String sitevalue;
  /**
   * Numeric ID from combobox site selection.
   *
   * @see #comboboxsite
   */
  int siteid;
  /**
   * Gets type of computer selected in ComboBox.
   *
   * @see #comboboxmachinetype
   */
  String machinetype;
  /**
   * TAG related to machine type.
   */
  String machinetag;
  /**
   * Hostname prefix (aka depends of the computer site).
   */
  String hostprefix;
  /**
   * Computer hostname.
   */
  String hostname;
  /**
   * Old hostname before changing.
   */
  String oldhostname;

  //----------------------------------------------------------------
  // FXML Variables
  //----------------------------------------------------------------
  /**
   * Object interface. Shows ComboBox for select type of machine/computer.
   */
  @FXML
  private ComboBox<String> comboboxmachinetype;

  /**
   * Object interface. Shows ComboBox for select machine brand/model.
   */
  @FXML
  private ComboBox<String> comboboxmachine;

  /**
   * Object interface. Shows ComboBox for select site (factory, office etc).
   */
  @FXML
  private ComboBox<String> comboboxsite;

  /**
   * Object interface. Shows Label for program status/progress.
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
   * Object interface. Field to type technician username.
   */
  @FXML
  private TextField textfielduser;

  /**
   * Object interface. Field to type technician password.
   */
  @FXML
  private PasswordField passwordfieldpass;

  /**
   * Event for pressing "start" button.
   *
   * @see #buttoniniciar
   * @throws IOException in a common failure
   */
  @FXML
  void acaobuttoniniciar()
  {
    log(null, "INFO", "Botão pressionado");

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
      FutureTask nocredentials = new FutureTask<>(new Dialog());
      Platform.runLater(nocredentials);
      textfielduser.setDisable(false);
      passwordfieldpass.setDisable(false);
      buttoniniciar.setDisable(false);
    }
    else
    {
      log(null, "INFO", "ID do Técnico: " + user);
      log(null, "INFO", "Senha do Técnico: " + pass);
      Thread Thread1 = new Thread(new MainTask());
      Thread1.setDaemon(true);
      Thread1.start();
      log(null, "INFO", "Iniciando Thread1 - MainTask");
    }
  }

  /**
   * Write log in console and in file at same time.
   *
   * @param throwable - exception throwable (if necessary)
   * @param logtype   - "INFO" for common info and "ERRO" for errors
   * @param logmsg    - The message to be written/logged into file
   */
  public void log(Throwable throwable, String logtype, String logmsg)
  {
    time = df.format(Calendar.getInstance().getTime());
    logtype = "[" + logtype + "]: ";
    if (throwable != null)
    {
      logmsg = (line + logmsg + line + getStackTrace(throwable));
    }
    System.out.println(time + logtype + logmsg);
    try
    {
      writeStringToFile(logfile, time + logtype + logmsg + line, "UTF-8", true);
    }
    catch (IOException ex)
    {
      System.out.println("ERRO CRÍTICO AO GERAR O LOG.");
    }
  }

  /**
   * Load a defined Script File and put values into a list, choosing the line ranges.
   *
   * @param lis - the list to write into
   * @param scriptfil - the script file
   * @param startlin  - the first line to read. In file, line 2 means startlin = 2
   * @param endlin    - the last line to read. For single line reading, use in endlin the same value as startlin
   *
   */
  public void loadScript(List lis, File scriptfil, int startlin, int endlin)
  {
    startlin--;
    endlin--;
    while (startlin <= endlin)
    {
      try
      {
        lis.add(Files.readAllLines(scriptfil.toPath()).get(startlin));
      }
      catch (IOException ex)
      {
        log(ex, "ERRO", "EXCEÇÃO EM loadScript(File, int, int): NÃO FOI POSSÍVEL INCLUIR A LINHA " + startlin + " NA LISTA.");
      }
      startlin++;
    }
  }

  /**
   * Perform Process Builder CMD line by a list.
   *
   * @param list - the list to read
   */
  public void runCMD(List<String> list)
  {
    list.forEach((cmdlin) ->
    {
      try
      {
        log(null, "INFO", "Executando comando " + cmdlin);
//        new ProcessBuilder("cmd", "/c", cmdlin).start();
        Process p = Runtime.getRuntime().exec("cmd /c " + cmdlin);
        int exit = p.exitValue();
        if (exit == 0)
        {
          log(null, "INFO", "OK (" + exit + ")");
        }
        else
        {
          log(null, "ERRO", "FALHA DURANTE A EXECUÇÃO DO COMANDO (" + exit + ").");
        }
        Thread.sleep(500);
      }
      catch (IOException ex)
      {
        log(ex, "ERRO", "EXCEÇÃO EM runCMD(String<List>) - COMANDO " + cmdlin + " INVÁLIDO OU FALHA CRÍTICA DO CMD.");
      }
      catch (InterruptedException ex2)
      {
        log(ex2, "ERRO", "EXCEÇÃO EM runCMD(String<List>) - THREAD INTERROMPIDA DE MANEIRA INESPERADA.");
      }
    });
  }

  /**
   * Move, copy, or delete a File object, writing to a log.
   *
   * @param operation   - "copiado" for copy, "movido" for move, "excluído" for delete.
   * @param source      - File or source
   * @param destination - File or destination. In delete, use "null" for definition
   *
   * @throws IOException when file does not exist
   */
  public void FileIOAndLog(String operation, File source, File destination) throws IOException
  {
    String a;
    if (source.isDirectory())
    {
      a = "Diretório";
    }
    else
    {
      a = "Arquivo";
    }
    if (source.exists())
    {
      if (("excluído".equals(operation)) && (destination == null))
      {
        if (source.exists())
        {
          log(null, "INFO", a + " \"" + source.toString() + " encontrado(a)");
          FileUtils.deleteQuietly(source);
          if (!source.exists())
          {
            log(null, "INFO", a + " \"" + source.toString() + " " + operation);
          }
        }
        else if (!source.exists())
        {
          log(null, "INFO", a + " \"" + source.toString() + "\" não foi encontrado(a)");
        }
        if (source.exists())
        {
          log(null, "ERRO", a + " \"" + source.toString() + "\" AINDA EXISTE");
        }
      }
      else
      {
        if ("copiado".equals(operation))
        {
          if (source.isDirectory())
          {
            FileUtils.copyDirectory(source, destination);
          }
          else
          {
            if (destination.isDirectory())
            {
              FileUtils.copyFileToDirectory(source, destination);
            }
            else
            {
              FileUtils.copyFile(source, destination);
            }
          }
        }
        else if ("movido".equals(operation))
        {
          if (source.isDirectory())
          {
            moveDirectoryToDirectory(source, destination, true);
          }
          else
          {
            moveFile(source, destination);
          }
        }
        log(null, "INFO", a + " \"" + source.toString() + "\" " + operation + " para \"" + destination.toString() + "\"");
      }
    }
    else
    {
      log(null, "ERRO", a + " \"" + source.toString() + "\" NÃO FOI ENCONTRADO.");
    }
  }

  /**
   * Initialization method.
   *
   * @param url sem informação.
   * @param rb sem informação.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    log(null, "INFO", "Logger inicializado. Janela aberta.");
    log(null, "INFO", "Checando Sistema Operacional...");
    ProcessBuilder os = new ProcessBuilder("cmd", "/c", "ver");
    String osget = "";
    try
    {
      osget = IOUtils.toString(os.start().getInputStream(), StandardCharsets.UTF_8);
    }
    catch (IOException ex)
    {
      log(ex, "ERRO", "EXCEÇÃO EM initialize(URL, ResourceBundle) - NÃO FOI POSSÍVEL VERIFICAR O SISTEMA OPERACIONAL.");
      Platform.exit();
    }
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
    if (!corefile.exists())
    {
      log(null, "ERRO", "COREFILE NÃO ENCONTRADO. A FERRAMENTA PÓS-INSTALAÇÃO NÃO IRÁ FUNCIONAR CORRETAMENTE!");
      Platform.exit();
    }
    else
    {
      log(null, "INFO", "Registrando variáveis de acordo com o Corefile...");
      log(null, "INFO", "Carregando lista de tipos de equipamento...");
      loadScript(machinetypelist, corefile, 70, 71);
      log(null, "INFO", "Carregando lista de modelos de equipamento...");
      loadScript(machinelist, corefile, 75, 83);
      log(null, "INFO", "Carregando lista de Unidades Lactalis...");
      loadScript(sitelist, corefile, 14, 65);
      log(null, "INFO", "Carregando lista de Endereços IP para teste de rede...");
      loadScript(IPs, corefile, 579, 579);
      log(null, "INFO", "Carregando lista de comandos SEP...");
      loadScript(SEPlist, corefile, 574, 574);
      log(null, "INFO", "Carregando lista de comandos de ajuste de Perfil de Energia...");
      if ("W7E".equals(osver))
      {
        loadScript(powerplan, corefile, 146, 203);
      }
      else if ("WXE".equals(osver))
      {
        loadScript(powerplan, corefile, 132, 141);
      }
      log(null, "INFO", "Carregando lista de comandos de ajustes dos Serviços do Windows...");
      if ("W7E".equals(osver))
      {
        loadScript(services, corefile, 417, 564);
      }
      else if ("WXE".equals(osver))
      {
        loadScript(services, corefile, 213, 412);
      }
      log(null, "INFO", "Carregando lista de comandos de ajustes para Conexão remota...");
      loadScript(remotecon, corefile, 208, 208);
      log(null, "INFO", "Carregando lista de comandos de ajustes para a Windows Store...");
      loadScript(winstore, corefile, 569, 569);
      log(null, "INFO", "Carregando lista de comandos de ajustes de performance...");
      if ("W7E".equals(osver))
      {
        loadScript(performance, corefile, 120, 127);
      }
      else if ("WXE".equals(osver))
      {
        loadScript(performance, corefile, 108, 115);
      }
      log(null, "INFO", "Carregando lista de comandos de ajustes do Office 365...");
      loadScript(O365, corefile, 98, 103);
      log(null, "INFO", "Carregando lista de comandos de ativação do Windows..");
      if ("W7E".equals(osver))
      {
        loadScript(activation, corefile, 93, 93);
      }
      else if ("WXE".equals(osver))
      {
        loadScript(activation, corefile, 88, 88);
      }
      log(null, "INFO", "Carregando informações do Domínio...");
      // Keep in mind that Files.readAllLines() reads first line as 0
      try
      {
        server = Files.readAllLines(corefile.toPath()).get(588);
        dom = Files.readAllLines(corefile.toPath()).get(583);
      }
      catch (IOException ex)
      {
        log(ex, "ERRO", "EXCEÇÃO EM initialize(URL, ResourceBundle): NÃO FOI POSSÍVEL RECEBER AS INFORMAÇÕES DE DOMÍNIO DO ARQUIVO COREFILE.");
      }
    }

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

    comboboxmachinetype.getItems().addAll(machinetypelist);

    comboboxmachinetype.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
      machinetype = comboboxmachinetype.getItems().get((int) new_value);
      log(null, "INFO", "Tipo de Equipamento \"" + machinetype + "\" de ID " + new_value.intValue() + " selecionado");
      if (new_value.intValue() == 0)
      {
        machinetag = "D";
      }
      else if (new_value.intValue() == 1)
      {
        machinetag = "N";
      }
      comboboxmachine.setDisable(false);
    });

    comboboxmachine.getItems().addAll(machinelist);
    comboboxmachine.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
      machinevalue = comboboxmachine.getItems().get((int) new_value);
      log(null, "INFO", "Equipamento \"" + machinevalue + "\" de ID " + new_value.intValue() + " selecionado");
      if ((new_value.intValue()) == 0)
      {
        installdrivers = false;
      }
      comboboxsite.setDisable(false);
    });

    comboboxsite.getItems().addAll(sitelist);
    comboboxsite.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
      sitevalue = comboboxsite.getItems().get((int) new_value);
      if (sitevalue.contains("("))
      {
        int startIndex1 = sitevalue.indexOf("(");
        int endIndex1 = sitevalue.indexOf(")");
        hostprefix = sitevalue.substring(startIndex1 + 1, endIndex1);
        {
          log(null, "INFO", "Prefixo da Unidade/Site: " + hostprefix);
        }
      }
      else
      {
        hostprefix = "Nenhum";
      }
      log(null, "INFO", "Unidade \"" + sitevalue + "\" de ID " + new_value.intValue() + " selecionada");
      buttoniniciar.setDisable(false);
      textfielduser.setDisable(false);
      passwordfieldpass.setDisable(false);
    });

    log(null, "INFO", "Checando conexão com o Domínio...");
    IPs.forEach((list) ->
    {
      try
      {
        domainconnected = testConnect(list);
      }
      catch (IOException ex)
      {
        log(ex, "ERRO", "EXCEÇÃO EM initialize(URL, ResourceBundle) - NÃO FOI POSSÍVEL REALIZAR O TESTE DE REDE COM O ENDEREÇO " + list);
      }
    });
    if (!domainconnected)
    {
      message = "nonetwork";
      log(null, "ERRO", "NÃO HÁ CONEXÃO DE REDE OU O DOMÍNIO NÃO PODE SER ACESSADO.");
      FutureTask nonetwork = new FutureTask<>(new Dialog());
      Platform.runLater(nonetwork);
    }
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
      log(null, "INFO", text);
    });
  }

  /**
   * Test a network connection using IP verification in 5s timeout.
   *
   * @param ip - IP for queue
   *
   * @throws UnknownHostException when the address can not be reached
   * @return false when there is not connection with IP
   */
  public boolean testConnect(String ip) throws IOException
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


  /**
   * Common generic Alert Box.
   */
  class Dialog implements Callable
  {

    @Override
    public Dialog call()
    {
      Alert dialog = new Alert(Alert.AlertType.ERROR);
      dialog.initModality(Modality.APPLICATION_MODAL);
      Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
      stage.setAlwaysOnTop(true);
      stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
      stage.setOnCloseRequest(Event::consume);
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
   * Main Thread triggered when "iniciar" button is pressed.
   *
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
        runCMD(SEPlist);
        changeStatus("Ajustando perfil de Energia...");
        runCMD(powerplan);
        changeStatus("Configurando Serviços...");
        runCMD(services);
        changeStatus("Ajustando Configurações de Acesso Remoto...");
        runCMD(remotecon);
        if ("WXE".equals(osver))
        {
          changeStatus("Desativando a Windows Store...");
          runCMD(winstore);
        }
        changeStatus("Realizando ajustes de performance...");
        runCMD(performance);
        changeStatus("Configurando o Office 365...");
        runCMD(O365);
        drvroot = new File("C:" + File.separator + "ImgPosInst" + File.separator + osver + File.separator + "DRV");
        if (installdrivers)
        {
          changeStatus("Listando drivers a serem instalados...");
          machinevalue = machinevalue.replaceAll("\\s+", "");
          log(null, "INFO", "Diretório de driver selecionado: " + machinevalue);
          File drvdir = new File(drvroot + File.separator + machinevalue);
          zipdrvfile = new File(drvdir + File.separator + "zip.zip");
          ZipFile zipdrv = new ZipFile(drvdir + File.separator + "zip.zip");
          changeStatus("Descompactando os arquivos necessários...");
          zipdrv.extractAll(drvdir.toString());
          log(null, "INFO", "Arquivo de pacote de driver extraído");
          changeStatus("Iniciando instalação de driver. Aguarde...");
          try
          {
            FileIOAndLog("copiado", new File(drvroot + File.separator + "DRV" + File.separator + "pnputil.exe"), new File(drvdir + File.separator + "pnputil.exe"));
            FileIOAndLog("copiado", new File(drvroot + File.separator + "DRV" + File.separator + "drv.bat"), new File(drvdir + File.separator + "drv.bat"));
          }
          catch (IOException ignored){}
          Process p = Runtime.getRuntime().exec("START /WAIT CMD /C '" + drvdir.toString() + "\\drv.bat >NUL'");
          int exitp = p.exitValue();
          p.waitFor();
          Process q = Runtime.getRuntime().exec("START /WAIT '" + drvdir.toString() + "\\video\\igxpin.exe -s'");
          int exitq = q.exitValue();
          q.waitFor();
          if ((exitp + exitq) != 0)
          {
            log(null, "ERRO", "FALHA DURANTE A EXECUÇÃO DO COMANDO DE INSTALAÇÃO DE DRIVERS.");
            log(null, "ERRO", "DRV.BAT RETORNOU CÓDIGO " + exitp);
            log(null, "ERRO", "IGXPIN.EXE RETORNOU CÓDIGO " + exitq);
          }
          else
          {
            log(null, "INFO", "Concluído.");
          }
          Thread.sleep(500);
//          new ProcessBuilder("cmd", "/c", "drv.bat >NUL").directory(drvdir).start();
//          Thread.sleep(180000);
//          new ProcessBuilder("cmd", "/c", "video\\igxpin.exe -s").directory(drvdir).start();
//          Thread.sleep(180000);
          log(null, "INFO", "Comando recursivo concluído");
        }
        Thread.sleep(500);
        changeStatus("Ativando o Windows...");
        runCMD(activation);
        if (domainconnected)
        {
          log(null, "INFO", "Verificando Hostname atual...");
          ProcessBuilder host = new ProcessBuilder("cmd", "/c", "hostname");
          oldhostname = IOUtils.toString(host.start().getInputStream(), StandardCharsets.UTF_8);
          log(null, "INFO", "Hostname atribuído pelo Sistema Operacional: " + oldhostname);
          ProcessBuilder sn = new ProcessBuilder("cmd", "/c", "wmic bios get serialnumber");
          hostsuffix = IOUtils.toString(sn.start().getInputStream(), StandardCharsets.UTF_8);
          File hostsuffixfile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "sn.exp");
          FileUtils.writeStringToFile(hostsuffixfile, hostsuffix, "UTF-8");
          List<String> hostcontents = FileUtils.readLines(hostsuffixfile, "UTF-8");
          String[] hostarray = new String[hostcontents.size()];
          hostarray = hostcontents.toArray(hostarray);
          for (String serialnumber : hostarray)
          {
            if ((!serialnumber.isEmpty()) && (!serialnumber.contains("SerialNumber")))
            {
              hostsuffix = serialnumber;
              hostsuffix = hostsuffix.trim();
            }
          }
          log(null, "INFO", "Número de Série: " + hostsuffix);
          if (!"Nenhum".equals(hostprefix))
          {
            hostname = (hostprefix + machinetag + hostsuffix);
          }
          else
          {
            hostname = "LACTALIS";
          }
          log(null, "INFO", "Hostname: " + hostname);
          changeStatus("Inserindo Máquina ao Domínio...");
          List<String> pshell = new ArrayList<>();
          pshell.add("$Server = '" + server + "'");
          pshell.add("$Dom = '" + dom + "'");
          pshell.add("$Hostname = '" + hostname + "'");
          pshell.add("$Username = '" + user + "'");
          pshell.add("$Password = '" + pass + "'");
          try (FileWriter fw = new FileWriter(pshellfile))
          {
            pshell.forEach((item) ->
            {
              try
              {
                fw.write(item + "\n");
                log(null, "INFO", "Atributo de Domínio: " + item);
              }
              catch (IOException ex)
              {
                log(ex, "ERRO", "NÃO FOI POSSÍVEL INSERIR A INFORMAÇÃO " + item + " NO SCRIPT POWERSHELL.");
              }
            });
          }
          Process p = Runtime.getRuntime().exec("cmd /c powershell.exe 'C:\\ImgPosInst\\core.ps1'");
          int exit = p.exitValue();
          if (exit == 0)
          {
            log(null, "INFO", "O Script PowerShell retornou " + exit + ", indicando sucesso.");
          }
          else
          {
            log(null, "ERRO", "FALHA DESCONHECIDA DURANTE A EXECUÇÃO DO SCRIPT POWERSHELL.");
          }
        }
        changeStatus("Concluindo e Reiniciando...");
        new ProcessBuilder("cmd", "/c", "shutdown -r -t 5").start();
        Thread.sleep(1000);
        log(null, "INFO", "Thread1 finalizada");
      }
      catch (InterruptedException x)
      {
        log(x, "ERRO", "THREAD INTERROMPIDA");
      }
      catch (IOException | ZipException ignored) {}
    }

  }

}
