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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alta189.annotations.RequiresParameters;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("com.alta189.annotations.RequiresParameters")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RequiresParametersProcessor extends GenericProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (ExecutableElement method : ElementFilter.methodsIn(roundEnv.getElementsAnnotatedWith(RequiresParameters.class))) {
			RequiresParameters requiresParameters = method.getAnnotation(RequiresParameters.class);
			if (requiresParameters != null) {
				if (!hasParameters(method, requiresParameters)) {
					error("missing required method parameters", method);
				}
			}
		}
		return true;
	}

	public boolean hasParameters(ExecutableElement method, RequiresParameters requiresParameters) {
		List<TypeMirror> parameters = getTypeMirrors(requiresParameters);
		if (parameters != null && parameters.size() > 0) {
			if (method.getParameters().size() == parameters.size()) {
				boolean matches = true;
				for (int i = 0; i < parameters.size(); i++) {
					if (!parameters.get(i).equals(method.getParameters().get(i).asType())) {
						matches = false;
					}
				}
				return matches;
			}
		}
		return false;
	}

	public List<TypeMirror> getTypeMirrors(RequiresParameters parameters) {
		List<TypeMirror> value = new LinkedList<TypeMirror>();
		if (parameters != null) {
			try {
				parameters.value();
			} catch (MirroredTypesException mte) {
				value.addAll(mte.getTypeMirrors());
			}
		}
		return value;
	}
}
