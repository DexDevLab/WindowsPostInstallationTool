/**
 * Main Class.
 */
package imgposinst;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 07/25/2019
 * @version 4.0.12-20200129-254
 *
 * Application Class. Initializes Controller.
 */
public class ImgPosInst extends Application
{

  /**
   * Corefile containing all info about UI and custom tweaks for the System.
   */
  static File corefile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "core.cfg");
  String version = "";

  /**
   * Método start.
   *
   * @param stage - O estágio principal
   * @throws Exception quando há falha na inicialização do estágio
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    Parent root = FXMLLoader.load(getClass().getResource("ImgPosInstFXML.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    if (corefile.exists())
    {
      ScriptFileReader sfr = new ScriptFileReader();
      sfr.readScript(corefile, true);
      List<String> vers = sfr.searchItemsFromCategory("appVers");
      vers.forEach((line) ->
      {
        version = line;
      });
      stage.setTitle("Ferramenta de Pós-Instalação de Imagem " + version + " - Randstad Technologies do Brasil");
    }
    else
    {
      stage.setTitle("Ferramenta de Pós-Instalação de Imagem ");
    }
    stage.setResizable(false);
    stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
    stage.setAlwaysOnTop(true);
    stage.setOnCloseRequest((e) ->
    {
      FutureTask<String> closerequest = new FutureTask<>(new CloseRequest());
      Platform.runLater(closerequest);
      e.consume();
    });
    stage.show();
  }

  class CloseRequest implements Callable
  {

    @Override
    public CloseRequest call()
    {
      Alert closerequest = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES, ButtonType.NO);
      closerequest.initModality(Modality.APPLICATION_MODAL);
      Stage stage = (Stage) closerequest.getDialogPane().getScene().getWindow();
      stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
      closerequest.setTitle("Fechar");
      closerequest.setHeaderText("");
      closerequest.setContentText("Deseja realmente fechar a Ferramenta de Pós-instalação de Imagem?\n\n"
                                  + "* Ajustes de performance cruciais ao Sistema Operacional não serão realizados\n"
                                  + "* O Windows não será ativado\n"
                                  + "* O antivírus não será instalado\n"
                                  + "* Os drivers não serão instalados\n"
                                  + "* A máquina não será ingressa ao Domínio\n\n"
                                  + "Tem certeza que deseja fazer isso manualmente ao fechar o programa?");
      ((Stage) closerequest.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
      Optional<ButtonType> result = closerequest.showAndWait();
      if (result.get() == ButtonType.YES)
      {
        System.exit(0);
      }
      return null;
    }

  }

  /**
   * Método Main.
   *
   * @param args
   */
  public static void main(String[] args) { launch(args); }
}
