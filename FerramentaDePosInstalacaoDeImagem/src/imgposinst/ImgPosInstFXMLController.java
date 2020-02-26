/**
 * Main interface Class.
 */
package imgposinst;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
 *
 * Initializable Class. Implements interface.
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
  List<String> domlist =  new ArrayList<>(); //Domain list obj to retrieve from method

  /**
   * Driver files root directories.
   */
  File drvroot;
  File drvdir;

  /**
   * Zip file cointaining drivers.
   */
  File zipdrvfile;
  ZipFile zipdrv;

  /**
   * String containing AlertBox Message.
   */
//  String message;
  /* Confirm if computer has connection to Domain. */
  boolean domainconnected;

  /**
   * Dialog boxes sinalizers;
   */
  boolean dialogDone = false;
  boolean _restartAlertDone = false;
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
  boolean installdrivers;
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
   * Object interface. Shows Label for detailed activity.
   */
  @FXML
  private Label labelstatus3;

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
  private TextField textfieldpass;

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
    MainJob task = new MainJob();

    labelstatus.textProperty().bind(task.titleProperty());
    labelstatus3.textProperty().bind(task.messageProperty());

    buttoniniciar.setDisable(true);
    comboboxsite.setDisable(true);
    comboboxmachine.setDisable(true);
    comboboxmachinetype.setDisable(true);
    progressbar.setVisible(true);
    labelstatus.setVisible(true);
    labelstatus2.setVisible(true);
    labelstatus3.setVisible(true);
    textfielduser.setDisable(true);
    textfieldpass.setDisable(true);

    user = textfielduser.getText();
    pass = textfieldpass.getText();

    if (user.isEmpty() | pass.isEmpty())
    {
      log(null, "ERRO", "USUÁRIO OU SENHA NULOS OU INVÁLIDOS.");
      dialogMessage("nocredentials");

      textfielduser.setDisable(false);
      textfieldpass.setDisable(false);
      buttoniniciar.setDisable(false);
    }
    else
    {
      log(null, "INFO", "ID do Técnico: " + user);
      log(null, "INFO", "Senha do Técnico: " + pass);
      new Thread(task).start();
      log(null, "INFO", "Iniciando MainTask");
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
    catch (IOException IGNORED)
    {
      System.out.println("ERRO CRÍTICO AO GERAR O LOG.");
    }
  }

  /**
   * Load a defined Script File and put values into a list.
   *
   * @param category - the keyword to find as category
   * @return lis - the output list
   *
   */
  public List<String> loadScript(String category)
  {
    ScriptFileReader sfr = new ScriptFileReader();
    sfr.readScript(corefile, true);
    List<String> lis = new ArrayList<>();
    lis = sfr.searchItemsFromCategory(category);
    return lis;
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
      Platform.runLater(() -> { new Exceptions().treatException(ex);});
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
      machinetypelist = loadScript("machineTypeList");
      log(null, "INFO", "Carregando lista de modelos de equipamento...");
      machinelist = loadScript("machineList");
      log(null, "INFO", "Carregando lista de Unidades Lactalis...");
      sitelist = loadScript("siteList");
      log(null, "INFO", "Carregando lista de Endereços IP para teste de rede...");
      IPs = loadScript("iPs");
      log(null, "INFO", "Carregando lista de comandos SEP...");
      SEPlist = loadScript("sEP");
      log(null, "INFO", "Carregando lista de comandos de ajuste de Perfil de Energia...");
      if ("W7E".equals(osver))
      {
        powerplan = loadScript("w7EPowerPlan");
      }
      else if ("WXE".equals(osver))
      {
        powerplan = loadScript("wXEPowerPlan");
      }
      log(null, "INFO", "Carregando lista de comandos de ajustes dos Serviços do Windows...");
      if ("W7E".equals(osver))
      {
        services = loadScript("w7EServices");
      }
      else if ("WXE".equals(osver))
      {
        services = loadScript("wXEServices");
      }
      log(null, "INFO", "Carregando lista de comandos de ajustes para Conexão remota...");
      remotecon = loadScript("remoteCon");
      log(null, "INFO", "Carregando lista de comandos de ajustes para a Windows Store...");
      winstore = loadScript("winstore");
      log(null, "INFO", "Carregando lista de comandos de ajustes de performance...");
      if ("W7E".equals(osver))
      {
        performance = loadScript("w7EPerformance");
      }
      else if ("WXE".equals(osver))
      {
        performance = loadScript("wXEPerformance");
      }
      log(null, "INFO", "Carregando lista de comandos de ajustes do Office 365...");
        O365 = loadScript("o365Config");
      log(null, "INFO", "Carregando lista de comandos de ativação do Windows..");
      if ("W7E".equals(osver))
      {
        activation = loadScript("w7EActivation");
      }
      else if ("WXE".equals(osver))
      {
        activation = loadScript("wXEActivation");
      }
      log(null, "INFO", "Carregando informações do Domínio...");
      domlist = loadScript("domain");
      domlist.forEach((line2) ->
      {
        dom = line2;
      });
      log(null, "INFO", "Todas as variáveis foram registradas com sucesso.");
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
    textfieldpass.setTooltip(new Tooltip("Insira a senha do técnico autorizado."));

    comboboxmachinetype.getItems().addAll(machinetypelist);

    comboboxmachinetype.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) ->
    {
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
    comboboxmachine.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) ->
    {
      machinevalue = comboboxmachine.getItems().get((int) new_value);
      log(null, "INFO", "Equipamento \"" + machinevalue + "\" de ID " + new_value.intValue() + " selecionado");
      installdrivers = (new_value.intValue()) != 0;
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
      textfieldpass.setDisable(false);
    });
  }

  /**
   * Check if machine can connect to Domain.
   */
  public void checkDomain()
  {
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
        Platform.runLater(() -> { new Exceptions().treatException(ex);});
      }
    });
    if (!domainconnected)
    {
      log(null, "ERRO", "NÃO HÁ CONEXÃO DE REDE OU O DOMÍNIO NÃO PODE SER ACESSADO.");
      dialogMessage("nonetwork");
    }
    else
    {
      dialogDone = true;
    }
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

  public void replaceInFile(File fil, String oldString, String newString)
  {
    String oldContent;
    String newContent;
    try
    {
      oldContent = FileUtils.readFileToString(fil, "UTF-8");
      newContent = oldContent.replaceAll(oldString, newString);
      FileUtils.write(fil, newContent, "UTF-8");
    }
    catch (IOException ex)
    {
      log(ex, "ERRO", "NÃO FOI POSSÍVEL ALTERAR O TEXTO.");
      Platform.runLater(() -> { new Exceptions().treatException(ex);});
    }
  }

  public void dialogMessage(String message)
  {

    Alert dialog = new Alert(Alert.AlertType.ERROR,"",ButtonType.OK);
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
    labelstatus3.setVisible(false);
    progressbar.setVisible(false);
    Optional<ButtonType> result = dialog.showAndWait();
    if (result.get() == ButtonType.OK)
    {
      dialogDone = true;
    }
  }

  class Ask2Restart implements Callable
  {

    @Override
    public Ask2Restart call()
    {
      Alert alrt = new Alert(Alert.AlertType.WARNING," ", ButtonType.YES, ButtonType.NO);
      alrt.initModality(Modality.APPLICATION_MODAL);
      Stage stage = (Stage) alrt.getDialogPane().getScene().getWindow();
      stage.getIcons().add(new Image(ImgPosInstFXMLController.class.getResourceAsStream("icon1.jpg")));
      alrt.setTitle("Procedimento Concluído");
      alrt.setHeaderText("");
      alrt.setContentText("Todos os procedimentos foram concluídos.\n"
                                  + "Verfique se o antivírus foi instalado corretamente antes de confirmar.\n\n"
                                  + "Deseja reiniciar o computador agora?");
      ((Stage) alrt.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
      Optional<ButtonType> result = alrt.showAndWait();
      if (result.get() == ButtonType.YES)
      {
        try
        {
          new ProcessBuilder("cmd", "/c", "shutdown -r -t 3").start();
        }
        catch (IOException ex)
        {
          log(ex, "ERRO", "ERRO AO INICIALIZAR O COMANDO DE REBOOT DA MÁQUINA");
          Platform.runLater(() -> { new Exceptions().treatException(ex);});
        }
        System.exit(0);
      }
      return null;
    }

  }

  public class MainJob extends Task<Integer>
  {
    /**
     * Chama o método principal da Classe.
     * @see MainJob
     * @see #versionCheck()
     */
    @Override
    protected Integer call() throws Exception
    {
      //  Chama o método que faz a verificação da versão e baixa/instala/aplica.
      mainStep();
      //  Código de retorno da Task.
      return 1;
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
         updateTitle(text);
       });
       log(null, "INFO", text);
     }

    public boolean isProcessOpen(String processName)
    {
      boolean output = false;
      try
      {
        String line;
        String _isRunning = "NÃO está executando!";
        Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
        try (BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream())))
        {
          while ((line = input.readLine()) != null)
          {
            if (line.contains(processName))
            {
              _isRunning = "está executando.";
              output = true;
            }
          }
        }
        log(null, "INFO", "O processo " + processName + " " + _isRunning);
      }
      catch (IOException ex)
      {
        log(ex, "ERRO", "FALHA NA EXECUÇÃO DO MÉTODO.");
        Platform.runLater(() -> { new Exceptions().treatException(ex);});
      }
      return output;
    }

    /**
     * Perform Process Builder CMD line by a list.
     *
     * @param list - the list to read
     */
    public void runCMD(List<String> list)
    {
      CMDExec runtime = new CMDExec();
      runtime.setList(list);
      Thread cmdExec = new Thread(runtime);
      cmdExec.setDaemon(true);
      cmdExec.start();
      while(cmdExec.isAlive()){}
    }


    class CMDExec implements Runnable
    {

      List<String> listForRun =  new ArrayList<>();

      private void setList(List<String> listToGive)
      {
        this.listForRun = listToGive;
      }

      private List<String> getList()
      {
        return this.listForRun;
      }

      @Override
      public void run()
      {
        List<String> itemList = this.getList();
        itemList.forEach((cmdlin) ->
        {
          try
          {
            log(null, "INFO", "Executando comando " + cmdlin);
            Platform.runLater(() ->
            {
              updateMessage("Executando " + cmdlin);
            });
            Process p = Runtime.getRuntime().exec("cmd /c " + cmdlin);
            p.waitFor();
            int exit = p.exitValue();
            if (exit == 0)
            {
              log(null, "INFO", "OK (" + exit + ")");
            }
            else
            {
              log(null, "ERRO", "FALHA DURANTE A EXECUÇÃO DO COMANDO (" + exit + ").");
            }
            Thread.sleep(200);
          }
          catch (IOException ex)
          {
            log(ex, "ERRO", "EXCEÇÃO EM runCMD(String<List>) - COMANDO " + cmdlin + " INVÁLIDO OU FALHA CRÍTICA DO CMD.");
            Platform.runLater(() -> { new Exceptions().treatException(ex);});
          }
          catch (InterruptedException x)
          {
            log(x, "ERRO", "THREAD INTERROMPIDA.");
            Platform.runLater(() -> { new Exceptions().treatException(x);});
          }
        });
        Platform.runLater(() ->
        {
          updateMessage("");
        });
      }
    }

    class Extract implements Runnable
    {
      @Override
      public void run()
      {
        try
        {
          zipdrv.extractAll(drvdir.toString());
        }
        catch (ZipException ex)
        {
          log(ex, "ERRO", "EXCEÇÃO AO EXTRAIR O ARQUIVO ZIP DOS DRIVERS.");
          Platform.runLater(() -> { new Exceptions().treatException(ex);});
        }
      }
    }

    class DrvInstall implements Runnable
    {

      @Override
      public void run()
      {
        ProcessBuilder pb = new ProcessBuilder("C:/ImgPosInst/" + osver + "/DRV/" + machinevalue + "/drv.bat");
        pb.redirectError();
        try
        {
          Process p = pb.start();
          Platform.runLater(() ->
          {
            updateMessage("Instalando drivers...");
          });
          log(null, "INFO", "Iniciando leitura de drv.bat...");
          try (InputStream inputStream = p.getInputStream())
          {
            int in = -1;
            List<String> out = new ArrayList<>();
            while ((in = inputStream.read()) != -1)
            {
              String outCheck = Character.toString((char)in);
              if(" ".equals(outCheck))
              {
                String str = String.join(",", out);
                String strnew = str.replaceAll("(?m)^ +$", "").replaceAll(",", "");
                log(null, "INFO", strnew);
                out.clear();
              }
              else
              {
                out.add(outCheck);
              }
            }
          }
        log(null, "INFO", "drv.bat concluído com sucesso.");
        }
        catch (IOException ex)
        {
          log(ex, "ERRO", "FALHA NA EXECUÇÃO DO COMANDO DE INSTALAÇÃO DE DRIVER.");
          Platform.runLater(() -> { new Exceptions().treatException(ex);});
        }
      }
    }

    public void mainStep()
    {
      try
      {
        Platform.runLater(() -> { checkDomain();});
        while(!dialogDone){Thread.sleep(100);}
        Platform.runLater(() ->
        {
          progressbar.setVisible(true);
          labelstatus.setVisible(true);
          labelstatus2.setVisible(true);
          labelstatus3.setVisible(true);
        });
        Thread.sleep(500);
        changeStatus("Iniciando...");
        Thread.sleep(500);
        if (!isProcessOpen("sepWscSvc64.exe"))
        {
          changeStatus("Instalando o Symantec Endpoint Protection. Aguarde...");
          runCMD(SEPlist);
          while(!isProcessOpen("sepWscSvc64.exe"))
          {
            Thread.sleep(1000);
            changeStatus("Validando a Instalação do Antivírus. Aguarde...");
          }
        }
        else
        {
          log(null, "INFO", "SEP detectado na máquina.");
        }
        Thread.sleep(500);
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
        if (installdrivers == true)
        {
          changeStatus("Listando drivers a serem instalados...");
          machinevalue = machinevalue.replaceAll("\\s+", "");
          log(null, "INFO", "Diretório de driver selecionado: " + machinevalue);
          drvdir = new File(drvroot + File.separator + machinevalue);
          zipdrvfile = new File(drvdir + File.separator + "zip.zip");
          zipdrv = new ZipFile(drvdir + File.separator + "zip.zip");
          changeStatus("Descompactando os arquivos de driver...");
          Thread extract = new Thread(new Extract());
          extract.setDaemon(true);
          extract.start();
          while(extract.isAlive()){}
          log(null, "INFO", "Arquivo de pacote de driver extraído");
          FileIOAndLog("copiado", new File(drvroot + File.separator+ "pnputil.exe"), new File(drvdir + File.separator + "pnputil.exe"));
          FileIOAndLog("copiado", new File(drvroot + File.separator + "drv.bat"), new File(drvdir + File.separator + "drv.bat"));
          Thread drvInstall = new Thread(new DrvInstall());
          drvInstall.setDaemon(true);
          drvInstall.start();
          log(null, "INFO", "Thread drvInstall inicializada");
          changeStatus("Iniciando instalação de driver. Aguarde...");
          while (drvInstall.isAlive()){Thread.sleep(100);}
          log(null, "INFO", "Comando recursivo concluído");
          Platform.runLater(() ->
          {
            updateMessage("");
          });
        }
        Thread.sleep(500);
        changeStatus("Ativando o Windows...");
        runCMD(activation);
        if ((domainconnected) && (!"Nenhum".equals(hostprefix)))
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
          hostname = (hostprefix + machinetag + hostsuffix);
          log(null, "INFO", "Hostname: " + hostname);
          changeStatus("Alterando Hostname e Inserindo Máquina ao Domínio...");
          replaceInFile(pshellfile, "= \"HOSTNAME\"", "= \"" + hostname + "\"");
          replaceInFile(pshellfile, "= \"DOMAIN\"", "= \"" + dom + "\"");
          replaceInFile(pshellfile, "= \"USER\"", "= \"" + user + "\"");
          replaceInFile(pshellfile, "= \"PASSWORD\"", "= \"" + pass + "\"");
          new ProcessBuilder("C:/ImgPosInst/joindomain.bat").start().waitFor();
          Thread.sleep(10000);
        }
        changeStatus("Concluindo...");
        Platform.runLater(() ->
        {
          updateMessage("");
        });
        Thread.sleep(5000);
        if (installdrivers == true)
        {
          FileIOAndLog("copiado", new File(drvroot + File.separator+ "igxpin.bat"), new File(drvdir + File.separator + "video" + File.separator + "igxpin.bat"));
          new ProcessBuilder("C:/ImgPosInst/" + osver + "/DRV/" + machinevalue + "/video/igxpin.bat").start().waitFor();
          log(null, "INFO", "Driver de vídeo instalado com sucesso");
        }
        FutureTask<String> ask2restart = new FutureTask<>(new Ask2Restart());
        Platform.runLater(ask2restart);
        while(!ask2restart.isDone())
        {
          Thread.sleep(1000);
        }
        log(null, "INFO", "Fechando o programa...");
        System.exit(0);
      }
      catch (InterruptedException x)
      {
        log(x, "ERRO", "THREAD INTERROMPIDA");
        Platform.runLater(() -> { new Exceptions().treatException(x);});
      }
      catch (IOException | ZipException ax)
      {
        log(ax, "ERRO", "ARQUIVO ZIP NÃO ENCONTRADO OU NÃO FOI POSSÍVEL EXTRAIR SEU CONTEÚDO.");
        Platform.runLater(() -> { new Exceptions().treatException(ax);});
      }
    }
  }
}