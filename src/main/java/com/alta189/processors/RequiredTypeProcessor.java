/*
 * This file is part of alta-processors.
 *
 * Copyright (c) 2012, alta189 <http://github.com/alta189/alta-processors/>
 * alta-processors is licensed under the GNU Lesser General Public License.
 *
 * alta-processors is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alta-processors is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alta189.processors;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alta189.annotations.RequiredType;

public class RequiredTypeProcessor extends AnnotationProcessor {
	public RequiredTypeProcessor(ParentProcessor parent) {
		super(parent);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (VariableElement field : ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(RequiredType.class))) {
			checkRequiredType(field);
		}
		for (TypeElement annotation : annotations) {
			RequiredType requiredType = annotation.getAnnotation(RequiredType.class);
			if (requiredType != null) {
				for (VariableElement field : ElementFilter.fieldsIn(roundEnv.getElementsAnnotatedWith(annotation))) {
					checkRequiredType(field, requiredType);
				}
			}
		}
		return true;
	}

	public void checkRequiredType(VariableElement field) {
		checkRequiredType(field, field.getAnnotation(RequiredType.class));
	}

	public void checkRequiredType(VariableElement field, RequiredType requiredType) {
		List<TypeMirror> types = getRequriedType(requiredType);
		if (!types.contains(field.asType())) {
			StringBuilder builder = new StringBuilder();
			builder.append("wrong type; correct type is any one of the following: ");

			Iterator<TypeMirror> iterator = types.iterator();
			while (iterator.hasNext()) {
				TypeMirror type = iterator.next();
				builder.append(type.toString());
				if (iterator.hasNext()) {
					builder.append(", ");
				}
			}

			error(builder.toString(), field);
		}
	}

	public List<TypeMirror> getRequriedType(RequiredType requiredType) {
		List<TypeMirror> value = new LinkedList<TypeMirror>();
		try {
			requiredType.value();
		} catch (MirroredTypesException e) {
			value.addAll(e.getTypeMirrors());
		}
		return value;
	}
}
