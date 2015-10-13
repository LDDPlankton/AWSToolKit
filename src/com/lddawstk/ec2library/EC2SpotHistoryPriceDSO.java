package com.lddawstk.ec2library;

import java.util.Date;

public class EC2SpotHistoryPriceDSO
{
	public String ec2InstanceType;
	public String ec2ProductDescription;
	public String ec2SpotPrice;
	public String ec2AvailabilityZone;
	public Date ec2TimeStamp;
	
	public EC2SpotHistoryPriceDSO()
	{
		this.ec2InstanceType = "";
		this.ec2ProductDescription = "";
		this.ec2SpotPrice = "";
		this.ec2AvailabilityZone = "";
		this.ec2TimeStamp = null;
	}
	

    
}
