/**
 * Main Class.
 */
package imgposinst;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 27/05/2019
 * @version 1.0.1-20190828-8
 * 
 * Main Class. Initializes Controller.
 */
public class ImgPosInst extends Application
{
  /**
   * Método start.
   * @param stage - O estágio principal
   * @throws Exception quando há falha na inicialização do estágio
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    Parent root = FXMLLoader.load(getClass().getResource("ImgPosInstFXML.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Ferramenta de Pós-Instalação de Imagem");
    stage.setResizable(false);
    stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
    stage.setAlwaysOnTop(true);
    stage.setOnCloseRequest((e) -> {e.consume();});
    stage.show();
  }

  /**
   * Método Main.
   * @param args
   */
  public static void main(String[] args) {launch(args);}
}


