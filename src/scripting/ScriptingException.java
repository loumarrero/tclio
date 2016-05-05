package scripting;

public class ScriptingException extends Exception
{
    private int errorCode = Integer.valueOf(StatusType.UNKNOWN_ERROR.getStatusCode());
    private Throwable originalException;

    public ScriptingException()
    {
        super();
    }

    public ScriptingException(String msg)
    {
        super(msg);
    }

    public ScriptingException(String msg, Throwable originalException)
    {
        super(msg);
        this.originalException = originalException;
    }

    public ScriptingException(String msg, int errorCode)
    {
        super(msg);
        this.errorCode = errorCode;
    }

    public ScriptingException(String msg, int errorCode, Throwable originalException)
    {
        super(msg);
        this.errorCode = errorCode;
        this.originalException = originalException;
    }


    /**
     * @return Returns the errorCode.
     */
    public int getErrorCode()
    {
        return (this.errorCode);
    }

    /**
     * @param errorCode The errorCode to set.
     */
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * @return Returns the originalException.
     */
    public Throwable getOriginalException()
    {
        return (this.originalException);
    }

    /**
     * @param originalException The originalException to set.
     */
    public void setOriginalException(Throwable originalException)
    {
        this.originalException = originalException;
    }
}