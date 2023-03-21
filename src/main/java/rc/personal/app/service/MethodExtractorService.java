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


    public List<Method> extractMethodsFromDirectory() throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(TOKEN).build();
        List<Method> methods = new ArrayList<>();
        getMethodsFromContentOrDirectory(github, OWNER, BRANCH, REPO, PATH, methods);
        return methods;
    }
    public void getMethodsFromContentOrDirectory(GitHub github, String owner, String branch, String repo, String path, List<Method> methods) throws IOException {
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
    public List<Method> extractMethodsFromJavaFileContent(String javaFileContent, String javaFileName) {
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
    public CompilationUnit parseJavaFileContent(String javaFileContent) {
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

    private static List<MethodDeclaration> getMethodDeclarations(CompilationUnit cu) {
        return cu.getTypes().stream()
                .flatMap(typeDeclaration -> typeDeclaration.getMethods().stream())
                .collect(Collectors.toList());
    }


}
