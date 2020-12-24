package io.swagger.models.properties;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.models.Xml;

import java.util.Map;

public interface Property {
    Property title(String title);

    Property description(String description);

    String getType();

    String getFormat();

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String title);

    Boolean getAllowEmptyValue();

    void setAllowEmptyValue(Boolean value);

    @JsonIgnore
    String getName();

    void setName(String name);

    boolean getRequired();
    /**
     * knife4j ui 在前端获取是否必填时，使用require属性，所以增加一个方法用于获取必填属性
     * @author lht
     * @since  2020/9/27 17:47
     */
    boolean getRequire();

    void setRequired(boolean required);

    @JsonGetter
    Object getExample();

    @JsonSetter
    void setExample(Object example);

    @JsonIgnore
    void setExample(String example);

    Boolean getReadOnly();

    void setReadOnly(Boolean readOnly);

    Integer getPosition();

    void setPosition(Integer position);

    Xml getXml();

    void setXml(Xml xml);

    void setDefault(String _default);

    @JsonIgnore
    String getAccess();

    @JsonIgnore
    void setAccess(String access);

    Map<String, Object> getVendorExtensions();

    /**
     * creates a new instance and renames the property to the given name.
     *
     * @return new shallow copy of the property
     */
    Property rename(String newName);
}
