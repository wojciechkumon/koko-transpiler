package newkokoc;

import org.apache.commons.io.FilenameUtils;
import org.newkoko.c.lexer.Lexer;
import org.newkoko.c.node.Start;
import org.newkoko.c.parser.Parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import newkoko.NewKokoMain;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class NewKokoC {

  public static void main(String[] argv) throws Exception {
    if (argv.length == 0) {
      InputStream inputStream = NewKokoMain.class.getClassLoader()
          .getResourceAsStream("test-input/new_koko_c.koko");
      singleCompile(inputStream, System.out);
      return;
    }

    Arrays.asList(argv)
        .forEach(NewKokoC::handleFile);
  }

  private static void handleFile(String file) {
    Path inputPath = Paths.get(file).toAbsolutePath();
    if (!Files.exists(inputPath)) {
      System.err.println("File " + file + " not exists!");
      return;
    }
    if (!Files.isReadable(inputPath)) {
      System.err.println("File " + file + " read protected!");
      return;
    }
    Path outputPath = getOutFilePath(inputPath);
    if (Files.exists(outputPath) && !Files.isWritable(outputPath)) {
      System.err.println("File " + outputPath + " is write protected!");
      return;
    }

    compileFile(inputPath, outputPath);
  }

  private static Path getOutFilePath(Path inputPath) {
    String outputFileName = FilenameUtils.getBaseName(inputPath.toString()) + ".c";
    return inputPath.getParent().resolve(outputFileName);
  }

  private static void compileFile(Path inputPath, Path outputPath) {
    try (InputStream in = Files.newInputStream(inputPath, READ)) {
      try (OutputStream out = Files.newOutputStream(outputPath, CREATE, TRUNCATE_EXISTING)) {
        singleCompile(in, out);
      }
      System.out.println(inputPath.getFileName() + " compiled to " + outputPath.getFileName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void singleCompile(InputStream in, OutputStream out) throws Exception {
    Lexer l = new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(in))));
    Parser p = new Parser(l);
    Start start = p.parse();

    try (KokoCodeGenerator codeGenerator = new KokoCodeGenerator(out)) {
      start.apply(codeGenerator);
    }
    //      start.apply(new AstDisplayer());
  }
}
