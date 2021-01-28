package com.climb.swagger.config;

import com.climb.swagger.annotations.IgnoreSwaggerParameter;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ClassUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.Types;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeField;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.Collections.collectionElementType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.typeNameFor;

/**
 * 支持{@link IgnoreSwaggerParameter}
 * @author lht
 * @since 2020/10/10 12:21
 */
@Primary
@Slf4j
@ConditionalOnBean(FieldProvider.class)
public class CustomizeModelAttributeParameterExpander extends ModelAttributeParameterExpander {

    private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
    private final FieldProvider fieldProvider;
    private final EnumTypeDeterminer enumTypeDeterminer;

    @Autowired
    protected DocumentationPluginsManager pluginsManager;

    public CustomizeModelAttributeParameterExpander(
            FieldProvider fields,
            EnumTypeDeterminer enumTypeDeterminer) {
        super(fields, enumTypeDeterminer);
        this.fieldProvider = fields;
        this.enumTypeDeterminer = enumTypeDeterminer;

    }


    @Override
    public List<Parameter> expand(ExpansionContext context) {

        List<Parameter> parameters = newArrayList();
        Set<String> beanPropNames = getBeanPropertyNames(context.getParamType().getErasedType());

        log.debug("Expanding parameter type: {}", context.getParamType());
        AlternateTypeProvider alternateTypeProvider = context.getDocumentationContext().getAlternateTypeProvider();

        List<ResolvedField> fieldsFirst = Lists.newArrayList();
        fieldProvider.in(context.getParamType()).forEach(fieldsFirst::add);

        List<ModelAttributeField> modelAttributes = fieldsFirst.stream()
                .filter(onlyBeanProperties(beanPropNames))
                .map(toModelAttributeField(alternateTypeProvider))
                .collect(Collectors.toList());

        List<ModelAttributeField> expendables = modelAttributes.stream()
                .filter(not(simpleType()))
                .filter(not(recursiveType(context)))
                .collect(Collectors.toList());

        for (ModelAttributeField each : expendables) {
            log.debug("Attempting to expand expandable field: {}", each.getField());
            parameters.addAll(
                    expand(
                            context.childContext(
                                    nestedParentName(context.getParentName(), each.getField()),
                                    each.getFieldType(),
                                    context.getDocumentationContext())));
        }

        List<ModelAttributeField> collectionTypes = modelAttributes.stream()
                .filter(and(isCollection(), not(recursiveCollectionItemType(context.getParamType()))))
                .collect(Collectors.toList());
        for (ModelAttributeField each : collectionTypes) {
            log.debug("Attempting to expand collection/array field: {}", each.getField());

            ResolvedType itemType = collectionElementType(each.getFieldType());
            if (Types.isBaseType(itemType) || enumTypeDeterminer.isEnum(itemType.getErasedType())) {
                parameters.add(simpleFields(context.getParentName(), context.getDocumentationContext(), each));
            } else {
                parameters.addAll(
                        expand(
                                context.childContext(
                                        nestedParentName(context.getParentName(), each.getField()),
                                        itemType,
                                        context.getDocumentationContext())));
            }
        }

        List<ModelAttributeField> simpleFields = modelAttributes.stream()
                .filter(simpleType())
                .collect(Collectors.toList());

        for (ModelAttributeField each : simpleFields) {
            parameters.add(simpleFields(context.getParentName(), context.getDocumentationContext(), each));
        }

        return parameters.stream().filter(not(hiddenParameters())).collect(Collectors.toList());
    }

    private Set<String> getBeanPropertyNames(Class<?> clazz) {
        try {
            Set<String> beanProps = new HashSet<>();
            PropertyDescriptor[] propDescriptors = getBeanInfo(clazz).getPropertyDescriptors();
            PropertyDescriptor[] arr$ = propDescriptors;
            int len$ = propDescriptors.length;
            for(int i$ = 0; i$ < len$; ++i$) {
                PropertyDescriptor propDescriptor = arr$[i$];
                //增加忽略逻辑 忽略IgnoreSwaggerParameter注解字段
                Field field= null;
                try{
                    field  = clazz.getDeclaredField(propDescriptor.getName());
                }catch (Exception e){
                }
                if (field!=null) {
                    field.setAccessible(true);
                    IgnoreSwaggerParameter ignoreSwaggerParameter = field.getDeclaredAnnotation(IgnoreSwaggerParameter.class);
                    if (ignoreSwaggerParameter != null) {
                        continue;
                    }
                }
                // 增加结束
                if (propDescriptor.getReadMethod() != null) {
                    beanProps.add(propDescriptor.getName());
                }
            }

            return beanProps;
        } catch (IntrospectionException var8) {
            log.warn(String.format("Failed to get bean properties on (%s)", clazz), var8);
            return Sets.newHashSet();
        }
    }


    private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return equal(collectionElementType(input.getFieldType()), paramType);
            }
        };
    }

    private Predicate<Parameter> hiddenParameters() {
        return new Predicate<Parameter>() {
            @Override
            public boolean apply(Parameter input) {
                return input.isHidden();
            }
        };
    }

    private Parameter simpleFields(
            String parentName,
            DocumentationContext documentationContext,
            ModelAttributeField each) {
        LOG.debug("Attempting to expand field: {}", each);
        String dataTypeName = Optional.fromNullable(typeNameFor(each.getFieldType().getErasedType()))
                .or(each.getFieldType().getErasedType().getSimpleName());
        LOG.debug("Building parameter for field: {}, with type: ", each, each.getFieldType());
        ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
                dataTypeName,
                parentName,
                each.getField(),
                documentationContext.getDocumentationType(),
                new ParameterBuilder());
        return pluginsManager.expandParameter(parameterExpansionContext);
    }


    private Predicate<ModelAttributeField> recursiveType(final ExpansionContext context) {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return context.hasSeenType(input.getFieldType());
            }
        };
    }

    private Predicate<ModelAttributeField> simpleType() {
        return and(not(isCollection()), not(isMap()),
                or(
                        belongsToJavaPackage(),
                        isBaseType(),
                        isEnum()));
    }

    private Predicate<ModelAttributeField> isCollection() {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return isContainerType(input.getFieldType());
            }
        };
    }

    private Predicate<ModelAttributeField> isMap() {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return Maps.isMapType(input.getFieldType());
            }
        };
    }

    private Predicate<ModelAttributeField> isEnum() {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return enumTypeDeterminer.isEnum(input.getFieldType().getErasedType());
            }
        };
    }

    private Predicate<ModelAttributeField> belongsToJavaPackage() {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return ClassUtils.getPackageName(input.getFieldType().getErasedType()).startsWith("java.lang");
            }
        };
    }

    private Predicate<ModelAttributeField> isBaseType() {
        return new Predicate<ModelAttributeField>() {
            @Override
            public boolean apply(ModelAttributeField input) {
                return Types.isBaseType(input.getFieldType())
                        || input.getField().getType().isPrimitive();
            }
        };
    }

    private Function<ResolvedField, ModelAttributeField> toModelAttributeField(
            final AlternateTypeProvider
                    alternateTypeProvider) {
        return new Function<ResolvedField, ModelAttributeField>() {
            @Override
            public ModelAttributeField apply(ResolvedField input) {
                return new ModelAttributeField(fieldType(alternateTypeProvider, input), input);
            }
        };
    }

    private Predicate<ResolvedField> onlyBeanProperties(final Set<String> beanPropNames) {
        return new Predicate<ResolvedField>() {
            @Override
            public boolean apply(ResolvedField input) {
                return beanPropNames.contains(input.getName());
            }
        };
    }

    private String nestedParentName(String parentName, ResolvedField field) {
        String name = field.getName();
        ResolvedType fieldType = field.getType();
        if (isContainerType(fieldType) && !Types.isBaseType(collectionElementType(fieldType))) {
            name += "[0]";
        }

        if (isNullOrEmpty(parentName)) {
            return name;
        }
        return String.format("%s.%s", parentName, name);
    }

    private ResolvedType fieldType(AlternateTypeProvider alternateTypeProvider, ResolvedField field) {
        return alternateTypeProvider.alternateFor(field.getType());
    }


    @VisibleForTesting
    BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz);
    }
}
