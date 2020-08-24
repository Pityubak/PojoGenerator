package com.pityubak.pojogenerator.domain;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import javax.lang.model.element.Name;

/**
 *
 * @author Pityubak
 */
public class GeneratedTemplate {

    private final Name name;
    private final MethodSpec getter;
    private final MethodSpec setter;
    private final FieldSpec field;

    public GeneratedTemplate(Builder builder) {
        this.name = builder.name;
        this.getter = builder.getter;
        this.setter = builder.setter;
        this.field = builder.field;
    }

    public Name getName() {
        return name;
    }

    public MethodSpec getGetter() {
        return getter;
    }

    public MethodSpec getSetter() {
        return setter;
    }

    public FieldSpec getField() {
        return field;
    }

    public static class Builder {

        private final Name name;
        private FieldSpec field;
        private MethodSpec getter;
        private MethodSpec setter;

        public Builder(Name name) {
            this.name = name;
        }

        public Builder addField(FieldSpec field) {
            this.field = field;
            return this;
        }

        public Builder addGetter(MethodSpec getter) {
            this.getter = getter;
            return this;
        }

        public Builder addSetter(MethodSpec setter) {
            this.setter = setter;
            return this;
        }

        public GeneratedTemplate build() {
            return new GeneratedTemplate(this);
        }
    }
}
