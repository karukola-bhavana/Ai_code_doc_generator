package com.bhavana.aidoc.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JavaCodeParser {

    public static class ClassInfo {
        private String name;
        private String packageName;
        private boolean isInterface;
        private List<String> annotations = new ArrayList<>();
        private List<String> fields = new ArrayList<>();
        private List<String> methods = new ArrayList<>();
        private List<String> restEndpoints = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPackageName() { return packageName; }
        public void setPackageName(String packageName) { this.packageName = packageName; }

        public boolean isInterface() { return isInterface; }
        public void setInterface(boolean isInterface) { this.isInterface = isInterface; }

        public List<String> getAnnotations() { return annotations; }
        public List<String> getFields() { return fields; }
        public List<String> getMethods() { return methods; }
        public List<String> getRestEndpoints() { return restEndpoints; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(isInterface ? "Interface: " : "Class: ").append(name);
            if (packageName != null && !packageName.isEmpty()) {
                sb.append(" (Package: ").append(packageName).append(")");
            }
            sb.append("\n");

            if (!annotations.isEmpty()) {
                sb.append("  Annotations: ").append(String.join(", ", annotations)).append("\n");
            }
            if (!restEndpoints.isEmpty()) {
                sb.append("  REST Endpoints:\n");
                for (String ep : restEndpoints) {
                    sb.append("    - ").append(ep).append("\n");
                }
            }
            if (!fields.isEmpty()) {
                sb.append("  Fields:\n");
                for (String f : fields) {
                    sb.append("    - ").append(f).append("\n");
                }
            }
            if (!methods.isEmpty()) {
                sb.append("  Methods:\n");
                for (String m : methods) {
                    sb.append("    - ").append(m).append("\n");
                }
            }
            return sb.toString();
        }
    }

    public ClassInfo parseJavaCode(String sourceCode) {
        ClassInfo info = new ClassInfo();
        if (sourceCode == null || sourceCode.isBlank()) {
            return info;
        }

        try {
            CompilationUnit cu = StaticJavaParser.parse(sourceCode);

            cu.getPackageDeclaration().ifPresent(pkg -> info.setPackageName(pkg.getNameAsString()));

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                info.setName(clazz.getNameAsString());
                info.setInterface(clazz.isInterface());

                for (AnnotationExpr ann : clazz.getAnnotations()) {
                    info.getAnnotations().add("@" + ann.getNameAsString());
                }

                for (FieldDeclaration field : clazz.getFields()) {
                    field.getVariables().forEach(var -> {
                        info.getFields().add(field.getElementType().asString() + " " + var.getNameAsString());
                    });
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    String methodSig = method.getType().asString() + " " + method.getNameAsString() + "(" +
                            formatParameters(method) + ")";
                    info.getMethods().add(methodSig);

                    // Check for Spring Web REST annotations
                    for (AnnotationExpr ann : method.getAnnotations()) {
                        String name = ann.getNameAsString();
                        if (name.equals("GetMapping") || name.equals("PostMapping") ||
                            name.equals("PutMapping") || name.equals("DeleteMapping") ||
                            name.equals("RequestMapping")) {
                            info.getRestEndpoints().add("@" + name + " " + methodSig + " -> " + ann.toString());
                        }
                    }
                }
            });
        } catch (Exception e) {
            // Fallback for non-parseable code
            info.setName("Unknown / Unparsed");
        }

        return info;
    }

    private String formatParameters(MethodDeclaration method) {
        List<String> params = new ArrayList<>();
        method.getParameters().forEach(p -> params.add(p.getType().asString() + " " + p.getNameAsString()));
        return String.join(", ", params);
    }

    public String summarizeClasses(List<ClassInfo> classes) {
        if (classes == null || classes.isEmpty()) {
            return "No Java classes analyzed.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("### Analyzed Java Components (Total: ").append(classes.size()).append(")\n\n");
        for (ClassInfo ci : classes) {
            sb.append(ci.toString()).append("\n");
        }
        return sb.toString();
    }
}
