package newkokoc;

import org.apache.commons.io.FilenameUtils;
import org.newkoko.c.lexer.Lexer;
import org.newkoko.c.node.Start;
import org.newkoko.c.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import newkokoc.semantics.Function;
import newkokoc.semantics.FunctionFinder;
import newkokoc.semantics.SemanticAnalyzer;
import newkokoc.semantics.SemanticAnalyzerException;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class NewKokoC {

  public static void main(String[] argv) throws Exception {
    if (argv.length == 0) {
      InputStream inputStream = NewKokoC.class.getClassLoader()
          .getResourceAsStream("test-input/new_koko_c.koko");
      try {
        singleCompile(inputStream, System.out);
      } catch (SemanticAnalyzerException e) {
        System.err.println("Error in input");
        System.err.println(e.getMessage());
      }
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
    } catch (SemanticAnalyzerException e) {
      System.err.println("Error in file: " + inputPath);
      System.err.println(e.getMessage());
      try {
        Files.deleteIfExists(outputPath);
      } catch (IOException e1) {
        throw new RuntimeException(e1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void singleCompile(InputStream in, OutputStream out) throws Exception {
    Lexer l = new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(in))));
    Parser p = new Parser(l);
    Start start = p.parse();

    List<Function> functions = findFunctions(start);
    checkSemantics(start, functions);

    try (KokoCodeGenerator codeGenerator = new KokoCodeGenerator(out)) {
      start.apply(codeGenerator);
    }
//    start.apply(new AstDisplayer());
  }

  private static List<Function> findFunctions(Start start) {
    FunctionFinder functionFinder = new FunctionFinder();
    start.apply(functionFinder);
    if (functionFinder.errorsFound()) {
      throw new SemanticAnalyzerException(functionFinder.getErrors());
    }
    return functionFinder.getFunctions();
  }

  private static void checkSemantics(Start start, List<Function> functions) {
    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(functions);
    start.apply(semanticAnalyzer);
    if (semanticAnalyzer.errorsFound()) {
      throw new SemanticAnalyzerException(semanticAnalyzer.getErrors());
    }
  }
}
