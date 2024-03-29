/*******************************************************************************
 * Copyright 2012 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
/**
 * Default predicate which returns true
 * 
 * @returns {Boolean}
 */
function always() {
	return true;
}

/**
 * Tests wether the the given role name is associated with a SignatureElement
 * with numerus SINGULAR.
 * 
 * Please note: This operation expects the variable "thematicRoleContexts" to be
 * defined and initalized with a list of objects of type ThematicRoleContext.
 * 
 * @param role
 *            The name of the thematic role to test
 * @returns TRUE if the associated SignatureElement has the numerus SINGULAR
 */
function isSingular(role) {
	return !isPlural(role);
}

/**
 * Tests wether the the given role name is associated with a SignatureElement
 * with numerus PLURAL.
 * 
 * Please note: This operation expects the variable "thematicRoleContexts" to be
 * defined and initalized with a list of objects of type ThematicRoleContext.
 * 
 * @param role
 *            The name of the thematic role to test
 * @returns TRUE if the associated SignatureElement has the numerus PLURAL
 */
function isPlural(role) {
	checkNotNull(role);

	if (thematicRoleContexts != null) {
		for ( var i = 0; i < thematicRoleContexts.size(); i++) {
			var thematicRoleContext = thematicRoleContexts.get(i);

			if ((thematicRoleContext.role.name == role)
					&& (thematicRoleContext.getNumerus().name() == "PLURAL")) {
				return true;
			}
		}
	}

	return false;
}

/**
 * Tests wether the the given role name is associated with a SignatureElement
 * with public accessible attributes / members.
 * 
 * Please note: This operation expects the variable "thematicRoleContexts" to be
 * defined and initalized with a list of objects of type ThematicRoleContext.
 * 
 * @param role
 *            The name of the thematic role to test
 * @returns TRUE if the associated SignatureElement has public accessable
 *          attributes
 */
function hasAttributes(role) {
	checkNotNull(role);

	if (thematicRoleContexts != null) {
		for ( var i = 0; i < thematicRoleContexts.size(); i++) {
			var thematicRoleContext = thematicRoleContexts.get(i);

			if ((thematicRoleContext.role.name == role)
					&& thematicRoleContext.hasPulicAccessableAttributes()) {
				return true;
			}
		}
	}

	return false;
}

/**
 * Tests wether the the given role name exists.
 * 
 * Please note: This operation expects the variable "thematicRoleContexts" to be
 * defined and initalized with a list of objects of type ThematicRoleContext.
 * 
 * @param role
 *            The name of the thematic role to test
 * @returns TRUE if the thematic with the given name is used in the list of
 *          "thematicRoleContexts"
 */
function exists(role) {
	checkNotNull(role);

	if (thematicRoleContexts != null) {
		for ( var i = 0; i < thematicRoleContexts.size(); i++) {
			var thematicRoleContext = thematicRoleContexts.get(i);

			if (thematicRoleContext.role.name == role) {
				return true;
			}
		}
	}

	return false;

}

/**
 * Prints all attributes of variable "thematicRoleContexts" to STDOUT and
 * returns TRUE.
 * 
 * Please note: This function is for test purposes only!
 * 
 * @returns {Boolean} TRUE
 */
function info() {
	for ( var attrib in thematicRoleContexts) {
		println('Found attrib ' + attrib);
	}

	return true;
}

/**
 * Checks whether the given "object" is null or not. If yes, an exception with
 * text "IllegalArgumentException: each predicate expects a value as parameter
 * and not null!" is thrown.
 * 
 * @param object
 *            The object to test
 */
function checkNotNull(object) {
	if (object == null) {
		throw "IllegalArgumentException: each predicate expects a value as parameter and not null!";
	}
}

/**
 * Checks whether the operation has the given predicate or not.
 * 
 * @param predicate
 *            The predicate to check for
 * @returns {Boolean} TRUE if the operation has the searched predicate
 */
function hasPredicate(predicate) {
	checkNotNull(predicate);

	if (thematicRoleContexts != null) {
		for ( var i = 0; i < thematicRoleContexts.size(); i++) {
			var thematicRoleContext = thematicRoleContexts.get(i);

			if (thematicRoleContext.predicate == predicate) {
				return true;
			}
		}
	}

	return false;
}