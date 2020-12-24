/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgposinst.starter;


import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;


/**
 * @author Daniel Augusto Monteiro de Almeida
 * @since 11/08/2019
 * @version 1.0.0-20191108-8
 *
 * Main Class.
 */
public class ImgPosInstStarter
{

  /**
   * Corefile containing all info about UI and custom tweaks for the System.
   */
  static File corefile = new File("C:" + File.separator + "ImgPosInst" + File.separator + "core.cfg");

  /**
   * Main Method.
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
    System.out.println("ARQUIVO COREFILE DETECTADO");
    try
    {
      new ProcessBuilder("cmd", "/c", "Ferramenta de Pós-Instalação de Imagem.jar").directory(new File("C:" + File.separator + "ImgPosInst")).start();
      System.out.println("PROGRAMA EXECUTADO COM SUCESSO");
    }
    catch (IOException ex)
    {
      System.out.println("ERRO AO EXECUTAR O ARQUIVO JAR");
    }
  }
}