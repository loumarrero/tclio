package scripting.legacy;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by lmarrero on 5/8/16.
 */
@XmlRootElement(name = "vendor")
public class Vendor {

    private String name;

    private List<FamilyRule> familyRules;

    @XmlElement(name = "family")
    public List<FamilyRule> getFamilyRules() {
        return familyRules;
    }

    public void setFamilyRules(List<FamilyRule> familyRules) {
        this.familyRules = familyRules;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
