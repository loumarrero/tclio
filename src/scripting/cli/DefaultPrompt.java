package scripting.cli;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "prompt")
public class DefaultPrompt {
    private String prompt;
    private String response;


    /**
     * @return Returns the prompt.
     */
    public String getPrompt() {
        return (this.prompt);
    }

    /**
     * @param prompt The prompt to set.
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * @return Returns the response.
     */
    public String getResponse() {
        return (this.response);
    }

    /**
     * @param response The response to set.
     */
    public void setResponse(String response) {
        this.response = response;
    }


    @Override
    public String toString() {
        return ("Prompt: " + prompt + " Response: " + response);
    }

}
