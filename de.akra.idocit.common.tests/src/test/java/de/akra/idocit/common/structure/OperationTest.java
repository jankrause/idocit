/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

import de.akra.idocit.common.structure.impl.TestOperation;
import de.akra.idocit.common.structure.impl.TestParameter;
import de.akra.idocit.common.structure.impl.TestParameters;

/**
 * Tests for {@link Operation}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class OperationTest
{
	/**
	 * Tests {@link Operation#copy(SignatureElement)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopy() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Create an Operation, copy it and make an equals test.
		 * ******************************************************************************
		 */
		{
			Operation sourceOperation = createOperation(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Operation copiedOperation = (Operation) sourceOperation
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			boolean ret = sourceOperation.equals(copiedOperation);
			assertEquals(true, ret);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create an Operation, copy it, change identifier and make an
		 * equals test that should return false.
		 * ****************************************************************************
		 */
		{
			Operation sourceOperation = createOperation(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Operation copiedOperation = (Operation) sourceOperation
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedOperation.setIdentifier("other name");
			assertEquals(false, sourceOperation.equals(copiedOperation));
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create an Operation, copy it, change identifier from output
		 * message and make an equals test that should return false.
		 * ****************************************************************************
		 */
		{
			Operation sourceOperation = createOperation(SignatureElement.EMPTY_SIGNATURE_ELEMENT);
			Operation copiedOperation = (Operation) sourceOperation
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			copiedOperation.getOutputParameters().getParameters().get(0)
					.setIdentifier("other name");
			assertEquals(false, sourceOperation.equals(copiedOperation));
		}
	}

	private Operation createOperation(SignatureElement parent)
	{
		Operation op = new TestOperation(parent, "Operation", "Searching Operations",
				Numerus.SINGULAR);
		op.setIdentifier("findSomething");

		/*
		 * Input message
		 */
		Parameters inputParameters = new TestParameters(op, "InputMessage",
				Numerus.SINGULAR);
		inputParameters.setIdentifier("findIn");
		op.setInputParameters(inputParameters);

		Parameter paramCust = new TestParameter(inputParameters, "Part",
				Numerus.SINGULAR, true);
		paramCust.setIdentifier("Cust");
		paramCust.setDataTypeName("Customer");
		paramCust.setSignatureElementPath("findIn.Cust(Customer)");
		inputParameters.addParameter(paramCust);

		Parameter paramId = new TestParameter(paramCust, "", Numerus.SINGULAR, false);
		paramId.setIdentifier("id");
		paramId.setDataTypeName("int");
		paramId.setDocumentationChanged(true);
		paramId.setSignatureElementPath("findIn.Cust(Customer).id(int)");
		paramCust.addParameter(paramId);

		Parameter paramNameIn = new TestParameter(paramCust, "", Numerus.SINGULAR, false);
		paramNameIn.setIdentifier("name");
		paramNameIn.setDataTypeName("String");
		paramNameIn.setSignatureElementPath("findIn.Cust(Customer).name(String)");
		paramCust.addParameter(paramNameIn);

		/*
		 * Output message
		 */
		Parameters outputParameters = new TestParameters(op, "OutputMessage",
				Numerus.SINGULAR);
		outputParameters.setIdentifier("findOut");
		op.setOutputParameters(outputParameters);

		Parameter paramCustOut = new TestParameter(outputParameters, "Part",
				Numerus.SINGULAR, true);
		paramCustOut.setIdentifier("Cust");
		paramCustOut.setDataTypeName("Customer");
		paramCustOut.setSignatureElementPath("findOut.Cust(Customer)");
		outputParameters.addParameter(paramCustOut);

		Parameter paramIdOut = new TestParameter(paramCustOut, "", Numerus.SINGULAR, true);
		paramIdOut.setIdentifier("id");
		paramIdOut.setDataTypeName("int");
		paramIdOut.setSignatureElementPath("findOut.Cust(Customer).id(int)");
		paramCustOut.addParameter(paramIdOut);

		Parameter paramNameOut = new TestParameter(paramCustOut, "", Numerus.SINGULAR,
				true);
		paramNameOut.setIdentifier("name");
		paramNameOut.setDataTypeName("String");
		paramNameOut.setSignatureElementPath("findOut.Cust(Customer).name(String)");
		paramCustOut.addParameter(paramNameOut);

		Parameter test1 = new TestParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "",
				Numerus.SINGULAR, false);
		test1.setIdentifier("id");
		test1.setDataTypeName("int");
		test1.setSignatureElementPath("findOut.Cust(Customer).id(int)");

		Parameter test2 = new TestParameter(SignatureElement.EMPTY_SIGNATURE_ELEMENT, "",
				Numerus.SINGULAR, false);
		test2.setIdentifier("id");
		test2.setDataTypeName("int");
		test2.setSignatureElementPath("findOu2.Cust(Customer).id(int)");

		/*
		 * Exception messages
		 */
		List<Parameters> exceptions = new Vector<Parameters>();

		Parameters exception = new TestParameters(op, "FaultMessage", Numerus.SINGULAR);
		exception.setIdentifier("fault");
		exceptions.add(exception);

		Parameter exParam = new TestParameter(exception, "Part", Numerus.SINGULAR, false);
		exParam.setIdentifier("Exception");
		exParam.setDataTypeName("ExType");
		exParam.setSignatureElementPath("fault.Exception(ExType)");
		exception.addParameter(exParam);

		op.setExceptions(exceptions);

		return op;
	}
}
