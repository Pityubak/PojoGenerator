package com.pityubak.pojogenerator.service;

import com.squareup.javapoet.JavaFile;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 *
 * @author Pityubak
 */
public interface Writer {

    void buildFieldAndMethod(Name name, VariableElement element);

    JavaFile buildFinalClass(TypeElement element);
}
