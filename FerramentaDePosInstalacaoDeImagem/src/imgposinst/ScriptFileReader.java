package imgposinst;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;


/**
  * @author Daniel Augusto Monteiro de Almeida
  * @since 12/02/2019
  * @version 1.0.0-20191204-85
  *
  * Read a custom Script File.
  */
public class ScriptFileReader
{

  public ScriptFileReader()
  {
    organized = false;
    openbracket = false;
    closebracket = false;
    searchDone = false;
    searchCount = 0;
    breakCount = 0;
    underline = new ArrayList<>();
    underline2 = new ArrayList<>();
    substr = "";
    linecount = 0;
    str = "";
    organizedOutput = new ArrayList<>();
    unorganizedOutput = new ArrayList<>();
    itemListing = new ArrayList<>();
  }

  private File scriptFile;
  private boolean organized;
  private boolean openbracket;
  private boolean closebracket;
  private boolean searchDone;
  private int searchCount;
  private int breakCount;
  private final List<String> underline;
  private final List<String> underline2;
  private String substr;
  private int linecount;
  private String str;
  private final List<String> organizedOutput;
  private final List<String> unorganizedOutput;
  private final List<String> itemListing;

  private void cleanVars()
  {
    this.linecount = 0;
    this.underline.clear();
  }

  public void readScript(File scriptFile, boolean isOrganized)
  {
    this.scriptFile = scriptFile;
    this.organized = isOrganized;
    List<String> lis = new ArrayList<>();
    if (this.organized)
    {
      try
      {
        lis = FileUtils.readLines(this.scriptFile, "UTF-8");
        lis.forEach((line) ->
        {
          if (line.contains("{"))
          {
            this.openbracket = true;
          }
          else if (this.openbracket)
          {
            try
            {
              this.underline.add(Files.readAllLines(this.scriptFile.toPath()).get(this.linecount+1));
              this.underline.forEach((lines) ->
              {
                if (lines.contains("{"))
                {
                  substr = line.trim().replaceAll("\\s+", " ");
                  this.organizedOutput.add("Category: " + substr);
                }
                else
                {
                  substr = line.trim().replaceAll("\\s+", " ");
                  this.organizedOutput.add("Item: " + substr);
                }
              });
              this.underline.clear();
              this.openbracket = false;
            }
            catch (IOException ex)
            {
              logging("ERROR AT READSCRIPT()");
            }
          }
          else
          {
            if (line.contains("}"))
            {
              this.closebracket = true;
            }
            else if (closebracket)
            {
              try
              {
                this.underline.add(Files.readAllLines(this.scriptFile.toPath()).get(this.linecount+1));
              }
              catch (IOException ex)
              {
                System.out.println("ERROR");
              }
              this.underline.forEach((lines2) ->
              {
                if (!lines2.contains("}"))
                {
                  try
                  {
                    this.underline2.add(Files.readAllLines(this.scriptFile.toPath()).get(this.linecount+2));
                    this.underline2.forEach((lines3) ->
                    {
                      substr = line.trim().replaceAll("\\s+", " ");
                      this.organizedOutput.add("\nCategory: " + substr);
                    });
                    this.underline2.clear();
                    this.closebracket = false;
                  }
                  catch (IOException ex)
                  {
                    logging("ERROR AT READSCRIPT()");
                  }
                }
              });
              this.underline.clear();
            }
            else
            {
              substr = line.trim().replaceAll("\\s+", " ");
              this.organizedOutput.add("Item: " + substr);
            }
          }
          this.linecount++;
        });
        this.cleanVars();
      }
      catch (IOException ex)
      {
        logging("ERROR AT READSCRIPT()");
      }
    }
    else
    {
      readScript(this.scriptFile, true);
      this.organizedOutput.forEach((line) ->
      {
        if (line.contains("Category: ") | line.contains("Item: "))
        {
          line = line.replace("Category: ", "");
          line = line.replace("Item: ", "");
          this.unorganizedOutput.add(line);
        }
      });
      this.organized = false;
      this.cleanVars();
    }
  }

  public String exportOrgOutput()
  {
    readScript(this.scriptFile, true);
    if (this.organized)
    {
      this.organizedOutput.forEach((line) ->
      {
        str = str + "\n" + line;
      });
    }
    else
    {
      this.unorganizedOutput.forEach((line) ->
      {
        str = str + "\n" + line;
      });
    }
    return str;
  }

  public List<String> searchItemsFromCategory(String category)
  {
    readScript(this.scriptFile, true);
    this.organizedOutput.forEach((line2) ->
    {
      line2 = line2.trim().replaceAll("\\s+", " ");
      if (line2.equals("Category: " + category))
      {
        this.breakCount = this.linecount + 1;
        while (!this.searchDone)
        {
          this.organizedOutput.forEach((line) ->
          {
            if (this.searchCount == this.breakCount)
            {
              if(line.contains("Item: "))
              {
                line = line.replace("Item: ", "");
                this.itemListing.add(line);
              }
              else
              {
                this.searchDone = true;
              }
            }
            this.searchCount++;
          });
          this.searchCount = 0;
          this.breakCount++;
        }
      }
      this.linecount++;
    });
    this.cleanVars();
    return this.itemListing;
  }

  private void logging (String message) { System.out.println(message); }

}