package com.lddawstk.cloudfrontlibrary;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;

public class AmazonCloudFrontClientWrapper
{
	public static AmazonCloudFrontClient cf = null;
	public static Region currentRegion = null;
	
	public AmazonCloudFrontClientWrapper()
	{
		
	}
	
	/*
	 * This function will set our AmazonClient Instance to make use of BasicSessionCredentials for Authentication.
	 * This function will allow us to call getInstance(), instance of creating new Object Instances.
	 * 
	 * @param	credentials		The credentials to use for Authentication.
	 */
	public static void setInstance(BasicSessionCredentials credentials)
	{
		if(cf == null)
		{
			cf = new AmazonCloudFrontClient(credentials);
			cf.setRegion(Region.getRegion(Regions.US_EAST_1));			//THIS IS THE DEFAULT [http://docs.amazonaws.cn/en_us/AWSSdkDocsJava/latest/DeveloperGuide/init-ec2-client.html]
		}
	}
	
	/*
	 * This function will return our AmazonClient Instance, preventing the need for new instantiation.
	 * 
	 * @return	AmazonCloudFrontClient
	 */
	public static AmazonCloudFrontClient getInstance()
	{
		return cf;
	}
	
	/*
	 * This function will set our AWS Region, and store the information locally.
	 * 
	 * @param	myRegion		The region to make use of.
	 */
	public static void setRegion(Regions myRegion)
	{
		currentRegion = Region.getRegion(myRegion);
		cf.setRegion(Region.getRegion(myRegion));
	}

}
