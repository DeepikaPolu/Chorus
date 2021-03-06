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
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.handlers.util.config.AbstractHandlerConfig;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class ProcessesConfig extends AbstractHandlerConfig {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfig.class);

    private String name;
    private String jre = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private String jvmargs;
    private String mainclass;
    private String args;
    private boolean logging;
    private int jmxPort = -1;
    private int debugPort = -1;
    private int terminateWaitTime = 30;
    private String logDirectory;
    private boolean appendToLogs;
    private boolean createLogDir = true; //whether to auto create

    public String getGroupName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJre() {
        return jre;
    }

    public void setJre(String jre) {
        this.jre = jre;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getJvmargs() {
        return jvmargs == null ? "" : jvmargs;
    }

    public void setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
    }

    public String getMainclass() {
        return mainclass;
    }

    public void setMainclass(String mainclass) {
        this.mainclass = mainclass;
    }

    public String getArgs() {
        return args == null ? "" : args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public int getTerminateWaitTime() {
        return terminateWaitTime;
    }

    public void setTerminateWaitTime(int terminateWaitTime) {
        this.terminateWaitTime = terminateWaitTime;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public boolean isAppendToLogs() {
        return appendToLogs;
    }

    public void setAppendToLogs(boolean appendToLogs) {
        this.appendToLogs = appendToLogs;
    }

    public boolean isCreateLogDir() {
        return createLogDir;
    }

    public void setCreateLogDir(boolean createLogDir) {
        this.createLogDir = createLogDir;
    }

    public boolean isValid() {
        boolean valid = true;
        if ( name == null || name.trim().length() == 0) {
            valid = logInvalidConfig("group name was null or empty");
        } else if ( jre == null || ! new File(jre).isDirectory() ) {
            valid = logInvalidConfig("jre property is null or jre path does not exist");
        } else if ( classpath == null ) {
            valid = logInvalidConfig("classpath was null");
        } else if ( mainclass == null || mainclass.trim().length() == 0 ) {
            valid = logInvalidConfig("main class was null or empty");
        }
        return valid;
    }

    public String getValidationRuleDescription() {
        return "name, jre, classpath and mainclass must be set";
    }

    protected ChorusLog getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "ProcessesConfig{" +
                "name='" + name + '\'' +
                ", jre='" + jre + '\'' +
                ", classpath='" + classpath + '\'' +
                ", jvmargs='" + jvmargs + '\'' +
                ", mainclass='" + mainclass + '\'' +
                ", args='" + args + '\'' +
                ", logging=" + logging +
                ", jmxPort=" + jmxPort +
                ", debugPort=" + debugPort +
                ", terminateWaitTime=" + terminateWaitTime +
                '}';
    }

}
