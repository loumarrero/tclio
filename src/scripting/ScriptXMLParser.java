package scripting;

import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScriptXMLParser<T> extends DefaultHandler{

    private T bean;
    private Class<T> beanClass;
    private StringBuilder temp;
    private String startElemString;
    private String startElemAttrName;
    private String rootElem;
    private List<T> beans = new ArrayList<T>();

    public ScriptXMLParser(Class<T> clazz,String elem,String idName){
        beanClass = clazz;
        startElemString = elem;
        startElemAttrName = idName;
    }
  
    protected T newInstance(String name) {
        T bean = null;
        try {
            bean  = beanClass.getConstructor(String.class).newInstance(name);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return bean;
    }
    
    /** The main method sets things up for parsing */
    public void loadBeans(File folder,FilenameFilter filter) throws IOException, SAXException,
            ParserConfigurationException {

        //Create a "parser factory" for creating SAX parsers
        SAXParserFactory spfac = SAXParserFactory.newInstance();

        //Now use the parser factory to create a SAXParser object
        SAXParser sp = spfac.newSAXParser();

        temp = new StringBuilder();
        
        
        File[] listOfFiles = folder.listFiles(filter);
        for(File file: listOfFiles){
            System.out.println("Parsing: "+file.getName());
            sp.parse(file,this);      
        }

    }


    public void loadBeans(File file) throws IOException, SAXException,
            ParserConfigurationException {

        //Create a "parser factory" for creating SAX parsers
        SAXParserFactory spfac = SAXParserFactory.newInstance();

        //Now use the parser factory to create a SAXParser object
        SAXParser sp = spfac.newSAXParser();

        temp = new StringBuilder();

        System.out.println("Parsing: "+file.getName());
        sp.parse(file,this);
    }

    /*
     * When the parser encounters plain text (not XML elements),
     * it calls(this method, which accumulates them in a string buffer
     */
    public void characters(char[] buffer, int start, int length) {
        //temp = new String(buffer, start, length);
        temp.append(buffer,start,length);
    }


    /*
     * Every time the parser encounters the beginning of a new element,
     * it calls this method, which resets the string buffer
     */
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {
        temp.delete(0,temp.length());
        if(rootElem==null){
            rootElem = qName;
        }
        if (qName.equalsIgnoreCase(startElemString)) {
            bean = newInstance(attributes.getValue(startElemAttrName));
        }
    }

    /*
     * When the parser encounters the end of an element, it calls this method
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

  
        if (qName.equalsIgnoreCase(startElemString)) {
            // add it to the list
            beans.add(bean);
        }else if(qName.equalsIgnoreCase(rootElem)) {
             // done
        }else{
            try {
                PropertyUtils.setSimpleProperty(bean,qName,temp.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    }
    
    public List<T> getBeans() {
        return beans;
    }

    
    public void dumpList() {
        System.out.println("No of  the scripts loaded  '" + beans.size()  + "'.");
        Iterator<T> it = beans.iterator();
        while (it.hasNext()) {
            Object script = it.next();
            System.out.println(script);
        }
    }

}
