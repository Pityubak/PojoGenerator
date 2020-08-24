package com.pityubak.pojogenerator.service;

import com.pityubak.pojogenerator.domain.GeneratedTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Name;

/**
 *
 * @author Pityubak
 */
public class GeneratedTemplateMapper {

    private final List<GeneratedTemplate> templates = new ArrayList<>();

    public void createMapping(GeneratedTemplate generated) {
        templates.add(generated);
    }

    public List<GeneratedTemplate> mapping(Name name) {
        final List<GeneratedTemplate> template = templates.stream()
                .filter(t -> t.getName().equals(name))
                .collect(Collectors.toList());
        return template;
    }
}
