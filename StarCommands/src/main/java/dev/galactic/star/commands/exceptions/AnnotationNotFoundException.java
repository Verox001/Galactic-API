/*
 * Copyright 2023 Galactic Star Studios
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.galactic.star.commands.exceptions;


/**
 * The exception that is thrown when there is not an annotation found when there should be one.
 */
public class AnnotationNotFoundException extends Exception {
	public AnnotationNotFoundException() {
	}

	public AnnotationNotFoundException(String message) {
		super(message);
	}

	public AnnotationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
