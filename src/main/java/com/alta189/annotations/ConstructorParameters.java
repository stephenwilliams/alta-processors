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
package com.alta189.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any type marked with this interface must have
 * constructors with the parameter types defined
 * as the value
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConstructorParameters {
	/**
	 * Each entry in the array represents a separate constructor </ br>
	 * Example: to require a type to have two constructors one having the
	 * parameter types String and boolean and one having String, boolean, and int.
	 * You would do the following: </ br>
	 * <code>@ConstructorParameters({"String; boolean", "String; boolean; int"})</code>
	 * </ br>
	 * Multiple parameters in a constructor should be separated with <code>;</code>
	 *
	 * @return value representing required constructor(s) parameters
	 */
	String[] value();
}
