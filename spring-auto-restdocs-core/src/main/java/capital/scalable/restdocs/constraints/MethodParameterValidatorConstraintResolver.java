/*
 * Copyright 2016 the original author or authors.
 *
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
 */

package capital.scalable.restdocs.constraints;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ValidatorConstraintResolver;

public class MethodParameterValidatorConstraintResolver extends ValidatorConstraintResolver
        implements MethodParameterConstraintResolver {
    @Override
    public List<Constraint> resolveForParameter(MethodParameter param) {
        List<Constraint> constraints = new ArrayList<>();
        for (Annotation annot : param.getParameterAnnotations()) {
            Class<? extends Annotation> type = annot.annotationType();
            if (type.getAnnotation(javax.validation.Constraint.class) != null) {
                Constraint constraint = createConstraint(annot, type);
                constraints.add(constraint);
            }
        }
        return constraints;
    }

    private Constraint createConstraint(Annotation annot, Class<? extends Annotation> type) {
        Map<String, Object> configuration = new HashMap();
        for (Method method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            Object value = getAnnotValue(annot, method);
            configuration.put(methodName, value);
        }
        return new Constraint(type.getName(), configuration);
    }

    private Object getAnnotValue(Annotation annot, Method method) {
        try {
            return method.invoke(annot);
        } catch (Exception e) {
            // do not fail on error, just return null as the value of the field
            return null;
        }
    }
}
