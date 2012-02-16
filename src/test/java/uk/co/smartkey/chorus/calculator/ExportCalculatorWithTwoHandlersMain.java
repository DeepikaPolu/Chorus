package uk.co.smartkey.chorus.calculator;

import uk.co.smartkey.chorus.annotations.Handler;
import uk.co.smartkey.chorus.annotations.Step;
import uk.co.smartkey.chorus.remoting.jmx.ChorusHandlerJmxExporter;

/**
 * Used to run a simple process that can be used to test Chorus features
 *
 * Created by: Steve Neal
 * Date: 14/11/11
 */
public class ExportCalculatorWithTwoHandlersMain {

    public static void main(String[] args) throws Exception {
        //export a calculator handler

        new ChorusHandlerJmxExporter(new EchoingHandler());

        ChorusHandlerJmxExporter exporter = new ChorusHandlerJmxExporter(new CalculatorHandler());
        exporter.getStepMetadata();

        Thread.sleep(1000 * 60 * 5); //keeps process alive for 5 mins
    }

    @Handler("Echo Values")
    public static class EchoingHandler {
        @Step("^.*accept a string with value '(.*)'$")
        public void accept(String s) {
            System.out.println(s);
        }
    }
}
