/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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
package de.akra.idocit.common.structure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.akra.idocit.common.services.RuleServiceTest;

/**
 * All tests.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ DocumentationTest.class, InterfaceArtifactTest.class,
		InterfaceTest.class, OperationTest.class, ParameterTest.class,
		RuleServiceTest.class })
public class AllIDocItCommonTests
{

}