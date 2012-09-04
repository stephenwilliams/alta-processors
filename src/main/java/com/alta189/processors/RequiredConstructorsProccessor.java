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

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alta189.annotations.Constructor;
import com.alta189.annotations.RequiredConstructors;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RequiredConstructorsProccessor extends GenericProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(RequiredConstructors.class))) {
			if (type.getKind().isInterface() || type.getKind() == ElementKind.ANNOTATION_TYPE) {
				continue;
			}
			RequiredConstructors requiredConstructors = type.getAnnotation(RequiredConstructors.class);
			if (requiredConstructors != null) {
				for (Constructor constructor : requiredConstructors.value()) {
					if (!hasConstructor(type, constructor)) {
						error("missing required constructor", type);
					}
				}
			}
		}
		for (TypeElement annotation : annotations) {
			RequiredConstructors requiredConstructors = annotation.getAnnotation(RequiredConstructors.class);
			if (requiredConstructors != null) {
				for (Constructor constructor : requiredConstructors.value()) {
					for (TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(annotation))) {
						if (type.getKind().isInterface() || type.getKind() == ElementKind.ANNOTATION_TYPE) {
							continue;
						}
						if (!hasConstructor(type, constructor)) {
							error("missing required constructor", type);
						}
					}
				}
			}
		}
		return true;
	}

	private boolean hasConstructor(TypeElement element, Constructor constructor) {
		for (ExecutableElement executableElement : ElementFilter.constructorsIn(element.getEnclosedElements())) {
			List<TypeMirror> parameters = getTypeMirrors(constructor);
			if (parameters != null && parameters.size() == executableElement.getParameters().size()) {
				boolean matches = true;
				for (int i = 0; i < parameters.size(); i++) {
					if (!parameters.get(i).equals(executableElement.getParameters().get(i).asType())) {
						matches = false;
					}
				}
				return matches;
			}
		}
		return false;
	}

	public List<TypeMirror> getTypeMirrors(Constructor constructor) {
		List<TypeMirror> value = new LinkedList<TypeMirror>();
		if (constructor != null) {
			try {
				constructor.value();
			} catch (MirroredTypesException mte) {
				value.addAll(mte.getTypeMirrors());
			}
		}
		return value;
	}
}
