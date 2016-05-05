package scripting;



import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.xmlrules.FromXmlRulesModule;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;


/**
 * <code>VendorRules</code> contains rules for the commands for the vendor specific
 * devices.
 * These rules are used while executing cli on devices from this vendor.
 */
public class VendorRules
{
    private String name;
    private List<FamilyRule> familyRules;

    public VendorRules()
    {
        familyRules = new ArrayList<FamilyRule> ();
    }

    public void addFamilyRule( FamilyRule  familyRule ) {
        familyRules.add(familyRule);
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return (this.name);
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return Returns the familyRules.
     */
    public List<FamilyRule> getFamilyRules()
    {
        return (this.familyRules);
    }

    public FamilyRule getFamilyRule(String familyStr)
    {
        for (FamilyRule item : familyRules)
        {
            if (familyStr.matches(item.getValue()))
            {
                return (item);
            }
        }
        return (null);
    }
    
    public static Map<String, VendorRules> loadRules() throws Exception {

        Map<String, VendorRules> vendorRulesMap = new HashMap<String, VendorRules>();
        
        // Configure Digester from XML ruleset
        List<VendorRules> propertiesRules = new ArrayList<VendorRules>();

        
        File mappingFile = TclInterpUtils.getDataFile("Scripting", "CommandCLIPropertyRules.xml");
        if (mappingFile == null)
        {
            throw new Exception("Data file CommandCLIPropertyRules.xml not found.");
        }

        // Create a Digester instance which has been initialised with
        // rules loaded from the specified file.
        Digester digester = newLoader(new FromXmlRulesModule() {
            @Override
            protected void loadRules()
            {
                try {
                    loadXMLRules( mappingFile.toURI().toURL() );
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        } ).newDigester();
        digester.setValidating(false);
        // Push empty List onto Digester's Stack
        digester.push(propertiesRules);

        // Parse the mapping XML document
        File rulesFile = TclInterpUtils.getDataFile("Scripting", "CommandCLIProperties.xml");
        if (rulesFile == null)
        {
            throw new Exception("Data file CommandCLIProperties.xml not found.");
        }
        digester.parse(rulesFile);

        for (VendorRules rule : propertiesRules)
        {
            vendorRulesMap.put(rule.getName().toUpperCase(), rule);
        }
    
        return vendorRulesMap;
    }
}

