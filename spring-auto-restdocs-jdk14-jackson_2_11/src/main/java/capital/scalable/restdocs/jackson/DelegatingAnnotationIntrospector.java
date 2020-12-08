/*-
 * #%L
 * Spring Auto REST Docs Java Record Support
 * %%
 * Copyright (C) 2015 - 2020 Scalable Capital GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package capital.scalable.restdocs.jackson;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

class DelegatingAnnotationIntrospector extends AnnotationIntrospector {
    private static final long serialVersionUID = 1L;

    private final AnnotationIntrospector delegate;

    public DelegatingAnnotationIntrospector(AnnotationIntrospector delegate) {
        this.delegate = delegate;
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    public Collection<AnnotationIntrospector> allIntrospectors() {
        return this.delegate.allIntrospectors();
    }

    public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
        return this.delegate.allIntrospectors(result);
    }

    public Version version() {
        return this.delegate.version();
    }

    public boolean isAnnotationBundle(Annotation ann) {
        return this.delegate.isAnnotationBundle(ann);
    }

    public ObjectIdInfo findObjectIdInfo(Annotated ann) {
        return this.delegate.findObjectIdInfo(ann);
    }

    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        return this.delegate.findObjectReferenceInfo(ann, objectIdInfo);
    }

    public PropertyName findRootName(AnnotatedClass ac) {
        return this.delegate.findRootName(ac);
    }

    public Value findPropertyIgnorals(Annotated ac) {
        return this.delegate.findPropertyIgnorals(ac);
    }

    public Boolean isIgnorableType(AnnotatedClass ac) {
        return this.delegate.isIgnorableType(ac);
    }

    public Object findFilterId(Annotated ann) {
        return this.delegate.findFilterId(ann);
    }

    public Object findNamingStrategy(AnnotatedClass ac) {
        return this.delegate.findNamingStrategy(ac);
    }

    public String toString() {
        return this.delegate.toString();
    }

    public String findClassDescription(AnnotatedClass ac) {
        return this.delegate.findClassDescription(ac);
    }

    @Deprecated
    public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
        return this.delegate.findPropertiesToIgnore(ac, forSerialization);
    }

    @Deprecated
    public String[] findPropertiesToIgnore(Annotated ac) {
        return this.delegate.findPropertiesToIgnore(ac);
    }

    @Deprecated
    public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
        return this.delegate.findIgnoreUnknownProperties(ac);
    }

    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        return this.delegate.findAutoDetectVisibility(ac, checker);
    }

    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        return this.delegate.findTypeResolver(config, ac, baseType);
    }

    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am,
        JavaType baseType) {
        return this.delegate.findPropertyTypeResolver(config, am, baseType);
    }

    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am,
        JavaType containerType) {
        return this.delegate.findPropertyContentTypeResolver(config, am, containerType);
    }

    public List<NamedType> findSubtypes(Annotated a) {
        return this.delegate.findSubtypes(a);
    }

    public String findTypeName(AnnotatedClass ac) {
        return this.delegate.findTypeName(ac);
    }

    public Boolean isTypeId(AnnotatedMember member) {
        return this.delegate.isTypeId(member);
    }

    public ReferenceProperty findReferenceType(AnnotatedMember member) {
        return this.delegate.findReferenceType(member);
    }

    public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
        return this.delegate.findUnwrappingNameTransformer(member);
    }

    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return this.delegate.hasIgnoreMarker(m);
    }

    public com.fasterxml.jackson.annotation.JacksonInject.Value findInjectableValue(AnnotatedMember m) {
        return this.delegate.findInjectableValue(m);
    }

    public Boolean hasRequiredMarker(AnnotatedMember m) {
        return this.delegate.hasRequiredMarker(m);
    }

    public Class<?>[] findViews(Annotated a) {
        return this.delegate.findViews(a);
    }

    public com.fasterxml.jackson.annotation.JsonFormat.Value findFormat(Annotated memberOrClass) {
        return this.delegate.findFormat(memberOrClass);
    }

    public PropertyName findWrapperName(Annotated ann) {
        return this.delegate.findWrapperName(ann);
    }

    public String findPropertyDefaultValue(Annotated ann) {
        return this.delegate.findPropertyDefaultValue(ann);
    }

    public String findPropertyDescription(Annotated ann) {
        return this.delegate.findPropertyDescription(ann);
    }

    public Integer findPropertyIndex(Annotated ann) {
        return this.delegate.findPropertyIndex(ann);
    }

    public String findImplicitPropertyName(AnnotatedMember member) {
        return this.delegate.findImplicitPropertyName(member);
    }

    public List<PropertyName> findPropertyAliases(Annotated ann) {
        return this.delegate.findPropertyAliases(ann);
    }

    public Access findPropertyAccess(Annotated ann) {
        return this.delegate.findPropertyAccess(ann);
    }

    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1,
        AnnotatedMethod setter2) {
        return this.delegate.resolveSetterConflict(config, setter1, setter2);
    }

    public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
        return this.delegate.findRenameByField(config, f, implName);
    }

    @Deprecated
    public Object findInjectableValueId(AnnotatedMember m) {
        return this.delegate.findInjectableValueId(m);
    }

    public Object findSerializer(Annotated am) {
        return this.delegate.findSerializer(am);
    }

    public Object findKeySerializer(Annotated am) {
        return this.delegate.findKeySerializer(am);
    }

    public Object findContentSerializer(Annotated am) {
        return this.delegate.findContentSerializer(am);
    }

    public Object findNullSerializer(Annotated am) {
        return this.delegate.findNullSerializer(am);
    }

    public Typing findSerializationTyping(Annotated a) {
        return this.delegate.findSerializationTyping(a);
    }

    public Object findSerializationConverter(Annotated a) {
        return this.delegate.findSerializationConverter(a);
    }

    public Object findSerializationContentConverter(AnnotatedMember a) {
        return this.delegate.findSerializationContentConverter(a);
    }

    public com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(Annotated a) {
        return this.delegate.findPropertyInclusion(a);
    }

    @Deprecated
    public Include findSerializationInclusion(Annotated a, Include defValue) {
        return this.delegate.findSerializationInclusion(a, defValue);
    }

    @Deprecated
    public Include findSerializationInclusionForContent(Annotated a, Include defValue) {
        return this.delegate.findSerializationInclusionForContent(a, defValue);
    }

    public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType)
        throws JsonMappingException {
        return this.delegate.refineSerializationType(config, a, baseType);
    }

    @Deprecated
    public Class<?> findSerializationType(Annotated a) {
        return this.delegate.findSerializationType(a);
    }

    @Deprecated
    public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
        return this.delegate.findSerializationKeyType(am, baseType);
    }

    @Deprecated
    public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
        return this.delegate.findSerializationContentType(am, baseType);
    }

    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        return this.delegate.findSerializationPropertyOrder(ac);
    }

    public Boolean findSerializationSortAlphabetically(Annotated ann) {
        return this.delegate.findSerializationSortAlphabetically(ann);
    }

    public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac,
        List<BeanPropertyWriter> properties) {
        this.delegate.findAndAddVirtualProperties(config, ac, properties);
    }

    public PropertyName findNameForSerialization(Annotated a) {
        return this.delegate.findNameForSerialization(a);
    }

    public Boolean hasAsValue(Annotated a) {
        return this.delegate.hasAsValue(a);
    }

    public Boolean hasAnyGetter(Annotated a) {
        return this.delegate.hasAnyGetter(a);
    }

    public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        return this.delegate.findEnumValues(enumType, enumValues, names);
    }

    public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliases) {
        this.delegate.findEnumAliases(enumType, enumValues, aliases);
    }

    public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
        return this.delegate.findDefaultEnumValue(enumCls);
    }

    @Deprecated
    public String findEnumValue(Enum<?> value) {
        return this.delegate.findEnumValue(value);
    }

    @Deprecated
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        return this.delegate.hasAsValueAnnotation(am);
    }

    @Deprecated
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return this.delegate.hasAnyGetterAnnotation(am);
    }

    public Object findDeserializer(Annotated am) {
        return this.delegate.findDeserializer(am);
    }

    public Object findKeyDeserializer(Annotated am) {
        return this.delegate.findKeyDeserializer(am);
    }

    public Object findContentDeserializer(Annotated am) {
        return this.delegate.findContentDeserializer(am);
    }

    public Object findDeserializationConverter(Annotated a) {
        return this.delegate.findDeserializationConverter(a);
    }

    public Object findDeserializationContentConverter(AnnotatedMember a) {
        return this.delegate.findDeserializationContentConverter(a);
    }

    public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType)
        throws JsonMappingException {
        return this.delegate.refineDeserializationType(config, a, baseType);
    }

    @Deprecated
    public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
        return this.delegate.findDeserializationType(am, baseType);
    }

    @Deprecated
    public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
        return this.delegate.findDeserializationKeyType(am, baseKeyType);
    }

    @Deprecated
    public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
        return this.delegate.findDeserializationContentType(am, baseContentType);
    }

    public Object findValueInstantiator(AnnotatedClass ac) {
        return this.delegate.findValueInstantiator(ac);
    }

    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        return this.delegate.findPOJOBuilder(ac);
    }

    public com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value findPOJOBuilderConfig(
        AnnotatedClass ac) {
        return this.delegate.findPOJOBuilderConfig(ac);
    }

    public PropertyName findNameForDeserialization(Annotated a) {
        return this.delegate.findNameForDeserialization(a);
    }

    public Boolean hasAnySetter(Annotated a) {
        return this.delegate.hasAnySetter(a);
    }

    public com.fasterxml.jackson.annotation.JsonSetter.Value findSetterInfo(Annotated a) {
        return this.delegate.findSetterInfo(a);
    }

    public Boolean findMergeInfo(Annotated a) {
        return this.delegate.findMergeInfo(a);
    }

    public Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        return this.delegate.findCreatorAnnotation(config, a);
    }

    @Deprecated
    public boolean hasCreatorAnnotation(Annotated a) {
        return this.delegate.hasCreatorAnnotation(a);
    }

    @Deprecated
    public Mode findCreatorBinding(Annotated a) {
        return this.delegate.findCreatorBinding(a);
    }

    @Deprecated
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return this.delegate.hasAnySetterAnnotation(am);
    }

}
