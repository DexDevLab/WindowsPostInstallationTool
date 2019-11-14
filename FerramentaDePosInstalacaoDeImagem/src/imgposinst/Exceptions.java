package imgposinst;


import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;


/**
 * This Class provides a custom Exception info
 * window, depending of the throwable.
 */
public class Exceptions
{
  /**
   * Treat the Exception accordingly, giving
   * a dialog showing the error.
   * @param ex - The throwable of the exception
   */
  public void treatException(Throwable ex)
  {
    Alert exceptionDialog = new Alert(Alert.AlertType.ERROR,"",ButtonType.OK);
    exceptionDialog.initModality(Modality.APPLICATION_MODAL);
    Stage stage = (Stage) exceptionDialog.getDialogPane().getScene().getWindow();
    stage.getIcons().add(new Image(ImgPosInst.class.getResourceAsStream("icon1.jpg")));
    stage.setOnCloseRequest((e) -> {e.consume();});
    exceptionDialog.setTitle("EXCEÇÃO - " + ExceptionUtils.getMessage(ex));
    exceptionDialog.setHeaderText("Uma falha no programa causou uma Exceção.");
    exceptionDialog.setContentText("Mensagem da Exceção: " + ExceptionUtils.getMessage(ex) + "\n\n"
                               +"Causa Raiz da Exceção: " + ExceptionUtils.getRootCauseMessage(ex) + "\n\n"
                               +"Stack do Erro: " + ExceptionUtils.getStackTrace(ex));
    ((Stage) exceptionDialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
    Optional<ButtonType> result = exceptionDialog.showAndWait();
    if (result.get() == ButtonType.OK)
    {
      exceptionDialog.close();
    }
  }
}
