package loja;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("AutoToString")
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_8)
public class AutoToStringProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getRootElements()) {
            if (element.getKind() == ElementKind.CLASS && element.getAnnotation(AutoToString.class) != null) {
                generateToStringMethod((TypeElement) element);
            }
        }
        return true;
    }

    private void generateToStringMethod(TypeElement classElement) {
        String className = classElement.getSimpleName().toString();
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("public class ").append(className).append(" {\n");
        
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                String fieldName = enclosedElement.getSimpleName().toString();
                sb.append("    private ").append(enclosedElement.asType().toString()).append(" ").append(fieldName).append(";\n");
            }
        }
        
        sb.append("\n");
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"").append(className).append("[\" +\n");
        
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                String fieldName = enclosedElement.getSimpleName().toString();
                sb.append("                \"").append(fieldName).append("=\" + ").append(fieldName).append(" + \",\" +\n");
            }
        }
        
        sb.append("                \"]\";\n");
        sb.append("    }\n");
        sb.append("}\n");

        try {
            JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + className);
            try (java.io.Writer writer = sourceFile.openWriter()) {
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to generate source file: " + e.getMessage());
        }
    }
}
