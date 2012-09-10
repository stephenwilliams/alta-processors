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
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public abstract class AnnotationProcessor {
	private final ParentProcessor parent;

	public AnnotationProcessor(ParentProcessor parent) {
		this.parent = parent;
	}

	public abstract boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

	public void note(String msg, Element e) {
		parent.note(msg, e);
	}

	public void warn(String msg, Element e) {
		parent.warn(msg, e);
	}

	public void error(String msg, Element e) {
		parent.error(msg, e);
	}
}
