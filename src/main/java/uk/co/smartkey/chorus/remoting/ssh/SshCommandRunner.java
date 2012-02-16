/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package uk.co.smartkey.chorus.remoting.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

/**
 * Created by: Steve Neal
 * Date: 13/10/11
 */
public class SshCommandRunner {

    private Log log = LogFactory.getLog(getClass());

    private final String host;
    private final String user;
    private final String password;

    public SshCommandRunner(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public int connectAndRunCommand(String command) {
        int exitCode = -1;
        ChannelExec channel = null;
        Session session = null;

        try {
            //initialise the ssh session
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, 22);

            //use username and password for authentication
            session.setUserInfo(new SimpleChorusUserInfo());
            session.connect();

            //set up the ssh channel and connect
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();
            channel.connect();

            //keep a track of the command's output
            StringBuilder outBuilder = new StringBuilder();
            StringBuilder errBuilder = new StringBuilder();

            log.debug(String.format("Connected to (%s) running command (%s)", host, command));
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outBuilder.append(new String(tmp, 0, i));
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errBuilder.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    exitCode = channel.getExitStatus();
                    break;
                }
                Thread.sleep(100);
            }


            //log the command's output and error streams
            String[] outLines = outBuilder.toString().trim().split("\\n");
            for (String outLine : outLines) {
                if (!(outLine.trim().length() == 0)) {
                    log.debug(outLine);
                }
            }
            String[] errLines = errBuilder.toString().trim().split("\\n");
            for (String errLine : errLines) {
                if (!(errLine.trim().length() == 0)) {
                    log.error(errLine);
                }
            }

            //log the command's exit code
            String exitMessage = String.format("Command (%s) finished running on (%s) with exit code %d ",
                    command, host, exitCode);

            if (exitCode == 0) {
                log.debug(exitMessage);
            } else {
                log.error(exitMessage);
            }

        } catch (Exception e) {
            String errorMsg = String.format("Failed to connect to (%s) and run remote command (%s)",
                    host, command);
            log.error(errorMsg, e);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }

        return exitCode;
    }

    private class SimpleChorusUserInfo implements UserInfo {
        public String getPassphrase() {
            return null;//passphrase for keystore
        }

        public String getPassword() {
            return password;
        }

        public boolean promptPassword(String s) {
            return true;//allow prompt for password
        }

        public boolean promptPassphrase(String s) {
            return false;
        }

        public boolean promptYesNo(String s) {
            //we can trust all hosts we connect to
            return s.contains("authenticity of host");
        }

        public void showMessage(String s) {
            System.out.println("message from remote server = " + s);
        }
    }

    public static void main(String[] args) {
        SshCommandRunner commandRunner = new SshCommandRunner("fftlrt9140.de.dresdnerkb.com", "ga2neil", "c0mputer");
        commandRunner.connectAndRunCommand("ls -l /");
    }
}
