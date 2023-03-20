package rc.personal.app.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.stereotype.Service;
import rc.personal.app.model.Method;
import org.apache.commons.text.RandomStringGenerator;

@Service
public class MethodExtractorService {

    private static final RandomStringGenerator NAME_GENERATOR = new RandomStringGenerator.Builder()
            .withinRange('a', 'z').build();

    private String anonymizeName(String name) {
        if (name == null) {
            return null;
        }
        String anonymizedName = NAME_GENERATOR.generate(10);
        return name.replaceAll("\\b" + name + "\\b", anonymizedName);
    }

    private String anonymizeCode(String code) {
        if (code == null) {
            return null;
        }
        String anonymizedCode = code;
        anonymizedCode = anonymizedCode.replaceAll("\\bclass\\s+" + "\\w+\\b", "class " + NAME_GENERATOR.generate(10));
        anonymizedCode = anonymizedCode.replaceAll("\\bnew\\s+" + "\\w+\\b", "new " + NAME_GENERATOR.generate(10));
        anonymizedCode = anonymizedCode.replaceAll("\\b\\w+\\s+" + "\\w+\\s*\\(", anonymizeName("$0") + "(");
        return anonymizedCode;
    }

    public List<Method> extractMethodsFromDirectory(String directoryPath) throws IOException {
        List<File> javaFiles = getJavaFilesFromDirectory(directoryPath);
        List<Method> methods = new ArrayList<>();
        for (File file : javaFiles) {
            CompilationUnit cu = parseJavaFile(file);
            if (cu == null) {
                continue;
            }
            List<MethodDeclaration> methodDeclarations = getMethodDeclarations(cu);
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                String methodName = anonymizeName(methodDeclaration.getName().toString());
                String methodCode = anonymizeCode(methodDeclaration.toString());
                Method method = new Method(methodName, methodCode);
                methods.add(method);
            }
        }
        return methods;
    }
    public void printExtractedMethods(List<Method> methods) {
        System.out.println("List of extracted methods:");
        for (Method method : methods) {
            System.out.println("Method name: " + method.getName());
            System.out.println("Method code: " + method.getCode());
            System.out.println();
        }
    }

    private List<File> getJavaFilesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory");
        }
        List<File> javaFiles = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                javaFiles.add(file);
            } else if (file.isDirectory()) {
                javaFiles.addAll(getJavaFilesFromDirectory(file.getAbsolutePath()));
            }
        }
        return javaFiles;
    }

    private CompilationUnit parseJavaFile(File file) throws IOException {
        try {
            JavaParser javaParser = new JavaParser();
            ParseResult<CompilationUnit> parseResult = javaParser.parse(file);
            return parseResult.getResult().orElse(null);
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName());
            e.printStackTrace();
            return null;
        }
    }



    private List<MethodDeclaration> getMethodDeclarations(CompilationUnit cu) {
        return cu.getTypes().stream()
                .flatMap(typeDeclaration -> typeDeclaration.getMethods().stream())
                .collect(Collectors.toList());
    }
}
