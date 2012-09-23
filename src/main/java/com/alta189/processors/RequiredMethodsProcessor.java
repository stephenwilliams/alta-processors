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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alta189.annotations.Method;
import com.alta189.annotations.RequiredMethods;

public class RequiredMethodsProcessor extends AnnotationProcessor {
	public RequiredMethodsProcessor(ParentProcessor parent) {
		super(parent);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(RequiredMethods.class))) {
			if (!type.getKind().isInterface()) {
				checkRequiredMethods(type);
			}
		}
		for (TypeElement annotation : annotations) {
			RequiredMethods requiredMethods = annotation.getAnnotation(RequiredMethods.class);
			if (requiredMethods != null) {
				for (TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(annotation))) {
					if (!type.getKind().isInterface()) {
						checkRequiredMethods(type, requiredMethods);
					}
				}
			}
		}
		return false;
	}

	public void checkRequiredMethods(TypeElement type) {
		RequiredMethods requiredMethods = type.getAnnotation(RequiredMethods.class);
		checkRequiredMethods(type, requiredMethods);
	}

	public void checkRequiredMethods(TypeElement type, RequiredMethods requiredMethods) {
		for (Method method : requiredMethods.value()) {
			if (!hasMethod(type, method)) {
				error("missing required method: " + toString(method), type);
			}
		}
	}

	public boolean hasMethod(TypeElement type, Method method) {
		for (ExecutableElement ee : ElementFilter.methodsIn(type.getEnclosedElements())) {
			if (ee.getSimpleName().toString().equals(method.name())) {
				if (checkModifiers(ee, method)) {
					if (ee.getReturnType().equals(getReturnElement(method))) {
						List<TypeMirror> parameters = getParameters(method);
						if (parameters.size() == ee.getParameters().size()) {
							boolean matches = true;
							for (int i = 0; i < parameters.size(); i++) {
								if (!parameters.get(i).equals(ee.getParameters().get(i).asType())) {
									matches = false;
								}
							}
							if (matches) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean checkModifiers(ExecutableElement ee, Method method) {
		if (ee.getModifiers().size() == method.modifiers().length) {
			List<javax.lang.model.element.Modifier> modifiers = Arrays.asList(method.modifiers());

			for (javax.lang.model.element.Modifier modifier : ee.getModifiers()) {
				if (!modifiers.contains(modifier)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public TypeMirror getReturnElement(Method method) {
		try {
			method.returnType();
		} catch (MirroredTypeException e) {
			return e.getTypeMirror();
		}
		return null;
	}

	public List<TypeMirror> getParameters(Method method) {
		List<TypeMirror> value = new LinkedList<TypeMirror>();
		try {
			method.parameters();
		} catch (MirroredTypesException e) {
			value.addAll(e.getTypeMirrors());
		}
		return value;
	}

	public int getModifier(Method method) {
		int value = 0;
		for (javax.lang.model.element.Modifier modifier : method.modifiers()) {
			switch (modifier) {
				case PUBLIC:
					value += Modifier.PUBLIC;
					break;
				case PROTECTED:
					value += Modifier.PROTECTED;
					break;
				case PRIVATE:
					value += Modifier.PRIVATE;
					break;
				case ABSTRACT:
					value += Modifier.ABSTRACT;
					break;
				case STATIC:
					value += Modifier.STATIC;
					break;
				case FINAL:
					value += Modifier.FINAL;
					break;
				case TRANSIENT:
					value += Modifier.TRANSIENT;
					break;
				case VOLATILE:
					value += Modifier.VOLATILE;
					break;
				case SYNCHRONIZED:
					value += Modifier.SYNCHRONIZED;
					break;
				case NATIVE:
					value += Modifier.NATIVE;
					break;
				case STRICTFP:
					value += Modifier.STRICT;
					break;
			}
		}
		return value;
	}

	public String toString(Method method) {
		StringBuilder builder = new StringBuilder();
		if (method.modifiers().length > 0) {

			builder.append(Modifier.toString(getModifier(method)))
					.append(" ");
		}

		TypeMirror returnType = getReturnElement(method);
		builder.append(returnType.toString())
				.append(" ");

		builder.append(method.name())
				.append("(");

		List<TypeMirror> parameters = getParameters(method);

		Iterator<TypeMirror> iterator = parameters.iterator();
		while (iterator.hasNext()) {
			TypeMirror param = iterator.next();
			builder.append(param.toString());
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(")");

		return builder.toString();
	}
}
