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
package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.handlers.processes.ProcessRedirector;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigurationProperty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 05/07/12
 * Time: 11:44
 *
 * Fork an interpreter process and capture the std out, std err and exit code into ChorusSelfTestResults
 */
public class ForkedRunner implements ChorusSelfTestRunner {

    public ChorusSelfTestResults runChorusInterpreter(Properties sysPropsForTest) throws Exception {
        return runForked(sysPropsForTest, "org.chorusbdd.Chorus", System.out, 30000);
    }

    public ChorusSelfTestResults runForked(Properties sysPropsForTest, String mainClass, PrintStream stdOutStream, int timeout) throws Exception {
        String jre = System.getProperty("java.home");

        sysPropsForTest.put("log4j.configuration", "org/chorusbdd/chorus/selftest/log4j-forked.xml");

        //See notes also in ProcessHandler
        //surrounding the classpath in quotes is currently breaking the classpath parsing for linux when launched via
        //Runtime.getRuntime().exec() (but it is ok from the shell)
        //I think we want to keep this in on windows, since we will more likely encounter directory names with spaces -
        //I'm worried those will break for linux although this will fix the classpath issue.
        //-so this workaround at least gets things working, but may break for folders with spaces in the name on 'nix
        boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
        String commandTxt = isWindows ?
              "%s%sbin%sjava %s -classpath \"%s\" %s %s" :
              "%s%sbin%sjava %s -classpath %s %s %s";

        String classPath = System.getProperty("java.class.path");

        String switches = getSwitches(sysPropsForTest);

        StringBuilder jvmArgs = getJvmArgs(sysPropsForTest);

        //construct a command
        String command = String.format(
              commandTxt,
              jre,
              File.separatorChar,
              File.separatorChar,
              jvmArgs,
              classPath,
              mainClass,
              switches).trim();

        System.out.println("About to run Java: " + command);

        ByteArrayOutputStream processOut = new ByteArrayOutputStream();
        ByteArrayOutputStream processErr = new ByteArrayOutputStream();

        Process process = Runtime.getRuntime().exec(command);
        ProcessRedirector outRedirector = new ProcessRedirector(process.getInputStream(), false, new PrintStream(processOut), stdOutStream);  //dumping both to out
        ProcessRedirector errRedirector = new ProcessRedirector(process.getErrorStream(), false, new PrintStream(processErr), stdOutStream);  //tend to get more consistent ordering

        Thread outThread = new Thread(outRedirector, "stdout-redirector");
        outThread.setDaemon(true);
        outThread.start();
        Thread errThread = new Thread(errRedirector, "stderr-redirector");
        errThread.setDaemon(true);
        errThread.start();

        int result = 0;
        if ( timeout > 0 ) {
            result = process.waitFor();
            //wait for logging to be flushed to output byte array asynchronously
            errThread.join(timeout);
            outThread.join(timeout);
        }

        return new ChorusSelfTestResults(
            processOut.toString("UTF-8"),
            processErr.toString("UTF-8"),
            result
        );
    }

    private String getSwitches(Properties sysPropsForTest) {
        StringBuilder sb = new StringBuilder();
        for ( Map.Entry<Object,Object> property : sysPropsForTest.entrySet()) {
            String propertyName = property.getKey().toString();
            if ( propertyName.startsWith("chorus")) {
                ConfigurationProperty c = ChorusConfigProperty.getConfigPropertyForSysProp(propertyName);
                sb.append(" ").append(c.getHyphenatedSwitch());
                sb.append(" ").append(property.getValue().toString());
            }
        }
        return sb.toString();
    }


    private StringBuilder getJvmArgs(Properties sysPropsForTest) {
        StringBuilder jvmArgs = new StringBuilder();
        for ( Map.Entry<Object,Object> property : sysPropsForTest.entrySet()) {
            //use switches instead of sys props for chorus properties when running forked
            //This tests the switches, but is also necessary since property values which include
            //spaces otherwise do not work - surrounding a sys prop value in quotes "" or '' does not
            //reliably work for both win and nix
            if ( ! property.getKey().toString().startsWith("chorus")) {
                jvmArgs.append("-D");
                jvmArgs.append(property.getKey());
                if ( ! property.getValue().equals("")) {
                    jvmArgs.append("=");
                    jvmArgs.append(property.getValue());
                }
                jvmArgs.append(" ");
            }
        }
        return jvmArgs;
    }
}
