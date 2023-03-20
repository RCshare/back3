package rc.personal.app.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;
import rc.personal.app.model.Method;

import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GHContent;


@Service
public class MethodExtractorService {
    Map<String, List<Method>> methodsByFile = new HashMap<>();

    String TOKEN = "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";
    String OWNER = "RCshare";
    String REPO = "back1";
    String PATH = "src";
    String BRANCH = "springSecurity";


    public static List<File> getJavaFilesFromContent(String content) {
        List<File> javaFiles = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().endsWith(".java")) {
                String[] parts = line.trim().split("/");
                String fileName = parts[parts.length - 1];
                String filePath = fileName;
                for (int i = parts.length - 2; i >= 0; i--) {
                    filePath = parts[i] + File.separator + filePath;
                }
                try {
                    File tempFile = File.createTempFile("temp", ".java");
                    Files.write(Paths.get(tempFile.getPath()), line.getBytes());
                    javaFiles.add(tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return javaFiles;
    }

    public List<Method> extractMethodsFromDirectory() throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(TOKEN).build();
        List<Method> methods = new ArrayList<>();
        getMethodsFromContentOrDirectory(github, OWNER, BRANCH, REPO, PATH, methods);
        return methods;
    }
    public static void getMethodsFromContentOrDirectory(GitHub github, String owner, String branch, String repo, String path, List<Method> methods) throws IOException {
        List<GHContent> contentList = github.getRepository(owner + '/' + repo).getDirectoryContent(path,branch);

        for (GHContent content : contentList) {
            if ("file".equals(content.getType()) && content.getName().endsWith(".java")) {
                String fileContent = new String(Base64.getDecoder().decode(content.getEncodedContent().replaceAll("\\r?\\n", "")), StandardCharsets.UTF_8);
                List<Method> fileMethods = extractMethodsFromJavaFileContent(fileContent, content.getName());
                methods.addAll(fileMethods);
            } else if ("dir".equals(content.getType())) {
                String dirPath = path + "/" + content.getName();
                getMethodsFromContentOrDirectory(github, owner, branch, repo, dirPath, methods);
            }
        }
    }
    public static List<Method> extractMethodsFromJavaFileContent(String javaFileContent, String javaFileName) {
        List<Method> methods = new ArrayList<>();
        CompilationUnit cu = parseJavaFileContent(javaFileContent);
        if (cu == null) {
            return methods;
        }
        List<MethodDeclaration> methodDeclarations = getMethodDeclarations(cu);
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            String methodName = methodDeclaration.getName().toString();
            String methodCode = methodDeclaration.toString();
            Method method = new Method(methodName, methodCode, javaFileName);
            methods.add(method);
        }
        return methods;
    }
    public static CompilationUnit parseJavaFileContent(String javaFileContent) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(javaFileContent.getBytes());
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new ParseException("Parsing failed"));
            return cu;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    //public List<Method> extractMethodsFromDirectory() throws IOException {
//
    //    GitHub github = new GitHubBuilder().withOAuthToken(TOKEN).build();
    //    List<GHContent> contentList = github.getRepository(OWNER+'/'+REPO).getDirectoryContent(PATH);
    //    for (GHContent content : contentList) {
    //        if ("file".equals(content.getType()) && content.getName().endsWith(".java")) {
    //            String fileContent = new String(Base64.getDecoder().decode(content.getContent()), StandardCharsets.UTF_8);
    //            try {
    //                File tempFile = File.createTempFile("temp", ".java");
    //                Files.write(Paths.get(tempFile.getPath()), fileContent.getBytes());
    //                javaFiles.add(tempFile);
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
//
    //    }
//
    //    GHContent content = github.getRepository(OWNER+'/'+REPO).getFileContent(PATH);
//
    //    String fileContent = new String(Base64.getDecoder().decode(content.getContent()), StandardCharsets.UTF_8);
    //    List<File> javaFiles = getJavaFilesFromContent(fileContent);
//
    //    //List<File> javaFiles = getJavaFilesFromDirectory(directoryPath);
    //    List<Method> methods = new ArrayList<>();
    //    for (File file : javaFiles) {
    //        CompilationUnit cu = parseJavaFile(file);
    //        if (cu == null) {
    //            continue;
    //        }
    //        String fileName = file.getName();
    //        List<MethodDeclaration> methodDeclarations = getMethodDeclarations(cu);
    //        for (MethodDeclaration methodDeclaration : methodDeclarations) {
    //            String methodName = methodDeclaration.getName().toString();
    //            String methodCode = methodDeclaration.toString();
    //            Method method = new Method(methodName, methodCode, fileName);
    //            methods.add(method);
    //        }
    //    }
    //    return methods;
    //}


    public void printExtractedMethods(List<Method> methods) {
        System.out.println("List of extracted methods:");
        methods.sort(Comparator.comparing(Method::getFileName));
        for (Method method : methods) {
            System.out.println("File name: " + method.getFileName());
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



    private static List<MethodDeclaration> getMethodDeclarations(CompilationUnit cu) {
        return cu.getTypes().stream()
                .flatMap(typeDeclaration -> typeDeclaration.getMethods().stream())
                .collect(Collectors.toList());
    }


}
