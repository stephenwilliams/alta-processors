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
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.Set;

import com.alta189.annotations.ConstructorParameters;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("com.alta189.annotations.ConstructorParameters")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ConstructorParameterProcessor extends GenericProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ConstructorParameters.class))) {
			ConstructorParameters constructorParameters = type.getAnnotation(ConstructorParameters.class);
			List<ExecutableElement> constructors = ElementFilter.constructorsIn(type.getEnclosedElements());
			for (String entry : constructorParameters.value()) {
				if (entry != null && !entry.isEmpty()) {
					String[] constrParameters = entry.split(";");
					for (int i = 0; i < constrParameters.length; i++) {
						constrParameters[i] = constrParameters[i].trim();
					}
					boolean foundConstructor = false;
					for (ExecutableElement constructor : constructors) {
						if (constructor.getParameters().size() == constrParameters.length) {
							boolean matches = true;
							for (int i = 0; i < constrParameters.length; i++) {
								if (!constrParameters[i].equals(constructor.getParameters().get(i).asType().toString())) {
									if (!("java.lang." + constrParameters[i]).equals(constructor.getParameters().get(i).asType().toString())) {
										matches = false;
									}
								}
							}
							if (matches) {
								foundConstructor = true;
							}
						}
					}
					if (!foundConstructor) {
						error("missing required constructor", type);
					}
				}
			}
		}
		return false;
	}
}
