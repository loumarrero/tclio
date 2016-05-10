package scripting.tcl;

import java.io.Serializable;


@SuppressWarnings("serial")
public class TclSource implements Serializable {
    private String sourceCode;
    private String originalSourceCode = null; //keeps the inital version of the TCLized code
    private SourceMetrics sourceMetrics;
    private String originalScript;

    public TclSource(String originalScript) {
        this.originalScript = originalScript;
        sourceMetrics = new SourceMetrics();
        sourceMetrics.resetCounts();
    }


    /**
     * @return Returns the sourceCode.
     */
    public String getSourceCode() {
        return (this.sourceCode);
    }

    /**
     * @param sourceCode The sourceCode to set.
     */
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
        if (this.originalSourceCode == null) {
            this.setOriginalSourceCode(sourceCode);
        }
    }

    /**
     * @return Returns the sourceMetrics.
     */
    public SourceMetrics getSourceMetrics() {
        return (this.sourceMetrics);
    }

    /**
     * @return Returns the originalScript.
     */
    public String getOriginalScript() {
        return (this.originalScript);
    }

    /**
     * @param originalScript The originalScript to set.
     */
    public void setOriginalScript(String originalScript) {
        this.originalScript = originalScript;
    }

    public String getOriginalSourceCode() {
        return originalSourceCode;
    }

    public void setOriginalSourceCode(String originalSourceCode) {
        this.originalSourceCode = originalSourceCode;
    }

    public static class SourceMetrics implements Serializable {
        private int lineCount = -1;
        private int emptyLineCount = -1;
        private int characterCount = -1;

        /**
         * @return Returns the lineCount.
         */
        public int getLineCount() {
            return (this.lineCount);
        }

        /**
         * @param lineCount The lineCount to set.
         */
        public void setLineCount(int lineCount) {
            this.lineCount = lineCount;
        }

        public void incrementLineCount() {
            lineCount = lineCount + 1;
        }

        /**
         * @return Returns the emptyLineCount.
         */
        public int getEmptyLineCount() {
            return (this.emptyLineCount);
        }

        /**
         * @param emptyLineCount The emptyLineCount to set.
         */
        public void setEmptyLineCount(int emptyLineCount) {
            this.emptyLineCount = emptyLineCount;
        }

        /**
         * Also increments <code>lineCount</code>. So do not increment line count twice.
         */
        public void incrementEmptyLineCount() {
            emptyLineCount = emptyLineCount + 1;
            incrementLineCount();
        }


        /**
         * @return Returns the character count.
         */
        public int getCharacterCount() {
            return (this.characterCount);
        }

        /**
         * @param characterCount The characterCount to set.
         */
        public void setCharacterCount(int characterCount) {
            this.characterCount = characterCount;
        }

        void resetCounts() {
            lineCount = 0;
            emptyLineCount = 0;
            characterCount = 0;
        }
    }
}