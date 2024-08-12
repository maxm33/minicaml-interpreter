import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import constructs.Binding;
import constructs.Expression;
import exceptions.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args)
            throws IllegalTokenException, WrongSyntaxException, ZeroDividerException,
            UnknownCommandException, TypeMismatchException, NoBindingException, IOException {
        if (args.length < 1) {
            System.err.println("\nNo path was provided.\nUsage: java Main <path-to-file>");
            return;
        }
        if (!args[0].endsWith(".ml")) {
            System.err.println("\nFile is not a .ml file");
            return;
        }

        String program = Files.readString(Paths.get(args[0]));
        String[] blocks = program.split("(?<=\\s+;;)");
        List<Binding> env = new ArrayList<Binding>();

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        Interpreter interpreter = new Interpreter();

        for (String block : blocks) {
            System.out.println(block + "\n");

            lexer.setInput(block);
            lexer.tokenize();

            parser.setInput(lexer.getResult());
            parser.parse();

            Expression result = interpreter.eval(parser.getResult(), env);

            if (result != null)
                System.out.println("-: " + result.getClass().getSimpleName() + " = " + interpreter.printValue(result));
            else
                System.out.println("-: null");
        }
    }
}
