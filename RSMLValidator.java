/*
Code taken from https://github.com/ghajba/Java

and adapted from a general xml/xsd validator to rsml validator
*/
//package RSML;

import static java.lang.System.out;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.SAXException;

public class RSMLValidator{
    private static Source[] xmlSources = new Source[]{};
    private static Source[] xsdSources = new Source[]{};

    private static void printUsage(){
        out.println("Usage of the application: ");
        out.println("java RSMLValidator <list of RSML files>");
        System.exit(-1);
        }

    private static void getArgumentFiles(List<String> args){
        List<Source> xmls = new ArrayList<Source>(args.size()/2);
        List<Source> xsds = new ArrayList<Source>(args.size()/2);

        // add the rsml xsd file
        try{
            Class cls = Class.forName("RSMLValidator");
            ClassLoader cLoader = cls.getClassLoader();
            InputStream i = cLoader.getResourceAsStream("rsml.xsd");
            //BufferedReader r = new BufferedReader(new InputStreamReader(i));
            xsds.add(new StreamSource(i));//new File("rsml.xsd")));
        }catch(ClassNotFoundException e){
            System.out.println(e);
            }
        
        for(String arg : args){
            xmls.add(new StreamSource(new File(arg)));
            }

        xmlSources = xmls.toArray(xmlSources);
        xsdSources = xsds.toArray(xsdSources);
        }

    public static void main(String... args){
        List<String> argsList = Arrays.asList(args);

        if(argsList.size() < 1){
            out.println("Too few parameters provided");
            printUsage();
            }

        getArgumentFiles(argsList);

        try{
            //Source xmlFile = new StreamSource(xmlSources);
            SchemaFactory schemaFactory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdSources);
            Validator validator = schema.newValidator();

            for(Source xmlFile : xmlSources){
                try{
                    validator.validate(xmlFile);
                    System.out.println(xmlFile.getSystemId() + " is valid");
                    }
                catch (SAXException e){
                    System.out.println(xmlFile.getSystemId() + " is invalid");
                    System.out.println("\tReason: " + e.getLocalizedMessage());
                    }
                catch(IOException e){
                    System.out.println(xmlFile.getSystemId() + " is invalid");
                    System.out.println("\tReason: " + e.getLocalizedMessage());
                    }
                }
            }
        catch (SAXException e){
            System.out.println("Schema creation failed:: " + e.getLocalizedMessage());
            }
        }
    }