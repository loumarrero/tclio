package util;

import scripting.ScriptBean;
import scripting.SourceMapping;
import scripting.cli.CLIRule;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JAXBLoader {

    public static boolean fileExists(String scriptName) {
        Path scriptFile = Paths.get("scripting", "bundled_scripts", "xml", scriptName);
        return scriptFile.toFile().exists();
    }

    public static <E> List<E> readList(Class<E> clazz, String file) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Wrapper.class, clazz);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        List<E> list = unmarshal(unmarshaller, clazz, file);
        return list;
    }

    public static <E> void writeList(Class<E> clazz, String name,List<E> mappings) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Wrapper.class, clazz);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshal(marshaller, mappings, name);
    }

    public static <E> E read(Class<E> clazz, String script) throws Exception {
        File xml = new File(script);
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        E obj = (E) unmarshaller.unmarshal(xml);
        return obj;
    }

    public static List<SourceMapping> loadScriptSettings() throws Exception {
        return readList(SourceMapping.class, "scripting/SyntaxConversionRules.xml");
    }

    public static void legacyCLIRules() throws Exception {
//        List<Vendor> mappings = readList(Vendor.class, "scripting/CommandCLIProperties.xml");
//
//        mappings.stream().forEach(x -> {
//            List<FamilyRule> rules = x.getFamilyRules();
//            if (rules != null) {
//                rules.stream().forEach(System.out::println);
//            }
//        });
    }

    public static void loadCLIRules() throws Exception {
        List<CLIRule> mappings = readList(CLIRule.class, "scripting/CLIRules.xml");

        mappings.stream().forEach(System.out::println);
    }

//    public static FamilyRule getCLIRule(String vendorName, String productFamily) throws Exception {
//        Optional<FamilyRule> optFamily = Optional.empty();
//        List<Vendor> mappings = readList(Vendor.class, "scripting/CommandCLIProperties.xml");
//        Optional<Vendor> optVendor = mappings.stream().filter(f -> vendorName.matches(f.getName())).findFirst();
//        if (optVendor.isPresent()) {
//            Vendor vendor = optVendor.get();
//            optFamily = vendor.getFamilyRules().stream().filter(f -> productFamily.matches(f.getValue())).findFirst();
//        }
//        return optFamily.get();
//    }

    public static CLIRule getCLIRule(String ruleName) throws Exception {
        List<CLIRule> mappings = readList(CLIRule.class, "scripting/CLIRules.xml");
        CLIRule defualtRule = null;
        CLIRule matchRule = null;
        for(CLIRule rule: mappings){
            if(rule.getName().equals("*")){
                defualtRule = rule;
            }else if(rule.getName().equals(ruleName)){
                matchRule = rule;
                break;
            }
        }
        return (matchRule == null) ? defualtRule : matchRule;
    }

    public static ScriptBean loadScript(String scriptName) throws Exception {
        Path scriptFile = Paths.get("scripting", "bundled_scripts", "xml", scriptName);
        ScriptBean bean = read(ScriptBean.class, scriptFile.toString());
        return bean;
    }

    public static void loadScripts() throws Exception {
        List<ScriptBean> beans = new ArrayList<>();
        Path scriptFolder = Paths.get("scripting", "bundled_scripts", "xml");
        try (Stream<String> lines = Files.lines(Paths.get(scriptFolder.getParent().toString(), "updated_scripts.list"))) {
            lines.forEach(s -> {
                try {
                    ScriptBean bean = read(ScriptBean.class, Paths.get(scriptFolder.toString(), s).toString());
                    beans.add(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        //long count = scriptNames.stream().count();
        System.out.println("Scriptins = " + beans.size());
    }

    private static <T> List<T> unmarshal(Unmarshaller unmarshaller,
                                         Class<T> clazz, String xmlLocation) throws JAXBException {
        StreamSource xml = new StreamSource(xmlLocation);
        Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(xml,
                Wrapper.class).getValue();
        return wrapper.getItems();
    }

    private static void marshal(Marshaller marshaller, List<?> list, String name)
            throws JAXBException {
        QName qName = new QName(name);
        Wrapper wrapper = new Wrapper(list);
        JAXBElement<Wrapper> jaxbElement = new JAXBElement<>(qName,
                Wrapper.class, wrapper);
        marshaller.marshal(jaxbElement, System.out);
    }


    public static void main(String[] args) throws Exception {
        //loadCLIRules();
        //loadScriptSettings();
        //ScriptBean bean = loadScript("Config_Syslog.xml");
        //System.out.println(bean.getContent());
        //loadScripts();
        long startTime = System.currentTimeMillis();
//        FamilyRule rule = getCLIRule("extreme","foo");
        loadScript("Config_Syslog.xml");
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time: " + endTime);
        //System.out.println(rule);
    }

    static class Wrapper<T> {

        private List<T> items;

        public Wrapper() {
            items = new ArrayList<T>();
        }

        public Wrapper(List<T> items) {
            this.items = items;
        }

        @XmlAnyElement(lax = true)
        public List<T> getItems() {
            return items;
        }

    }

}
