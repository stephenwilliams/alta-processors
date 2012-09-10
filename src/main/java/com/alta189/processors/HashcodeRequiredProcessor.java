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
import javax.lang.model.util.ElementFilter;
import java.util.Set;

import com.alta189.annotations.HashcodeRequired;

public class HashcodeRequiredProcessor extends AnnotationProcessor {
	public HashcodeRequiredProcessor(ParentProcessor parent) {
		super(parent);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(HashcodeRequired.class))) {
			if (!hasHashcode(type)) {
				error("Class does not override hashcode method", type);
			}
		}
		return false;
	}

	public boolean hasHashcode(TypeElement type) {
		for (ExecutableElement method : ElementFilter.methodsIn(type.getEnclosedElements())) {
			if (method.getSimpleName().toString().equals("hashcode") && method.getParameters().size() == 0 && method.getReturnType().toString().equals("int")) {
				return true;
			}
		}
		return false;
	}
}