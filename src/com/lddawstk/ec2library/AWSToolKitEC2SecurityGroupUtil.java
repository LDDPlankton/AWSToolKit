package com.lddawstk.ec2library;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AWSToolKitEC2SecurityGroupUtil
{
	public AWSToolKitEC2SecurityGroupUtil()
	{
		
	}
	
	/*
	 * This function will generate a Local IP Range [our.ip/16] to be used to assign to a security group.
	 * 
	 * @return	String
	 */
	public static String generateLocalIPRange()
	{
        String ipAddr = "0.0.0.0/0";

        try
        {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getHostAddress()+"/16";
        }
        catch (UnknownHostException e)
        {
        }
        return ipAddr;
	}
	
	
}
