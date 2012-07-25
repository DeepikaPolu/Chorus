package remoting.jmx.dualhandlerexport;

import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class StartAProcessMain {

    public static void main(String[] args) throws InterruptedException {

        //just make sure the next standard out appears after the 'I wait for 1 second' in the interpreter output
        Thread.sleep(250);

        //write out all the arguments so we can test them
        for (String s : args) {
            System.out.println(s);
        }

        System.out.println("Exporting the handler");
        JmxDualHandlerExportHandler handler = new JmxDualHandlerExportHandler();
        ChorusHandlerJmxExporter exporter = new ChorusHandlerJmxExporter(handler);
        exporter.export();

        JmxDualHandlerExportHandlerTwo handler2 = new JmxDualHandlerExportHandlerTwo();
        ChorusHandlerJmxExporter exporter2 = new ChorusHandlerJmxExporter(handler2);
        exporter2.export();

        Thread.sleep(10000);
    }

}