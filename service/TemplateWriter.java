package com.pityubak.pojogenerator.service;

import com.pityubak.pojogenerator.domain.GeneratedTemplate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 *
 * @author Pityubak
 */
public class TemplateWriter implements Writer {

    private final GeneratedTemplateMapper mapper = new GeneratedTemplateMapper();
    private final Elements elementUtils;

    public TemplateWriter(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    @Override
    public void buildFieldAndMethod(Name name, VariableElement element) {
        final TypeName type = TypeName.get(element.asType());
        final String simpleName = element.getSimpleName().toString();
        final MethodSpec getter = buildGetter(type, simpleName);
        final MethodSpec setter = buildSetter(type, simpleName);
        final FieldSpec field = buildField(type, simpleName);
        GeneratedTemplate template = new GeneratedTemplate.Builder(name)
                .addField(field)
                .addGetter(getter)
                .addSetter(setter)
                .build();
        mapper.createMapping(template);
    }

    @Override
    public JavaFile buildFinalClass(TypeElement element) {
        final Name simpleName = element.getSimpleName();
        final String pkgName = getPackageName(element);
        Builder finalClass = TypeSpec.classBuilder(simpleName.toString() + "Pojo")
                .addModifiers(Modifier.PUBLIC);
        addFieldsAndConstructor(finalClass, simpleName);
        addMethods(finalClass, simpleName);
        TypeSpec typeSpec = finalClass.build();

        return JavaFile.builder(pkgName, typeSpec).build();

    }

    private String getPackageName(TypeElement element) {
        PackageElement pkg = elementUtils.getPackageOf(element);
        return pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
    }

    private void addFieldsAndConstructor(Builder builder, Name name) {
        List<FieldSpec> fields = mapper.mapping(name).stream()
                .map(t -> t.getField()).collect(Collectors.toList());
        fields.forEach(builder::addField);
        MethodSpec.Builder constr = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        fields.forEach(f -> {
            constr.addParameter(f.type, f.name);
            constr.addStatement("this.$N=$N", f.name, f.name);
        });
        MethodSpec construct = constr.build();
        builder.addMethod(construct);

    }

    private void addMethods(Builder builder, Name name) {
        List<MethodSpec> getter = mapper.mapping(name).stream()
                .map(t -> t.getGetter()).collect(Collectors.toList());
        List<MethodSpec> setter = mapper.mapping(name).stream()
                .map(t -> t.getSetter()).collect(Collectors.toList());
        getter.forEach(builder::addMethod);
        setter.forEach(builder::addMethod);
    }

    private FieldSpec buildField(TypeName type, String simpleName) {
        return FieldSpec.builder(type, simpleName)
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    private MethodSpec buildGetter(TypeName type, String simpleName) {
        String name = createRightMethodName(simpleName);
        return MethodSpec.methodBuilder("get" + name)
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return $N", simpleName)
                .build();
    }

    private MethodSpec buildSetter(TypeName type, String simpleName) {
        ParameterSpec param = ParameterSpec.builder(type, simpleName)
                .build();
        String name = createRightMethodName(simpleName);
        return MethodSpec.methodBuilder("set" + name)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(param)
                .addStatement("this.$N=$N", simpleName, simpleName)
                .build();
    }

    private String createRightMethodName(String simpleName) {
        return simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);

    }
}
