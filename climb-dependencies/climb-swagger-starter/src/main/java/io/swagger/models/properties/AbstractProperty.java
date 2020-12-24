package io.swagger.models.properties;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.models.Xml;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractProperty implements Property, Cloneable {
    protected String name;
    protected String type;
    protected String format;
    protected Object example;
    protected Xml xml;
    protected boolean required;
    protected Integer position;
    protected String description;
    protected String title;
    protected Boolean readOnly;
    protected Boolean allowEmptyValue;
    protected String access;
    protected Map<String, Object> vendorExtensions = new LinkedHashMap<String, Object>();

    @Override
    public Property rename(String newName) {
        try {
            Property newProperty = (Property) clone();
            newProperty.setName(newName);
            return newProperty;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("Clone is not supported!?");
        }
    }
    @Override
    public Property title(String title) {
        this.setTitle(title);
        return this;
    }
    @Override
    public Property description(String description) {
        this.setDescription(description);
        return this;
    }

    public Property readOnly() {
        this.setReadOnly(Boolean.TRUE);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractProperty)) {
            return false;
        }

        AbstractProperty that = (AbstractProperty) o;

        if (required != that.required) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (format != null ? !format.equals(that.format) : that.format != null) {
            return false;
        }
        if (example != null ? !example.equals(that.example) : that.example != null) {
            return false;
        }
        if (xml != null ? !xml.equals(that.xml) : that.xml != null) {
            return false;
        }
        if (position != null ? !position.equals(that.position) : that.position != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (readOnly != null ? !readOnly.equals(that.readOnly) : that.readOnly != null) {
            return false;
        }
        if (allowEmptyValue != null ? !allowEmptyValue.equals(that.allowEmptyValue) : that.allowEmptyValue != null) {
            return false;
        }
        if (access != null ? !access.equals(that.access) : that.access != null) {
            return false;
        }
        return vendorExtensions != null ? vendorExtensions.equals(that.vendorExtensions) : that.vendorExtensions == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (example != null ? example.hashCode() : 0);
        result = 31 * result + (xml != null ? xml.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (readOnly != null ? readOnly.hashCode() : 0);
        result = 31 * result + (allowEmptyValue != null ? allowEmptyValue.hashCode() : 0);
        result = 31 * result + (access != null ? access.hashCode() : 0);
        result = 31 * result + (vendorExtensions != null ? vendorExtensions.hashCode() : 0);
        return result;
    }

    public Property allowEmptyValue(Boolean allowEmptyValue) {
        this.setAllowEmptyValue(allowEmptyValue);
        return this;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public Object getExample() {
        return example;
    }
    @Override
    public void setExample(Object example) {
        this.example = example;
    }

    @Override
    public void setExample(String example) {
        this.setExample((Object) example);
    }
    @Override
    public Integer getPosition() {
        return position;
    }
    @Override
    public void setPosition(Integer position) {
        this.position = position;
    }
    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    @Override
    public Xml getXml() {
        return xml;
    }
    @Override
    public void setXml(Xml xml) {
        this.xml = xml;
    }
    @Override
    public boolean getRequired() {
        return required;
    }
    @Override
    public boolean getRequire() {
        return required;
    }
    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public Boolean getReadOnly() {
        return readOnly;
    }
    @Override
    public void setReadOnly(Boolean readOnly) {
        if (Boolean.FALSE.equals(readOnly)) {
            this.readOnly = null;
        } else {
            this.readOnly = readOnly;
        }
    }
    @Override
    public void setDefault(String _default) {
        // do nothing
    }

    @Override
    public String getAccess() {
        return access;
    }

    @Override
    public void setAccess(String access) {
        this.access = access;
    }
    @Override
    public Boolean getAllowEmptyValue() {
        return allowEmptyValue;
    }
    @Override
    public void setAllowEmptyValue(Boolean allowEmptyValue) {
        if(allowEmptyValue != null) {
            this.allowEmptyValue = allowEmptyValue;
        }
    }

    @JsonAnyGetter
    @Override
    public Map<String, Object> getVendorExtensions() {
        return vendorExtensions;
    }

    @JsonAnySetter
    public void setVendorExtension(String name, Object value) {
        if (name.startsWith("x-")) {
            vendorExtensions.put(name, value);
        }
    }

    public void setVendorExtensions(Map<String, Object> vendorExtensions) {
        this.vendorExtensions = vendorExtensions;
    }

    public void setVendorExtensionMap(Map<String, Object> vendorExtensionMap) {
        this.vendorExtensions.putAll(vendorExtensionMap);
    }

}
