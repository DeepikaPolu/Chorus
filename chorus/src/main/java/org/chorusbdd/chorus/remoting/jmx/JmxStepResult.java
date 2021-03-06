/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.core.interpreter.ChorusContext;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/07/12
 * Time: 08:24
 *
 * Wrap the result of implementing a step remotely - this is the object returned by the
 * step implementation method - along with the remote chorus context state
 */
public class JmxStepResult implements Serializable {

    private static final long serialVersionUID = 1;

    public static final String CHORUS_CONTEXT_FIELD = "CHORUS_CONTEXT";
    public static final String STEP_RESULT_FIELD = "STEP_RESULT";

    //Using a Map to store field data because it might help to support backwards compatibility
    //we may be able to add more fields and still deserialize earlier versions of JmxStepResult
    private Map<String, Object> fieldMap = new HashMap<String, Object>();

    public JmxStepResult(ChorusContext chorusContext, Object result) {
        fieldMap.put(CHORUS_CONTEXT_FIELD, chorusContext);
        fieldMap.put(STEP_RESULT_FIELD, result);
        if ( result != null && ! (result instanceof Serializable)) {
            throw new ChorusException(
                "The returned type of a remotely called step implementation must be Serializable, " +
                "class type was " + result.getClass()
            );
        }
    }

    public ChorusContext getChorusContext() {
        return (ChorusContext)fieldMap.get(CHORUS_CONTEXT_FIELD);
    }

    public Object getResult() {
        return fieldMap.get(STEP_RESULT_FIELD);
    }
}
