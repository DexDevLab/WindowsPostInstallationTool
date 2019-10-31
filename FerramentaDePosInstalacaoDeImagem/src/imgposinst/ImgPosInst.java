/**
 * Main Class.
 */
package imgposinst;

import java.io.File;
import java.nio.file.Files;
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
import javax.swing.JOptionPane;

/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 27/05/2019
 * @version 3.0.0-20191031-15
 *
 * Main Class. Initializes Controller.
 */
public class ImgPosInst extends Application
{

  /**
   * Corefile containing all info about UI and custom tweaks for the System.
   */
  static File corefile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "core.cfg");
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
      String version = Files.readAllLines(corefile.toPath()).get(593);
      stage.setTitle("Ferramenta de Pós-Instalação de Imagem " + version);
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
    });
    stage.show();
  }


  class CloseRequest implements Callable
  {

    @Override
    public CloseRequest call()
    {
      Alert closerequest = new Alert(Alert.AlertType.CONFIRMATION);
      closerequest.initModality(Modality.APPLICATION_MODAL);
      Stage stage = (Stage) closerequest.getDialogPane().getScene().getWindow();
      stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
      stage.setOnCloseRequest((e) -> {e.consume();});
      closerequest.getButtonTypes().clear();
      closerequest.setTitle("Fechar");
      closerequest.setHeaderText("");
      closerequest.setContentText("Deseja realmente fechar a Ferramenta de Pós-instalação de Imagem?\n\n"
                                  + "* Ajustes de performance cruciais ao Sistema Operacional não serão realizados\n"
                                  + "* O Windows não será ativado\n"
                                  + "* O antivírus não será instalado\n"
                                  + "* Os drivers não serão instalados\n"
                                  + "* A máquina não será ingressa ao Domínio\n\n"
                                  + "Tem certeza que deseja fazer isso manualmente ao fechar o programa?");
      ButtonType btnsim1 = new ButtonType("Sim");
      closerequest.getButtonTypes().add(btnsim1);
      ButtonType btnnao1 = new ButtonType("Não");
      closerequest.getButtonTypes().add(btnnao1);
      ((Stage) closerequest.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
      Optional<ButtonType> result = closerequest.showAndWait();
      if (result.get() == btnsim1)
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
  public static void main(String[] args)
  {
    while (!corefile.exists())
    {
      JOptionPane.showOptionDialog
        (null,
          "Os arquivos de recurso necessários para "
            + "a Ferramenta de Pós-instalação de "
            + "Imagem não foram encontrados em "
            + "\"C:\\ImgPosInst\". \n"
            + "Copie o diretório \"ImgPosInst\" "
            + "para a Unidade C: do Sistema e clique "
            + "em \"OK\".",
          "Recurso não encontrado",
          JOptionPane.DEFAULT_OPTION,
          JOptionPane.INFORMATION_MESSAGE,
          null,
          null,
        null);
    }
    launch(args);
  }

}
