/*******************************************************************************
 * Copyright (c) 2013 -- WPI Suite
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Team 5 D13
 * 
 ******************************************************************************/

package edu.wpi.cs.wpisuitetng.modules.requirementmanager.requirement;

import edu.wpi.cs.wpisuitetng.modules.requirementmanager.models.Iteration;
import edu.wpi.cs.wpisuitetng.network.Request;
import edu.wpi.cs.wpisuitetng.network.RequestObserver;
import edu.wpi.cs.wpisuitetng.network.models.IRequest;
import edu.wpi.cs.wpisuitetng.network.models.ResponseModel;

/** An observer for a request to retrieve an iteration with the provided id
 */
public class RetrieveIterationObserver implements RequestObserver {
	
	/** Variable that contains the retrieved iteration	 */
	private Iteration iteration;
	
	/** Construct a new observer
	 */
	public RetrieveIterationObserver() {
		iteration = null;
	}

	/** Respond to a successful message from the network
	 * @param iReq the response from the server
	 */
	public void responseSuccess(IRequest iReq) {
		Request request = (Request) iReq;
		
		// get the response from the request
		ResponseModel response = request.getResponse();

		// check the response code of the request
		if (response.getStatusCode() != 200) {
			System.err.println("Received " + iReq.getResponse().getStatusCode() + " error from server: " + iReq.getResponse().getStatusMessage());
			return;
		}
		
		Iteration[] iterationArray = Iteration.fromJSONArray(response.getBody());
		iteration = iterationArray[0];
	}

	/** Respond to an  unsuccessful message from the network 
	 * @param iReq the response from the server
	 */
	public void responseError(IRequest iReq) {
		System.err.println("Received " + iReq.getResponse().getStatusCode() + " error from server: " + iReq.getResponse().getStatusMessage());
	}

	/** Respond to a failure message from the network 
	 * 
	 * @param iReq the response from the server
	 * @param exception unused
	 */
	public void fail(IRequest iReq, Exception exception) {
		System.err.println("Unable to complete request: " + exception.getMessage());
	}
	
	/** Get the iteration stored
	 * 
	 * @return the Iteration
	 */
	public Iteration getIteration(){
		return iteration;
	}
}
