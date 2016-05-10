package scripting;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SourceMapping")
public class SourceMapping {
    private String id;
    private String xossyntaxexpr;
    private String tclexpr;
    private String since;
    private String casesensitive;
    private String hint;

    public SourceMapping() {
    }

    public SourceMapping(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    /**
     * @return Returns the xossyntaxexpr.
     */
    public String getXossyntaxexpr() {
        return (this.xossyntaxexpr);
    }

    /**
     * @param xossyntaxexpr The xossyntaxexpr to set.
     */
    public void setXossyntaxexpr(String xossyntaxexpr) {
        this.xossyntaxexpr = xossyntaxexpr;
    }

    /**
     * @return Returns the tclexpr.
     */
    public String getTclexpr() {
        return (this.tclexpr);
    }

    /**
     * @param tclexpr The tclexpr to set.
     */
    public void setTclexpr(String tclexpr) {
        this.tclexpr = tclexpr;
    }

    /**
     * @return Returns the casesensitive.
     */
    public String getCasesensitive() {
        return (this.casesensitive);
    }

    /**
     * @param casesensitive The casesensitive to set.
     */
    public void setCasesensitive(String casesensitive) {
        this.casesensitive = casesensitive;
    }

    /**
     * @return Returns the since.
     */
    public String getSince() {
        return (this.since);
    }

    /**
     * @param since The since to set.
     */
    public void setSince(String since) {
        this.since = since;
    }

    /**
     * @return Returns the hint.
     */
    public String getHint() {
        return (this.hint);
    }

    /**
     * @param hint The hint to set.
     */
    public void setHint(String hint) {
        this.hint = hint;
    }

    public String toString() {
        StringBuilder mappingStr = new StringBuilder();
        mappingStr.append("id = ").append(this.id).append("\n").
                append("xossyntaxexpr = ").append(this.xossyntaxexpr).append("\n").
                append("tclexpr = ").append(this.tclexpr).append("\n").
                append("since = ").append(this.since).append("\n").
                append("casesensitive = ").append(this.casesensitive).append("\n").
                append("hint = ").append(this.hint).append("\n");

        return (mappingStr.toString());
    }
}