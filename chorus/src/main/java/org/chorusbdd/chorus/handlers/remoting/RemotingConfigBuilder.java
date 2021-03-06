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
package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.handlers.util.config.AbstractHandlerConfigBuilder;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfigBuilder;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/09/12
 * Time: 08:51
 */
public class RemotingConfigBuilder extends AbstractHandlerConfigBuilder implements HandlerConfigBuilder<RemotingConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingConfigBuilder.class);

    public RemotingConfig createConfig(Properties p, Properties defaults) {
        RemotingConfig r = new RemotingConfig();
        setProperties(defaults, r);
        setProperties(p, r);
        return r;
    }

    private void setProperties(Properties p, RemotingConfig r) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();

            if ("name".equals(key)) {
                r.setName(value);
            } else if ("protocol".equals(key)) {
                r.setProtocol(value);
            } else if ("host".equals(key)) {
                r.setHost(value);
            } else if ("port".equals(key)) {
                r.setPort(parseIntProperty(value, "port"));
            } else if ("connectionAttempts".equals(key)) {
                r.setConnnectionAttempts(parseIntProperty(value, "connectionAttempts"));
            } else if ("connectionAttemptMillis".equals(key)) {
                r.setConnectionAttemptMillis(parseIntProperty(value, "connectionAttemptMillis"));
            } else if ( "connection".equals(key)) {
                String[] vals = String.valueOf(value).split(":");
                if (vals.length != 3) {
                    throw new ChorusException(
                        "Could not parse remoting property 'connection', " +
                        "expecting a value in the form protocol:host:port"
                    );
                }
                r.setProtocol(vals[0]);
                r.setHost(vals[1]);
                r.setPort(parseIntProperty(vals[2], "connection:port"));
            } else {
                log.warn("Ignoring property " + key + " which is not a supported Remoting handler property");
            }
        }
    }

}
