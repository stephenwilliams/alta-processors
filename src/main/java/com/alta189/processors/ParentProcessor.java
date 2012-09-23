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
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.MetaInfServices;

@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ParentProcessor extends AbstractProcessor {
	private Set<AnnotationProcessor> processors = new HashSet<AnnotationProcessor>();

	public ParentProcessor() {
		processors.add(new DefaultConstructor(this));
		processors.add(new RequiredConstructorsProccessor(this));
		processors.add(new RequiredParametersProcessor(this));
		processors.add(new EqualsRequiredProcessor(this));
		processors.add(new ToStringRequiredProcessor(this));
		processors.add(new HashcodeRequiredProcessor(this));
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (AnnotationProcessor processor : processors) {
			processor.process(annotations, roundEnv);
		}
		return false;
	}

	public void note(String msg, Element e) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, e);
	}

	public void warn(String msg, Element e) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, e);
	}

	public void error(String msg, Element e) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
	}
}
