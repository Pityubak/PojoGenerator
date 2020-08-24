package com.pityubak.pojogenerator.service;

import com.google.auto.service.AutoService;
import com.pityubak.pojogenerator.annotations.Pojo;
import com.pityubak.pojogenerator.exception.ProcessingException;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 *
 * @author Pityubak
 */
@AutoService(Processor.class)
public class PojoProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Writer builder = new TemplateWriter(elementUtils);
        for (Element element : roundEnv.getElementsAnnotatedWith(Pojo.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                throw new ProcessingException("Only classes can be annotated with @%s",
                        Pojo.class.getSimpleName());
            }
            TypeElement typeElement = (TypeElement) element;
            Name className = typeElement.getSimpleName();
            validateClass(typeElement);

            for (Element vars : typeElement.getEnclosedElements()) {
                if (vars.getKind() == ElementKind.FIELD) {
                    VariableElement variable = (VariableElement) vars;
                    builder.buildFieldAndMethod(className, variable);
                }
            }
            JavaFile file = builder.buildFinalClass(typeElement);
            try {
                file.writeTo(filer);
            } catch (IOException ex) {
                error(typeElement, ex.getMessage());
            }
        }

        return true;
    }

    private void validateClass(TypeElement typeElement) throws ProcessingException {
        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new ProcessingException(
                    "The class %s is abstract. You can't annotate abstract classes with @%",
                    typeElement.getQualifiedName().toString(), Pojo.class.getSimpleName());
        }
        if (typeElement.getModifiers().contains(Modifier.PRIVATE)) {
            throw new ProcessingException("The class %s is not public.",
                    typeElement.getQualifiedName().toString());
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Pojo.class.getCanonicalName());
        return annotations;
    }

    public void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
