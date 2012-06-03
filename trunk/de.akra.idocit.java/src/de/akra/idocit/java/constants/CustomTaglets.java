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
package de.akra.idocit.java.constants;

public class CustomTaglets
{
	public static final String PARAM_INFO = "@paraminfo";
	public static final String RETURN_INFO = "@returninfo";
	public static final String THROWS_INFO = "@throwsinfo";
	
	public static final String SUB_RETURN = "@subreturn";
	public static final String SUB_RETURN_INFO = "@subreturninfo";
	public static final String SUB_PARAM = "@subparam";
	public static final String SUB_PARAM_INFO = "@subparaminfo";
	public static final String SUB_THROWS_INFO = "@subthrowsinfo";
	public static final String THEMATIC_GRID = "@thematicgrid";
	
	public static final String SUB_RETURN_PATTERN = SUB_RETURN + "\\s*";
	public static final String SUB_PARAM_PATTERN = SUB_PARAM + "\\s*";
	public static final String THEMATIC_GRID_PATTERN = THEMATIC_GRID + "\\s*";
}
