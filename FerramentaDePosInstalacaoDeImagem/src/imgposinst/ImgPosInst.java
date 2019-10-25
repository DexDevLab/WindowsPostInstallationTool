/**
 * Main Class.
 */
package imgposinst;

import java.io.File;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 27/05/2019
 * @version 2.0.0-20191024-9
 *
 * Main Class. Initializes Controller.
 */
public class ImgPosInst extends Application
{

  /**
   * Corefile containing all info about UI and custom tweaks for the System.
   */
  File corefile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "src" + File.separator + "core" + File.separator + "core.cfg");

  /**
   * Método start.
   *
   * @param stage - O estágio principal
   * @throws Exception quando há falha na inicialização do estágio
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    String version = Files.readAllLines(corefile.toPath()).get(574);
    Parent root = FXMLLoader.load(getClass().getResource("ImgPosInstFXML.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Ferramenta de Pós-Instalação de Imagem " + version);
    stage.setResizable(false);
    stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
    stage.setAlwaysOnTop(true);
    stage.setOnCloseRequest((e) ->
    {
      e.consume();
    });
    stage.show();
  }

  /**
   * Método Main.
   *
   * @param args
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
