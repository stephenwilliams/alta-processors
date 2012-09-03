package com.alta189.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public abstract class GenericProcessor extends AbstractProcessor {
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
