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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

import com.alta189.annotations.RequiresParameters;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("com.alta189.annotations.RequiresParameters")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RequiresParametersProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (ExecutableElement e : ElementFilter.methodsIn(roundEnv.getElementsAnnotatedWith(RequiresParameters.class))) {
			RequiresParameters requiresParameters = e.getAnnotation(RequiresParameters.class);
			if (!passes(e)) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "missing the required parameters", e);
			}
		}
		return true;
	}

	public boolean passes(ExecutableElement element) {
		boolean passes = true;

		List<? extends VariableElement> parameters = element.getParameters();
		RequiresParameters requiresParameters = element.getAnnotation(RequiresParameters.class);

		if (parameters.size() == requiresParameters.value().length) {
			for (int i = 0; i < requiresParameters.value().length; i++) {
				if (!requiresParameters.value()[i].equals(parameters.get(i).asType().toString())) {
					if (!("java.lang." + requiresParameters.value()[i]).equals(parameters.get(i).asType().toString())) {
						passes = false;
					}
				}
			}
		} else {
			passes = false;
		}
		return passes;
	}
}
