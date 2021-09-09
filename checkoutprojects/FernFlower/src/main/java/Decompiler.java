import org.jboss.windup.decompiler.api.*;
import org.jboss.windup.decompiler.fernflower.FernFlowerResultSaver;
import org.jboss.windup.decompiler.fernflower.FernflowerDecompiler;
import org.jboss.windup.decompiler.fernflower.FernflowerJDKLogger;
import org.jboss.windup.util.exception.WindupStopException;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.util.InterpreterUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Decompiler extends FernflowerDecompiler {
    /**
     * 参数是class文件
     * @param
     */
    public String decompile() {

        try {
            // 1. 定义反编译后代码存储位置
            String sourceFolder = "/Users/chixiaye/prapr/examples/Defects4J/Lang-33/target/prapr-reports/202108312147/pool/";
            String classPath="/Users/chixiaye/prapr/examples/Defects4J/Lang-33/target/prapr-reports/202108312147/pool/mutant-1.class";
            // 3. 调用ConsoleDecompiler

            DecompilationResult result = decompileClassFile(Paths.get( classPath), Paths.get(sourceFolder));
            if (result.getFailures().size() > 0) {
                return "";
            }

            // 4. 获取编译后的文件
            Map<String, String> decompiles = result.getDecompiledFiles();

            File sourceCodeFile = new File(decompiles.values().iterator().next());

            FileReader fileReader = new FileReader(sourceCodeFile);
            String code = fileReader.getEncoding();
            System.out.println(code);


            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private Map<String, Object> getOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put(IFernflowerPreferences.REMOVE_BRIDGE, "0");
        options.put(IFernflowerPreferences.LAMBDA_TO_ANONYMOUS_CLASS, "1");
        options.put(IFernflowerPreferences.BYTECODE_SOURCE_MAPPING, "1");
        options.put(IFernflowerPreferences.DUMP_ORIGINAL_LINES, "1");
        return options;
    }

    public DecompilationResult decompileClassFile(Path classFilePath, Path outputDir) throws DecompilationException {
        final DecompilationResult result = new DecompilationResult();
        DecompilationListener listener = new DecompilationListener() {
            private boolean cancelled;

            @Override
            public void fileDecompiled(List<String> inputPath, String outputPath) {
                try {
                    result.addDecompiled(inputPath, outputPath);
                }
                catch (WindupStopException stop) {
                    this.cancelled = true;
                    throw new WindupStopException(stop);
                }
            }

            @Override
            public void decompilationFailed(List<String> inputPath, String message) {
                result.addFailure(new DecompilationFailure(message, inputPath, null));
            }

            @Override
            public void decompilationProcessComplete(){
            }

            @Override
            public boolean isCancelled() {
                return this.cancelled;
            }
        };

        FernFlowerResultSaver resultSaver = getResultSaver(Collections.singletonList(classFilePath.toString()), outputDir.toFile(), listener);
        Fernflower fernflower = new Fernflower(getByteCodeProvider(), resultSaver, getOptions(), new FernflowerJDKLogger());
        fernflower.getStructContext().addSpace(classFilePath.toFile(), true);
        fernflower.decompileContext();

        if (!resultSaver.isFileSaved()) {
            listener.decompilationFailed(Collections.singletonList(classFilePath.toString()), "File was not decompiled!");
        }

        return result;
    }

    private IBytecodeProvider getByteCodeProvider() {
        return new IBytecodeProvider() {
            @Override
            public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
                return InterpreterUtil.getBytes(new File(externalPath));
            }
        };
    }

    private FernFlowerResultSaver getResultSaver(final List<String> requests, File directory, final DecompilationListener listener) {
        return new FernFlowerResultSaver(requests, directory, listener);
    }

}
