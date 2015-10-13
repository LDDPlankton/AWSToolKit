package com.lddawstk.interfaces;

public interface InterfaceError
{
	public void reset();
    public boolean isError();
    public void setErrorMessage(String msg);
    public String getErrorMessage();
}
