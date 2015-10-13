package com.lddawstk.ec2library;

public class EC2SecurityGroupDSO
{
	public String groupId;
	public String groupName;
	public String description;
	public String vpcId;
	public String tags;
	
	public EC2SecurityGroupDSO()
	{
		this.groupId = "";
		this.groupName = "";
		this.description = "";
		this.vpcId = "";
		this.tags = "";
	}
}
