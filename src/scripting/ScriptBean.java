package scripting;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "script")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptBean {

    @XmlAttribute(name = "name")
    private String name;
    private String comments;
    private String content;
    private String contexts;
    private String creator;
    private String supprotedGroups;
    private String roles;
    private String auditLogEnabled;
    private String saveConfigOnExit;
    private String menuScope;
    private String scriptTimeout;
    private String defaultCategory;
    private String scriptOwner;
    private String rollbackScript;
    private String postprocessScript;
    private String miscXMLData;
    private String creationDate;
    private String category;
    private String vendor;

    public ScriptBean() {
    }

    public ScriptBean(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContexts() {
        return contexts;
    }

    public void setContexts(String context) {
        this.contexts = context;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSupprotedGroups() {
        return supprotedGroups;
    }

    public void setSupprotedGroups(String supprotedGroups) {
        this.supprotedGroups = supprotedGroups;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getAuditLogEnabled() {
        return auditLogEnabled;
    }

    public void setAuditLogEnabled(String auditLogEnabled) {
        this.auditLogEnabled = auditLogEnabled;
    }

    public String getSaveConfigOnExit() {
        return saveConfigOnExit;
    }

    public void setSaveConfigOnExit(String saveConfigOnExit) {
        this.saveConfigOnExit = saveConfigOnExit;
    }

    public String getMenuScope() {
        return menuScope;
    }

    public void setMenuScope(String menuScope) {
        this.menuScope = menuScope;
    }

    public String getScriptTimeout() {
        return scriptTimeout;
    }

    public void setScriptTimeout(String scriptTimeout) {
        this.scriptTimeout = scriptTimeout;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public String getScriptOwner() {
        return scriptOwner;
    }

    public void setScriptOwner(String scriptOwner) {
        this.scriptOwner = scriptOwner;
    }

    public String getRollbackScript() {
        return rollbackScript;
    }

    public void setRollbackScript(String rollbackScript) {
        this.rollbackScript = rollbackScript;
    }

    public String getPostprocessScript() {
        return postprocessScript;
    }

    public void setPostprocessScript(String postprocessScript) {
        this.postprocessScript = postprocessScript;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getMiscXMLData() {
        return miscXMLData;
    }

    public void setMiscXMLData(String miscXMLData) {
        this.miscXMLData = miscXMLData;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String toString() {
        return name;
    }
}
